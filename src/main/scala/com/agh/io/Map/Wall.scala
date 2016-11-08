package com.agh.io.Map

/**
  * Created by XZC on 11/8/2016.
  */
class Wall(
    val typeName: String,
    val id: String,
    val width: Double,
    val height: Double,
    val color: String,
    val from: Point,
    val to: Point
){

    val line = LineCalculator.getLineBasedOnTwoPoints(from, to)
}
