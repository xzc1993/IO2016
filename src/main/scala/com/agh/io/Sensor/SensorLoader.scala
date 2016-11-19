package com.agh.io.Sensor

import java.io.File

class SensorLoader(sensorDataFile: File){

    def load(): Sensor = {
        var data: Array[SensorScan] = new Array[SensorScan](0)
        val bufferedSource = io.Source.fromFile(sensorDataFile)
        for (line <- bufferedSource.getLines) {
            val cols = line.split(";").map(_.trim)
            val dateTime = cols(0)
            data :+= new SensorScan(dateTime, cols.drop(1).map(x => x.toDouble))
        }
        bufferedSource.close
        new Sensor(data)
    }
}
