package com.agh.io.Map

/**
  * Created by XZC on 11/8/2016.
  */
case class Point(
    x: Double,
    y: Double
){
    def getDistanceToPoint(a:Point): Double = {
        Math.sqrt((x - a.x)*(x - a.x) + (y - a.y)*(y - a.y))
    }

    def scale(factor: Double): Point = Point(x = x * factor, y = y * factor)
}
