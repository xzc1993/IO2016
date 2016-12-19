package com.agh.io.remote

import akka.actor.{Actor, Props}
import com.agh.io.configuration.Configuration
import com.agh.io.core.PositionCalculator
import com.agh.io.map.MapLoader
import com.agh.io.remote.Properties._

class Worker(configuration: Configuration) extends Actor {
    val map = new MapLoader(configuration.inputData.mapDataFile).load()

    override def preStart() = {
        val coordinatorAddress = s"$Protocol://$CoordinatorSystemName@${configuration.coordinatorHostname}:$CoordinatorSystemPort/user/$CoordinatorActorName"
        val coordinator = context.actorSelection(coordinatorAddress)
        coordinator ! WorkerStarted
    }

    override def receive = {
        case Calculate(sensorScan) =>
            val positionCalculator = new PositionCalculator(sensorScan, configuration.inputData.sensorParameters, map)
            val ratedPosition = positionCalculator.run()
            sender ! Calculated(configuration.nodeId, ratedPosition)
        case ShutDown =>
            context.system.terminate()
    }
}

object Worker {
    def props(configuration: Configuration) = Props(new Worker(configuration))
}