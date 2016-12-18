package com.agh.io

import akka.actor.{ActorSystem, Props}
import com.agh.io.configuration.CommandLineParser
import com.typesafe.config.ConfigFactory

object Main {
    def main(args: Array[String]): Unit = {
        val configuration = new CommandLineParser().load(args)
        ActorSystem("WorkerSystem", akkaConfig(configuration.hostname, 2554))
        if (configuration.nodeId == 0) {
            val coordinatorSystem = ActorSystem("CoordinatorSystem", akkaConfig(configuration.hostname, 2552))
            coordinatorSystem.actorOf(Coordinator.props(configuration), name = "coordinator")
        }
    }

    private def akkaConfig(hostname: String, port: Int) = ConfigFactory.parseString(
        s"""
           |akka {
           |  actor {
           |    provider = "akka.remote.RemoteActorRefProvider"
           |    warn-about-java-serializer-usage = false
           |  }
           |  remote {
           |    enabled-transports = ["akka.remote.netty.tcp"]
           |    netty.tcp {
           |      hostname = "$hostname"
           |      port = $port
           |    }
           |  }
           |}
        """.stripMargin)
}