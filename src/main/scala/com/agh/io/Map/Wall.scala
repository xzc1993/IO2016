package com.agh.io.Map

/**
  * Created by XZC on 11/8/2016.
  */
case class Wall(
    typeName: String,
    id: String,
    width: Double,
    height: Double,
    color: String,
    from: Point,
    to: Point
)
