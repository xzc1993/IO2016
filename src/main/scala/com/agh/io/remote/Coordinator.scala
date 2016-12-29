package com.agh.io.remote

import java.io.{File, PrintWriter}
import java.time.temporal.ChronoUnit

import akka.actor.{Actor, ActorRef, Props}
import com.agh.io.configuration.Configuration
import com.agh.io.core.{Position, RatedPosition, RatedPositionWithPrediction, RobotMotionModelPositionCalculator}
import com.agh.io.map.MapLoader
import com.agh.io.output.MapDrafter
import com.agh.io.sensor.SensorLoader

import scala.collection.mutable
import scala.io.Source

class Coordinator(configuration: Configuration) extends Actor {
    val hosts = Source.fromFile(configuration.hostsFile).getLines().toList
    val sensor = new SensorLoader(configuration.inputData.sensorDataFile).load()
    val map = new MapLoader(configuration.inputData.mapDataFile).load()
    val mapDrafter = new MapDrafter(map, configuration.drawingFile)

    var workers = Vector[ActorRef]()
    val results = mutable.Map[Int, RatedPosition]()

    override def receive = {
        case WorkerStarted =>
            workers = workers :+ sender
            if (workers.length == hosts.length) self ! StartCalculation
        case StartCalculation =>
            val circularWorkerIterator = Iterator.continually(workers).flatten
            for (i <- configuration.rangeStart to (configuration.rangeEnd - 1)) {
                val worker = circularWorkerIterator.next()
                worker ! Calculate(sensor.scans(i), i)
            }
        case Calculated(ratedPosition, id) =>
            results.put(id, ratedPosition)
            if (results.size == (configuration.rangeEnd - configuration.rangeStart)) self ! Done
        case Done =>
            val positions = results.toSeq.sortBy(_._1).map(_._2)
            val positionPredictions = calculatePositionPredictions(positions)
            val positionsWithPredictions = positions.zip(positionPredictions).map(RatedPositionWithPrediction.tupled(_))

            saveDataToFile(positionsWithPredictions)
            mapDrafter.drawPath(positionsWithPredictions)
            workers.foreach(_ ! ShutDown)
            context.system.terminate()
    }

    def saveDataToFile(positions: Seq[RatedPositionWithPrediction]) = {
        val writer = new PrintWriter(new File(s"result_${configuration.rangeStart}_${configuration.rangeEnd}.txt"))
        positions.foreach(position => {
            writer.write(
                f"${position.ratedPosition.position.point.x}%6.1f;${position.ratedPosition.position.point.y}%6.1f;" +
                    f"${position.ratedPosition.position.angle}%6.1f;${position.ratedPosition.fitness}%6.1f;" +
                    f"${position.nextPositionPrediction.point.x}%6.1f;${position.nextPositionPrediction.point.y}%6.1f;" +
                    f"${position.nextPositionPrediction.angle}%6.1f;${position.differenceNorm}%6.1f"
            )
        })
        writer.close()
    }
    def calculatePositionPredictions(positions: Seq[RatedPosition]): Seq[Position] = {
        positions.head.position +: positions.zip(sensor.scans.zip(sensor.scans.drop(1))).map({
            case (position, (scan, nextScan)) =>
                new RobotMotionModelPositionCalculator(position.position, scan.leftWheelVelocity, scan.rightWheelVelocity)
                    .getPositionAfterTime(scan.date.until(nextScan.date, ChronoUnit.MILLIS) / 1000.0)
        })
    }
}

object Coordinator {
    def props(configuration: Configuration) = Props(new Coordinator(configuration))
}

case object StartCalculation

case object Done