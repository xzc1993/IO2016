package com.agh.io.Core

import com.agh.io.Map.Point

case class Cluster(positions: Seq[RatedPosition]) {
    def centroid = {
        val sum = positions.map(_.position).reduce((p1: Position, p2: Position) => {
            Position(Point(p1.position.x + p2.position.x, p1.position.y + p2.position.y), p1.angle + p2.angle)
        })
        Position(Point(sum.position.x / positions.length, sum.position.y / positions.length), sum.angle / positions.length)
    }
}