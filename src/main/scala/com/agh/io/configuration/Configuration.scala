package com.agh.io.configuration

import java.io.File

case class SensorParameters(lowerDistanceAccuracyThreshold: Double = 50.0,
                            upperDistanceAccuracyThreshold: Double = 5000.0,
                            infiniteDistanceReadingValue: Double = 0.0) {
    require(lowerDistanceAccuracyThreshold >= 0 && upperDistanceAccuracyThreshold >= 0)
    require(lowerDistanceAccuracyThreshold < upperDistanceAccuracyThreshold)
    require(!(lowerDistanceAccuracyThreshold <= infiniteDistanceReadingValue && infiniteDistanceReadingValue <= upperDistanceAccuracyThreshold))
}

case class InputData(sensorDataFile: File = null,
                     mapDataFile: File = null,
                     sensorParameters: SensorParameters = SensorParameters())

case class Configuration(nodeId: Int = 0,
                         rangeStart: Int = 0,
                         rangeEnd: Int = Int.MaxValue,
                         hostname: String = "",
                         coordinatorHostname: String = "",
                         hostsFile: File = null,
                         drawingFile: File = null,
                         inputData: InputData = InputData())