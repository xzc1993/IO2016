package com.agh.io.core

import com.agh.io.map.Point

case class Cluster(positions: Seq[RatedPosition]) {
    def centroid = {
        val sum = positions.map(_.position).reduce((p1: Position, p2: Position) => {
            Position(Point(p1.point.x + p2.point.x, p1.point.y + p2.point.y), p1.angle + p2.angle)
        })
        Position(Point(sum.point.x / positions.length, sum.point.y / positions.length), sum.angle / positions.length)
    }
}