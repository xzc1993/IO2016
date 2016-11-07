package com.agh.io.Configuration

import java.io.File

/**
  * Created by XZC on 11/6/2016.
  */
case class Configuration(
     nodeId: Int = 0,
     sensorDataFile: File = null,
     mapDataFile: File = null
)

