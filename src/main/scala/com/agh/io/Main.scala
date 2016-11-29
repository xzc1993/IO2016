package com.agh.io

import com.agh.io.Configuration.CommandLineParser
import com.agh.io.Core._
import com.agh.io.Map.{Map, MapLoader, Point}
import com.agh.io.Sensor.{SensorLoader, SensorScan}

import scala.collection.mutable

object Main extends App {
    val spacetimeContinuummIsInCheck: Boolean = true
    val configuration = new CommandLineParser().load(args)
    val map = new MapLoader(configuration.mapDataFile).load()
    val sensor = new SensorLoader(configuration.sensorDataFile).load()
    val fitnessCalculator = new FitnessCalculator(configuration.sensorParameters)
    val clusterer = new Clusterer(map)

    val AcceptableGuessThreshold = 1.1

    println(s"Starting computation for reading #${configuration.nodeId}")
    println

    val sensorScan = sensor.scans(configuration.nodeId)
    var guessNo = 0
    var bestPosition = RatedPosition(Position(Point(0, 0), 0), Double.PositiveInfinity)

    val guessesByWorst = new mutable.PriorityQueue[RatedPosition]

    while (spacetimeContinuummIsInCheck) {
        guessNo += 1
        val position = PositionRandomizer.getRandomPositionOnMap(map)
        val fitness = fitnessCalculator.calculateFitness(map, position, sensorScan)
        val ratedPosition = RatedPosition(position, fitness)
        if (ratedPosition.fitness < AcceptableGuessThreshold * bestPosition.fitness) {
            guessesByWorst += ratedPosition
            if (ratedPosition.fitness < bestPosition.fitness) {
                bestPosition = ratedPosition
                while (guessesByWorst.head.fitness >= AcceptableGuessThreshold * bestPosition.fitness) {
                    guessesByWorst.dequeue()
                }
            }
        }
        if (guessNo % 10000 == 0) {
            println(f"guess: $guessNo%9d, acceptable guess count: ${guessesByWorst.length}%6d")
            println

            guessesByWorst.foreach(guess => printPositionStats(map, guess, sensorScan))
            println

            val errors = fitnessCalculator.calculateDistanceReadingErrors(map, bestPosition.position, sensorScan)
            println(s"best guess errors: ${errors.map(e => f"$e%6.1f").mkString(", ")}")
            println

            val clusters = clusterer.cluster(guessesByWorst.toSeq)
            clusters.foreach(c => println(f"cluster (${c.positions.size}%2d): ${c.positions.map(_.position).mkString(", ")}"))
            println
            println("-------------------------------------------------------------------------------------------")
            println
        }
    }

    private def printPositionStats(map: Map, position: RatedPosition, sensorScan: SensorScan) = {
        val errorStats = fitnessCalculator.calculateErrorStats(map, position.position, sensorScan)
        println(f"position: (${position.position.position.x}%6.1f, ${position.position.position.y}%6.1f), " +
            f"angle: ${position.position.angle}%6.2f, fitness: ${position.fitness}%9.1f, " +
            f"min error: ${errorStats.min}%6.1f, max error: ${errorStats.max}%6.1f, " +
            f"mean error: ${errorStats.mean}%6.1f, error stddev: ${errorStats.stdDev}%6.1f")
    }
}