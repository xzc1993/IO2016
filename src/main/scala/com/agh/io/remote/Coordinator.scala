package com.agh.io.remote

import akka.actor.{Actor, ActorRef, Address, Deploy, Props}
import akka.remote.RemoteScope
import com.agh.io.configuration.Configuration
import com.agh.io.core.{MapDrafter, RatedPosition}
import com.agh.io.map.MapLoader
import com.agh.io.remote.Properties._
import com.agh.io.sensor.SensorLoader

import scala.io.Source

class Coordinator(configuration: Configuration) extends Actor {
    val hosts = Source.fromFile(configuration.hostsFileName).getLines().toList
    val sensor = new SensorLoader(configuration.inputData.sensorDataFile).load()
    val map = new MapLoader(configuration.inputData.mapDataFile).load()
    val mapDrafter = new MapDrafter(map)

    var workers = Vector[ActorRef]()
    var results = Vector[RatedPosition]()

    override def preStart() = {
        val circularHostIterator = Iterator.continually(hosts).flatten
        for (i <- sensor.scans.indices) {
            val next = circularHostIterator.next()
            val address = Address(Protocol, WorkerSystemName, next, WorkerSystemPort)
            val worker = context.actorOf(Worker.props(i, configuration.inputData.sensorParameters, configuration.inputData.mapDataFile).
                withDeploy(Deploy(scope = RemoteScope(address))))
            workers = workers :+ worker
            worker ! Calculate(sensor.scans(i))
        }
    }

    override def receive = {
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

case object Done