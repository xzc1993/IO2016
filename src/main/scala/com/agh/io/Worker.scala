package com.agh.io

import java.io.File

import akka.actor.{Actor, Props}
import com.agh.io.configuration.SensorParameters
import com.agh.io.core.PositionCalculator
import com.agh.io.map.MapLoader

class Worker(id: Int, sensorParameters: SensorParameters, mapDataFile: File) extends Actor {
    val map = new MapLoader(mapDataFile).load()

    override def receive = {
        case Calculate(sensorScan) =>
            val positionCalculator = new PositionCalculator(sensorScan, sensorParameters, map)
            val ratedPosition = positionCalculator.run()
            sender ! Calculated(id, ratedPosition)
        case ShutDown =>
            context.system.terminate()
    }
}

object Worker {
    def props(id: Int, sensorParameters: SensorParameters, mapDataFile: File) = Props(new Worker(id, sensorParameters, mapDataFile))
}