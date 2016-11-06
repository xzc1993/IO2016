package com.agh.io

import com.agh.io.Configuration.CommandLineParser
import com.agh.io.Map.MapLoader
import com.agh.io.Sensor.SensorLoader

object Main extends App {
    val configuration = new CommandLineParser().load(args)
    val map = new MapLoader().load();
    val sensor = new SensorLoader().load();
    println(s"Hello World from node ${configuration.nodeId}")
}