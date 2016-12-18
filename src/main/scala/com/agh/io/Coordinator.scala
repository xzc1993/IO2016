package com.agh.io

import java.io.{File, PrintWriter}

import akka.actor.{Actor, Address, Deploy, Props}
import akka.remote.RemoteScope
import com.agh.io.configuration.Configuration
import com.agh.io.core.{MapDrafter, RatedPosition}
import com.agh.io.map.MapLoader
import com.agh.io.sensor.{Sensor, SensorLoader}

class Coordinator(configuration: Configuration) extends Actor {
    val sensor = Sensor(new SensorLoader(configuration.inputData.sensorDataFile).load().scans.slice(0, 3))
    val map = new MapLoader(configuration.inputData.mapDataFile).load()
    val mapDrafter = new MapDrafter(map)

    var results = Vector[RatedPosition]()
    val pw = new PrintWriter(new File("results.txt"))

    override def preStart() = {
        for (i <- sensor.scans.indices) {
            val hostname = configuration.hostname
            val address = Address("akka.tcp", "WorkerSystem", hostname, 2554)
            val worker = context.actorOf(Props(new Worker(i, configuration.inputData.sensorParameters, configuration.inputData.mapDataFile)).
                withDeploy(Deploy(scope = RemoteScope(address))))
            worker ! Calculate(sensor.scans(i))
        }
    }

    override def receive = {
        case Calculated(workerId, ratedPosition) =>
            results = results :+ ratedPosition
            pw.write(s"${ratedPosition.position.point.x},${ratedPosition.position.point.y}")
            pw.flush()
            if (results.length == sensor.scans.length) self ! Done
        case Done =>
            mapDrafter.drawPath(results)
            pw.close()
    }
}

case object Done