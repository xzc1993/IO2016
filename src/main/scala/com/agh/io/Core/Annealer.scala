package com.agh.io.Core

import com.agh.io.Map.{Map, Point}

import scala.util.Random

class Annealer(map: Map, fitnessCalculator: FitnessCalculator) {
    private val StartTemperature = 1000.0
    private val MinTemperature = 0.5
    private val CoolingFactor = 0.9999
    private val MovingRange = 5.0 // TODO optimize these, parameterize?

    def anneal(position: RatedPosition): RatedPosition = {
        var temperature = StartTemperature
        var iterationNo = 0
        var currentPosition = position
        var bestPosition = currentPosition
        while (temperature > MinTemperature) {
            iterationNo += 1
            val newPosition = moveRandomly(currentPosition)
            if (random.nextDouble() < acceptanceProbability(newPosition.fitness - currentPosition.fitness, temperature)) {
                currentPosition = newPosition
            }
            if (newPosition.fitness < bestPosition.fitness) {
                bestPosition = newPosition
            }
            temperature = temperature * CoolingFactor

            if (iterationNo % 10000 == 0) {
                println(s"annealing... iteration $iterationNo, T = $temperature")
            }
        }

        println(s"done after $iterationNo iterations")

        bestPosition
    }


    private def moveRandomly(position: RatedPosition): RatedPosition = {
        var newPoint: Point = null
        do {
            val xMove = randomMove() * widthFactor
            val yMove = randomMove() * heightFactor
            newPoint = Point(position.position.point.x + xMove, position.position.point.y + yMove)
        } while (isOutsideMap(newPoint))
        val newPosition = Position(newPoint, position.position.angle + randomMove())
        RatedPosition(newPosition, fitnessCalculator.calculateFitness(newPosition))
    }

    private def randomMove() = (random.nextDouble() - 0.5) * MovingRange

    private def isOutsideMap(point: Point): Boolean = {
        point.x > map.getMapWidth || point.x < 0.0 || point.y > map.getMapHeight || point.y < 0
    }

    private val random = new Random()
    private val widthFactor = map.getMapWidth / 360.0
    private val heightFactor = map.getMapHeight / 360.0

    private def acceptanceProbability(fitnessDifference: Double, temperature: Double) = {
        if (fitnessDifference < 0.0) 1.0 else math.exp(-fitnessDifference / temperature)
    }
}
