package com.agh.io

import java.io.{File, PrintWriter}

import com.agh.io.Configuration.CommandLineParser
import com.agh.io.Core._
import com.agh.io.Map.{MapLoader, Point}
import com.agh.io.Sensor.SensorLoader

import scala.io.Source

object Main extends App {
    val configuration = new CommandLineParser().load(args)
    val map = new MapLoader(configuration.mapDataFile).load()
    val sensor = new SensorLoader(configuration.sensorDataFile).load()
    val mapDrafter = new MapDrafter(map)
    var results = List[RatedPosition]()


//    val positions = Source.fromFile("results.txt").getLines().map( line => {
//        val coordinates = line.split(",")
//        new Position( new Point(coordinates(0).toDouble, coordinates(1).toDouble), 0.0)
//    })
//    mapDrafter.drawPath(positions.toList)
    val pw = new PrintWriter(new File("results.txt" ))
    for(idx <- 0 to sensor.scans.length){
        println(s"Calculating position for reading ${idx}")
        val positionCalculator = new PositionCalculator(idx, configuration, map, sensor)
        val result = positionCalculator.run()
        results = results :+ result
        pw.write("" + result.position.point.x + "," + result.position.point.y + "\n")
        pw.flush()
    }
    pw.close
    mapDrafter.drawPath(results)
}