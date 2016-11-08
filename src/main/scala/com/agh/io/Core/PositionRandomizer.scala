package com.agh.io.Core

import com.agh.io.Map.Point
import com.agh.io.Map.Map

import scala.util.Random

/**
  * Created by XZC on 11/8/2016.
  */
object PositionRandomizer {

    val random = new Random(11)

    def getRandomPositionOnMap(map: Map): Position = {
        new Position( new Point( random.nextDouble() * map.getMapWidth(), random.nextDouble() * map.getMapHeight()), random.nextDouble() * 360)
    }
}
