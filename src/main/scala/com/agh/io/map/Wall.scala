package com.agh.io.map

/**
  * Created by XZC on 11/8/2016.
  */
case class Wall(from: Point, to: Point) {
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

    def scale(factor: Double): Wall = Wall(from = from.scale(factor), to = to.scale(factor))
}
