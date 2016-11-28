package com.agh.io.Core

case class RatedPosition(position: Position, fitness: Double) extends Ordered[RatedPosition] {
    def compare(that: RatedPosition): Int = this.fitness compare that.fitness
}