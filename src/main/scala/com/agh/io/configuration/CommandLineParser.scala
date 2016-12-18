package com.agh.io.configuration

import java.io.File

class ConfigurationException(msg: String) extends Exception(msg)

class CommandLineParser {

    def load(args: Array[String]): Configuration = {
        val parser = new scopt.OptionParser[Configuration]("scopt") {
            opt[Int]('i', "nodeId").required().action({ (x, c) =>
                c.copy(nodeId = x)
            }).text("Current node ID.")
            opt[String]('h', "hostname").required().action({ (x, c) =>
                c.copy(hostname = x)
            }).text("Current node hostname.")
            opt[File]('x', "hosts").required().action({ (x, c) =>
                c.copy(hostsFileName = x)
            }).text("Current node hostname.")
            opt[File]('s', "sensorData").required().valueName("<file>").action({ (x, c) =>
                c.copy(inputData = c.inputData.copy(sensorDataFile = x))
            }).text("File containing sensor data.")
            opt[File]('m', "map").required().valueName("<file>").action({ (x, c) =>
                c.copy(inputData = c.inputData.copy(mapDataFile = x))
            }).text("File containing map.")
            opt[Double]("lowerDistanceAccuracyThreshold").action({ (x, c) =>
                c.copy(inputData = c.inputData.copy(sensorParameters = c.inputData.sensorParameters.copy(lowerDistanceAccuracyThreshold = x)))
            }).text("The minimum value of an accurate sensor reading (millimeters).")
            opt[Double]("upperDistanceAccuracyThreshold").action({ (x, c) =>
                c.copy(inputData = c.inputData.copy(sensorParameters = c.inputData.sensorParameters.copy(upperDistanceAccuracyThreshold = x)))
            }).text("The maximum value of an accurate sensor reading (millimeters).")
            opt[Double]("infiniteDistanceReadingValue").action({ (x, c) =>
                c.copy(inputData = c.inputData.copy(sensorParameters = c.inputData.sensorParameters.copy(infiniteDistanceReadingValue = x)))
            }).text("The value of a sensor reading when the obstacles are out of range (millimeters).")
        }
        val config: Configuration = parser.parse(args, Configuration()).orNull
        if (config == null) {
            throw new ConfigurationException("Failed to load arguments.")
        }
        config
    }
}