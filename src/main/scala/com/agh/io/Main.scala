package com.agh.io

import akka.actor.ActorSystem
import com.agh.io.configuration.CommandLineParser
import com.agh.io.remote.Coordinator
import com.agh.io.remote.Properties._
import com.typesafe.config.ConfigFactory

object Main {
    def main(args: Array[String]): Unit = {
        val configuration = new CommandLineParser().load(args)
        ActorSystem(WorkerSystemName, akkaConfig(configuration.hostname, WorkerSystemPort))
        if (configuration.nodeId == 0) {
            val coordinatorSystem = ActorSystem(CoordinatorSystemName, akkaConfig(configuration.hostname, CoordinatorSystemPort))
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