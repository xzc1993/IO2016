package com.agh.io.Core

import com.agh.io.Configuration.SensorParameters
import com.agh.io.Map.{LineCalculator, Map}
import com.agh.io.Sensor.SensorScan

/**
  * Created by XZC on 11/8/2016.
  */
class FitnessCalculator(sensorScan: SensorScan, sensorParameters: SensorParameters, map: Map) {
    private val normalizedRealDistanceReadings = sensorScan.readings
        .map(_.distance) // making a copy
        .transform(normalizeDistance(sensorParameters.infiniteDistanceReadingValue))

    def calculateFitness(position: Position): Double = {
        val distanceReadingErrors = calculateDistanceReadingErrors(position)
        calculateMeanSquaredError(distanceReadingErrors)
    }

    def calculateDistanceReadingErrors(position: Position): Array[Double] = {
        val normalizedTheoreticalDistanceReadings = sensorScan.readings.map(_.angle) // making a copy
            .transform(
            angle => {
                val currentAngle = normalizeAngle(position.angle + angle)
                val theoreticalReadingLine = LineCalculator.getLineFromPointWithGivenAngle(position.point, currentAngle)
                val maybeCollisionPoint = map.findCollisionWithWalls(theoreticalReadingLine, position.point, currentAngle)
                val theoreticalDistance = maybeCollisionPoint.map(_.getDistanceToPoint(position.point)).getOrElse(InfiniteDistance)
                normalizeDistance(InfiniteDistance)(theoreticalDistance)
            }
        )

        val distanceReadingErrors = normalizedTheoreticalDistanceReadings // for readability
        for (i <- distanceReadingErrors.indices) {
            val theoretical = normalizedTheoreticalDistanceReadings(i)
            val real = normalizedRealDistanceReadings(i)
            distanceReadingErrors(i) = math.abs(theoretical - real)
        }
        distanceReadingErrors.toArray
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
        errors.map(e => e * e).sum / errors.length
    }

    def calculateErrorStats(position: Position): Stats = {
        val distanceReadingErrors = calculateDistanceReadingErrors(position)
        Stats.calculate(distanceReadingErrors)
    }

    private val InfiniteDistance = Double.PositiveInfinity
}

case class Stats(min: Double, max: Double, mean: Double, stdDev: Double)

object Stats {
    def calculate(series: Seq[Double]): Stats = {
        val mean = series.sum / series.length
        val variances = series.map(x => (x - mean) * (x - mean))
        val stdDev = Math.sqrt(variances.sum / variances.length)
        Stats(series.min, series.max, mean, stdDev)
    }
}