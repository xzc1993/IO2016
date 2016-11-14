package com.agh.io.Core

import com.agh.io.Map.{Line, LineCalculator}
import com.agh.io.Sensor.SensorReading
import com.agh.io.Map.Map

/**
  * Created by XZC on 11/8/2016.
  */
object FitnessCalculator {

    def calculateFitness(map: Map, position: Position, sensorReading: SensorReading): Double = {
        var error = 0.0
        for(currentReadingIndex: Int <- 0 to 1366 ){
            var currentAngle = position.angle + currentReadingIndex * 0.2
            var sensorReadingLine: Line = LineCalculator.getLineFromPointWithGivenAngle(
                position.position,
                currentAngle
            )
            var expectedCollisionPoint = map.findCollisionWithWalls(sensorReadingLine, position.position, currentAngle)
            error += position.position.getDistanceToPoint(expectedCollisionPoint)
        }
        error
    }
}
