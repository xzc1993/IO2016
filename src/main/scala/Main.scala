package com.agh.io

import com.agh.io.Configuration.CommandLineParser
import com.agh.io.Map.MapLoader
import com.agh.io.Sensor.SensorLoader

object Main extends App {
    val configuration = new CommandLineParser().load(args)
    val map = new MapLoader(configuration.mapDataFile).load()
    val sensor = new SensorLoader(configuration.sensorDataFile).load()
    println(s"Hello World from node ${configuration.nodeId}")
    println(map.getPixel(0,0))
    println(sensor.data(0).data(5))
}