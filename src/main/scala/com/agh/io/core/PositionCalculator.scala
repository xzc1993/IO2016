package com.agh.io.core

import java.util.concurrent.{Callable, Executors, FutureTask}

import com.agh.io.configuration.SensorParameters
import com.agh.io.map.{Map, Point}
import com.agh.io.sensor.SensorScan

import scala.collection.mutable

/**
  * Created by XZC on 12/3/2016.
  */
class PositionCalculator(sensorScan: SensorScan, sensorParameters: SensorParameters, map: Map) {
    val fitnessCalculator = new FitnessCalculator(sensorScan, sensorParameters, map)
    val clusterer = new Clusterer(map)
    val annealer = new Annealer(map, fitnessCalculator)
    val cpu_cores = Runtime.getRuntime.availableProcessors()
    val pool = Executors.newFixedThreadPool(cpu_cores)

    val IterationCount = 20000
    val AcceptableGuessThreshold = 1.1

    def computeHypothesisClusters() = {
        var guessNo = 0
        var bestPosition = RatedPosition(Position(Point(0, 0), 0), Double.PositiveInfinity)

        val guessesByWorst = new mutable.PriorityQueue[RatedPosition]

        while (guessNo < IterationCount/cpu_cores) {
            guessNo += 1
            val position = PositionRandomizer.getRandomPositionOnMap(map)
            val fitness = fitnessCalculator.calculateFitness(position)
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

//            if (guessNo % 10000 == 0) { // TODO proper logging?
//                println(f"guess: $guessNo%9d, acceptable guess count: ${guessesByWorst.length}%6d")
//                println
//
//                guessesByWorst.foreach(guess => printPositionStats(map, guess, sensorScan))
//                println
//
//                val errors = fitnessCalculator.calculateDistanceReadingErrors(bestPosition.position)
//                println(s"best guess errors: ${errors.map(e => f"$e%6.1f").mkString(", ")}")
//                println
//
//                val clusters = clusterer.cluster(guessesByWorst.toSeq)
//                clusters.foreach(c => println(f"cluster (${c.positions.size}%2d, centroid: ${c.centroid}): " +
//                    s"${c.positions.map(_.position).mkString(", ")}"))
//                println
//                println("-------------------------------------------------------------------------------------------")
//                println
//            }
        }

        clusterer.cluster(guessesByWorst.toSeq)
    }

    def printPositionStats(map: Map, position: RatedPosition, sensorScan: SensorScan) = {
        val errorStats = fitnessCalculator.calculateErrorStats(position.position)
        println(f"position: (${position.position.point.x}%6.1f, ${position.position.point.y}%6.1f), " +
            f"angle: ${position.position.angle}%6.2f, fitness: ${position.fitness}%9.1f, " +
            f"min error: ${errorStats.min}%6.1f, max error: ${errorStats.max}%6.1f, " +
            f"mean error: ${errorStats.mean}%6.1f, error stddev: ${errorStats.stdDev}%6.1f")
    }

    def run(): RatedPosition = {
        val clusters = runClusterization()
        val result = runAnnealing(clusters.take(cpu_cores*2))
        pool.shutdownNow()
        result
    }

    private def runClusterization(): Seq[Cluster] = {
        val futures = (0 to cpu_cores).map(_ => {
            new FutureTask[Seq[Cluster]](new Callable[Seq[Cluster]]() {
                def call(): Seq[Cluster] = {
                    computeHypothesisClusters()
                }
            })
        }
        )
        futures.foreach(future => {
            pool.execute(future)
        })
        futures.map(future => {
            future.get()
        }).reduce((resultA: Seq[Cluster], resultB: Seq[Cluster]) => {
            resultA ++ resultB
        })
    }

    private def runAnnealing(clusters: Seq[Cluster]): RatedPosition = {
        val futures = clusters.map(cluster => {
            new FutureTask[RatedPosition] (new Callable[RatedPosition] () {
                def call (): RatedPosition = {
                    val centroid = cluster.centroid // TODO take best sample(s) from the cluster instead of the centroid?
                    val ratedCentroid = RatedPosition (centroid, fitnessCalculator.calculateFitness (centroid) )
                    val annealedPosition = annealer.anneal (ratedCentroid)
//                    println (s"centroid before annealing: $ratedCentroid")
//                    println (s"position after annealing:  $annealedPosition")
//                    println
                    annealedPosition
                }
            })}
        )
        futures.foreach (future => {
            pool.execute (future)
        })
        futures.map(future => {
            future.get ()
        }).minBy (x => x.fitness)
    }
}
