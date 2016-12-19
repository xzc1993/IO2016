package com.agh.io.sensor

import java.time.LocalDateTime

/**
  * Created by XZC on 11/6/2016.
  */
case class SensorScan(date: LocalDateTime, leftWheelVelocity: Int, rightWheelVelocity: Int, readings: Array[SensorReading])