package com.agh.io.sensor

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

class SensorLoader(sensorDataFile: File) {
    def load(): Sensor = {
        var data: Array[SensorScan] = new Array[SensorScan](0)
        val bufferedSource = io.Source.fromFile(sensorDataFile)
        for (line <- bufferedSource.getLines) {
            val cols = line.split(";").map(_.trim)
            val dateTime = parseDateTime(cols(0))
            val leftWheelVelocity = cols(1).toInt
            val rightWheelVelocity = cols(2).toInt
            val readings = cols.drop(3)
            data :+= SensorScan(dateTime, leftWheelVelocity, rightWheelVelocity, readings.sliding(2, 2).collect({
                case Array(distance, angle) => SensorReading(angle.toDouble, distance.toDouble)
            }).toArray)
        }
        bufferedSource.close
        Sensor(data)
    }

    private def parseDateTime(dateTimeString: String) = LocalDateTime.parse(dateTimeString, DateTimeFormatter)

    private val DateTimeFormatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 3, true)
        .toFormatter
}
