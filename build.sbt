

name := "IO2016"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.1",
    "com.typesafe.akka" %% "akka-remote" % "2.4.1",
    "com.github.scopt" %% "scopt" % "3.4.0",
    "io.spray" %% "spray-json" % "1.3.2",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "org.apache.commons" % "commons-math3" % "3.6.1",
    "org.springframework" % "spring-context" % "4.2.5.RELEASE",
    "org.springframework" % "spring-aspects" % "4.2.5.RELEASE",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "javax.inject" % "javax.inject" % "1"
)

mainClass in (Compile, run) := Some("com.agh.io.Main")