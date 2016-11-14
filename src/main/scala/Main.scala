package com.agh.io

import com.agh.io.Configuration.CommandLineParser
import com.agh.io.Core.{FitnessCalculator, PositionRandomizer}
import com.agh.io.Map.MapLoader
import com.agh.io.Sensor.SensorLoader

object Main extends App {
    val spacetimeContinuummIsInCheck: Boolean = true
    val configuration = new CommandLineParser().load(args)
    val map = new MapLoader(configuration.mapDataFile).load()
    val sensor = new SensorLoader(configuration.sensorDataFile).load()
    println(s"Hello World from node ${configuration.nodeId}")

    while(spacetimeContinuummIsInCheck){
        var position = PositionRandomizer.getRandomPositionOnMap(map)
        print(s"Calculating fitness for position: (${position.position.x},${position.position.y}) and angle ${position.angle}... ")
        println(s"Done. Result: ${FitnessCalculator.calculateFitness(map, position, sensor.readings(0))}")
    }

}