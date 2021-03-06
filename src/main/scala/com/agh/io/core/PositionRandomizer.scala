package com.agh.io.core

import com.agh.io.map.Point
import com.agh.io.map.Map

import scala.util.Random

/**
  * Created by XZC on 11/8/2016.
  */
object PositionRandomizer {

    val random = new Random()

    def getRandomPositionOnMap(map: Map): Position = {
        Position(Point(random.nextDouble() * map.getMapWidth, random.nextDouble() * map.getMapHeight), random.nextDouble() * 360)
    }
}
