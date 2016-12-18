package com.agh.io.remote

import com.agh.io.core.RatedPosition
import com.agh.io.sensor.SensorScan

case class Calculate(sensorScan: SensorScan)

case class Calculated(workerId: Int, ratedPosition: RatedPosition)

case object ShutDown