package com.agh.io.core

import com.agh.io.map.Point

/**
  * Created by XZC on 11/8/2016.
  */
case class Position(point: Point, angle: Double) {
    def -(that: Position) = Position(this.point - that.point, this.angle - that.angle)
}
