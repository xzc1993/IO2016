package com.agh.io

import java.io.File
import java.time.temporal.ChronoUnit

import com.agh.io.configuration.CommandLineParser
import com.agh.io.core.{Position, RatedPosition, RatedPositionWithPrediction, RobotMotionModelPositionCalculator}
import com.agh.io.map.{MapLoader, Point}
import com.agh.io.output.MapDrafter
import com.agh.io.sensor.{Sensor, SensorLoader, SensorReading, SensorScan}

/**
  * Created by XZC on 1/5/2017.
  */
object DrawerMain {
    def main(args: Array[String]): Unit = {
        val configuration = new CommandLineParser().load(args)
        val sensor = new SensorLoader(configuration.inputData.sensorDataFile).load()
        val map = new MapLoader(configuration.inputData.mapDataFile).load()


        val positions = loadPositions()
        println(positions.length)
        val positionPredictions = calculatePositionPredictions(sensor, positions)
        println(positionPredictions.length)
        val positionsWithPredictions = positions.zip(positionPredictions).map(RatedPositionWithPrediction.tupled(_))
        println(positionsWithPredictions.length)

        var idx = 0;
        for(slice <- positionsWithPredictions.grouped(100)){
            val file = new File(s"map_3_${idx}.png")
            val mapDrafter = new MapDrafter(map, file)
            mapDrafter.drawPath(slice)
            idx += 1
        }

    }

    def loadPositions(): Seq[RatedPosition] = {
        val bufferedSource = io.Source.fromFile("results/results_3.csv")
        var data: Array[RatedPosition] = new Array[RatedPosition](0)
        for (line <- bufferedSource.getLines) {
            val cols = line.split(",").map(_.trim)
            data :+= RatedPosition(new Position(new Point(cols(0).toDouble, cols(1).toDouble), cols(2).toDouble), cols(3).toDouble)
        }
        bufferedSource.close
        data.toSeq
    }

    def calculatePositionPredictions(sensor: Sensor, positions: Seq[RatedPosition]): Seq[Position] = {
        positions.head.position +: positions.zip(sensor.scans.zip(sensor.scans.drop(1))).map({
            case (position, (scan, nextScan)) =>
                new RobotMotionModelPositionCalculator(position.position, scan.leftWheelVelocity, scan.rightWheelVelocity)
                    .getPositionAfterTime(scan.date.until(nextScan.date, ChronoUnit.MILLIS) / 1000.0)
        })
    }
}
