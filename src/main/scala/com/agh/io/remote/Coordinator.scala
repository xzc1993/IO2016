package com.agh.io.remote

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
    val hosts = Source.fromFile(configuration.hostsFileName).getLines().toList
    val sensor = new SensorLoader(configuration.inputData.sensorDataFile).load()
    val map = new MapLoader(configuration.inputData.mapDataFile).load()
    val mapDrafter = new MapDrafter(map)

    var workers = Vector[ActorRef]()
    val results = mutable.Map[Int, RatedPosition]()

    override def receive = {
        case WorkerStarted =>
            workers = workers :+ sender
            if (workers.length == hosts.length) self ! StartCalculation
        case StartCalculation =>
            val circularWorkerIterator = Iterator.continually(workers).flatten
            for (i <- sensor.scans.indices) {
                val worker = circularWorkerIterator.next()
                worker ! Calculate(sensor.scans(i), i)
            }
        case Calculated(ratedPosition, id) =>
            results.put(id, ratedPosition)
            if (results.size == sensor.scans.length) self ! Done
        case Done =>
            val positions = results.toSeq.sortBy(_._1).map(_._2)
            val positionPredictions = calculatePositionPredictions(positions)
            positions.foreach(position => println(s"${position.position.point.x};${position.position.point.y};${position.position.angle}"))
            mapDrafter.drawPath(positions.zip(positionPredictions).map(RatedPositionWithPrediction.tupled(_)))
            workers.foreach(_ ! ShutDown)
            context.system.terminate()
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