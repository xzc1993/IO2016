package com.agh.io.Configuration

class ConfigurationException(msg: String) extends Exception(msg)

class CommandLineParser {

    def load(args: Array[String]): Configuration = {
        val parser = new scopt.OptionParser[Configuration]("scopt") {
            opt[Int]('i', "nodeId").required().action({ (x, c) =>
                c.copy(nodeId = x)
            }).text("Current node ID.")
        }
        val config: Configuration = parser.parse(args, Configuration()).orNull
        if (config == null) {
            throw new ConfigurationException("Failed to load arguments.")
        }
        config
    }
}