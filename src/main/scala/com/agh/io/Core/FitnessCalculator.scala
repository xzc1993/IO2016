package com.agh.io.Core

import com.agh.io.Configuration.SensorParameters
import com.agh.io.Map.{LineCalculator, Map}
import com.agh.io.Sensor.SensorScan

/**
  * Created by XZC on 11/8/2016.
  */
class FitnessCalculator(sensorParameters: SensorParameters) {
    def calculateFitness(map: Map, position: Position, sensorScan: SensorScan): Double = {
        val angles = sensorScan.readings.map(_.angle)
        val theoreticalDistanceReadings = angles.map(angle => {
            val currentAngle = normalizeAngle(position.angle + angle)
            val theoreticalReadingLine = LineCalculator.getLineFromPointWithGivenAngle(position.position, currentAngle)
            val maybeCollisionPoint = map.findCollisionWithWalls(theoreticalReadingLine, position.position, currentAngle)
            maybeCollisionPoint.map(_.getDistanceToPoint(position.position)).getOrElse(InfiniteDistance)
        })
        val normalizedTheoreticalDistanceReadings = theoreticalDistanceReadings.map(normalizeDistance(InfiniteDistance))
        val normalizedRealDistanceReadings = sensorScan.readings.map(_.distance)
            .map(normalizeDistance(sensorParameters.infiniteDistanceReadingValue))

        val distanceReadingErrors = normalizedTheoreticalDistanceReadings.zip(normalizedRealDistanceReadings).map({
            case (theoretical, real) => theoretical - real
        })
        calculateMeanSquaredError(distanceReadingErrors)
    }

    private def normalizeAngle(angle: Double): Double = {
        var angleToNormalize = angle
        while (angleToNormalize < 0.0) angleToNormalize += 360.0
        angleToNormalize % 360.0
    }

    private def normalizeDistance(infiniteDistanceValue: Double): (Double) => Double = distance => {
        if (distance == infiniteDistanceValue) sensorParameters.upperDistanceAccuracyThreshold
        else if (distance < sensorParameters.lowerDistanceAccuracyThreshold) sensorParameters.lowerDistanceAccuracyThreshold
        else if (distance > sensorParameters.upperDistanceAccuracyThreshold) sensorParameters.upperDistanceAccuracyThreshold
        else distance
    }

    private def calculateMeanSquaredError(errors: Array[Double]): Double = {
        math.sqrt(errors.map(e => e * e).sum / errors.length)
    }

    private val InfiniteDistance = Double.PositiveInfinity
}
