package com.agh.io.Sensor

import java.io.File

class SensorLoader(sensorDataFile: File){

    def load(): Sensor = {
        var data: Array[SensorScan] = new Array[SensorScan](0)
        val bufferedSource = io.Source.fromFile(sensorDataFile)
        for (line <- bufferedSource.getLines) {
            val cols = line.split(";").map(_.trim)
            val dateTime = cols(0)
            val readings = cols.drop(3) // 1 and 2 are some unknown data
            data :+= new SensorScan(dateTime, readings.sliding(2, 2).collect({
                case Array(distance, angle) => SensorReading(angle.toDouble, distance.toDouble)
            }).toArray)
        }
        bufferedSource.close
        new Sensor(data)
    }
}
