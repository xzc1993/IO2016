package com.agh.io.Sensor

import java.io.File

class SensorLoader(sensorDataFile: File){

    def load(): Sensor = {
        var data: Array[SensorReading] = new Array[SensorReading](0)
        val bufferedSource = io.Source.fromFile(sensorDataFile)
        for (line <- bufferedSource.getLines) {
            val cols = line.split(";").map(_.trim)
            data :+= new SensorReading(cols(0), cols.drop(1).map( x => x.toDouble))
        }
        bufferedSource.close
        new Sensor(data)
    }
}
