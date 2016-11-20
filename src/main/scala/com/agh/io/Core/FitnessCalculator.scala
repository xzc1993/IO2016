package com.agh.io.Core

import com.agh.io.Map.{Line, LineCalculator}
import com.agh.io.Sensor.SensorScan
import com.agh.io.Map.Map

/**
  * Created by XZC on 11/8/2016.
  */
object FitnessCalculator {
    private val NoWallPenalty = 5000.0

    def calculateFitness(map: Map, position: Position, sensorScan: SensorScan): Double = {
        val errors = sensorScan.readings.map(reading => {
            val currentAngle = normalizeAngle(position.angle + reading.angle)
            val theoreticalReadingLine = LineCalculator.getLineFromPointWithGivenAngle(position.position, currentAngle)
            val maybeCollisionPoint = map.findCollisionWithWalls(theoreticalReadingLine, position.position, currentAngle)
            maybeCollisionPoint.map(_.getDistanceToPoint(position.position) - reading.distance).getOrElse(NoWallPenalty)
        })
        calculateMeanSquaredError(errors)
    }

    private def normalizeAngle(angle: Double): Double = {
        var angleToNormalize = angle
        while (angleToNormalize < 0.0) angleToNormalize += 360.0
        angleToNormalize % 360.0
    }

    private def calculateMeanSquaredError(errors: Array[Double]): Double = {
        math.sqrt(errors.map(e => e * e).sum / errors.length)
    }
}
