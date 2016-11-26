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
    val fitnessCalculator = new FitnessCalculator(configuration.sensorParameters)

    println(s"Hello World from node ${configuration.nodeId}")

    var guessNo = 0
    var bestFitness = Double.PositiveInfinity

    while(spacetimeContinuummIsInCheck){
        guessNo += 1
        val position = PositionRandomizer.getRandomPositionOnMap(map)
        val fitness = fitnessCalculator.calculateFitness(map, position, sensor.scans(0))
        if (fitness < bestFitness) {
            bestFitness = fitness
            println(f"guess: $guessNo%9d, position: (${position.position.x}%6.1f, ${position.position.y}%6.1f), angle: ${position.angle}%6.2f, fitness: $fitness%9.1f")
        }
    }
}