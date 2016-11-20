package com.agh.io.Map

/**
  * Created by XZC on 11/8/2016.
  */
class Wall(

    val from: Point,
    val to: Point
){
    def getLine(): Line = {
        LineCalculator.getLineBasedOnTwoPoints(from, to)
    }

    def getMinX(): Double = {
        Math.min(to.x, from.x)
    }

    def getMinY(): Double = {
        Math.min(to.y, from.y)
    }

    def getMaxX(): Double = {
        Math.max(to.x, from.x)
    }

    def getMaxY(): Double = {
        Math.max(to.y, from.y)
    }

    def scale(factor: Double): Wall = new Wall(from = from.scale(factor), to = to.scale(factor))
}
