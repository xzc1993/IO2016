package com.agh.io.remote

import com.agh.io.core.RatedPosition
import com.agh.io.sensor.SensorScan

case object WorkerStarted

case class Calculate(sensorScan: SensorScan, id: Int)

case class Calculated(ratedPosition: RatedPosition, id: Int)

case object ShutDown