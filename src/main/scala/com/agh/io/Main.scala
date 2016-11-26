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

    val sensorScan = sensor.scans(0)
    var guessNo = 0
    var bestFitness = Double.PositiveInfinity

    while (spacetimeContinuummIsInCheck) {
        guessNo += 1
        val position = PositionRandomizer.getRandomPositionOnMap(map)
        val fitness = fitnessCalculator.calculateFitness(map, position, sensorScan)
        if (fitness < bestFitness) {
            bestFitness = fitness
            val errorStats = fitnessCalculator.calculateErrorStats(map, position, sensorScan)
            println(f"guess: $guessNo%9d, position: (${position.position.x}%6.1f, ${position.position.y}%6.1f), angle: ${position.angle}%6.2f, " +
                f"fitness: $fitness%9.1f, min. error: ${errorStats.min}%6.1f, max. error: ${errorStats.max}%6.1f, " +
                f"mean error: ${errorStats.mean}%6.1f, error stddev: ${errorStats.stdDev}%6.1f")
        }
    }
}