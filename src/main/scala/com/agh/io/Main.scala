package com.agh.io

import java.io.{File, PrintWriter}

import com.agh.io.Configuration.CommandLineParser
import com.agh.io.Core._
import com.agh.io.Map.MapLoader
import com.agh.io.Sensor.SensorLoader

object Main extends App {
    val configuration = new CommandLineParser().load(args)
    val map = new MapLoader(configuration.mapDataFile).load()
    val sensor = new SensorLoader(configuration.sensorDataFile).load()
    val mapDrafter = new MapDrafter(map)
    var results = Set[RatedPosition]()

    for(idx <- 0 to 0){
        println(s"Calculating position for reading ${idx}")
        val positionCalculator = new PositionCalculator(idx, configuration, map, sensor)
        results += positionCalculator.run()
    }

    val pw = new PrintWriter(new File("results.txt" ))
    results.foreach( rp => {
        pw.write("" + rp.position.point.x + "," + rp.position.point.y)
    })
    pw.close

    mapDrafter.drawPath(results.map( x => x.position))
}