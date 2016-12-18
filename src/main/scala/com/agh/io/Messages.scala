package com.agh.io

import com.agh.io.core.RatedPosition
import com.agh.io.sensor.SensorScan

case class Calculate(sensorScan: SensorScan)

case class Calculated(workerId: Int, ratedPosition: RatedPosition)