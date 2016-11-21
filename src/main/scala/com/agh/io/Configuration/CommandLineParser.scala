package com.agh.io.Configuration

import java.io.File

class ConfigurationException(msg: String) extends Exception(msg)

class CommandLineParser {

    def load(args: Array[String]): Configuration = {
        val parser = new scopt.OptionParser[Configuration]("scopt") {
            opt[Int]('i', "nodeId").required().action({ (x, c) =>
                c.copy(nodeId = x)
            }).text("Current node ID.")
            opt[File]('s', "sensorData").required().valueName("<file>").action({ (x, c) =>
                c.copy(sensorDataFile = x)
            }).text("File containing sensor data.")
            opt[File]('m', "map").required().valueName("<file>").action({ (x, c) =>
                c.copy(mapDataFile = x)
            }).text("File containing map.")
            opt[Double]("lowerDistanceAccuracyThreshold").action({ (x, c) =>
                c.copy(sensorParameters = c.sensorParameters.copy(lowerDistanceAccuracyThreshold = x))
            }).text("The minimum value of an accurate sensor reading (millimeters).")
            opt[Double]("upperDistanceAccuracyThreshold").action({ (x, c) =>
                c.copy(sensorParameters = c.sensorParameters.copy(upperDistanceAccuracyThreshold = x))
            }).text("The maximum value of an accurate sensor reading (millimeters).")
            opt[Double]("infiniteDistanceReadingValue").action({ (x, c) =>
                c.copy(sensorParameters = c.sensorParameters.copy(infiniteDistanceReadingValue = x))
            }).text("The value of a sensor reading when the obstacles are out of range (millimeters).")
        }
        val config: Configuration = parser.parse(args, Configuration()).orNull
        if (config == null) {
            throw new ConfigurationException("Failed to load arguments.")
        }
        config
    }
}