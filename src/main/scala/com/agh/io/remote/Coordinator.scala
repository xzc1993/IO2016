package com.agh.io.remote

import akka.actor.{Actor, ActorRef, Props}
import com.agh.io.configuration.Configuration
import com.agh.io.core.{MapDrafter, RatedPosition}
import com.agh.io.map.MapLoader
import com.agh.io.sensor.SensorLoader

import scala.io.Source

class Coordinator(configuration: Configuration) extends Actor {
    val hosts = Source.fromFile(configuration.hostsFileName).getLines().toList
    val sensor = new SensorLoader(configuration.inputData.sensorDataFile).load()
    val map = new MapLoader(configuration.inputData.mapDataFile).load()
    val mapDrafter = new MapDrafter(map)

    var workers = Vector[ActorRef]()
    var results = Vector[RatedPosition]()

    override def receive = {
        case WorkerStarted =>
            workers = workers :+ sender
            if (workers.length == hosts.length) self ! StartCalculation
        case StartCalculation =>
            val circularWorkerIterator = Iterator.continually(workers).flatten
            for (i <- sensor.scans.indices) {
                val worker = circularWorkerIterator.next()
                worker ! Calculate(sensor.scans(i))
            }
        case Calculated(workerId, ratedPosition) =>
            results = results :+ ratedPosition
            if (results.length == workers.length) self ! Done
        case Done =>
            results.foreach(position => println(s"${position.position.point.x};${position.position.point.y};${position.position.angle}"))
            mapDrafter.drawPath(results)
            workers.foreach(_ ! ShutDown)
            context.system.terminate()
    }
}

object Coordinator {
    def props(configuration: Configuration) = Props(new Coordinator(configuration))
}

case object StartCalculation

case object Done