package com.agh.io.core

case class RatedPosition(position: Position, fitness: Double) extends Ordered[RatedPosition] {
    def compare(that: RatedPosition): Int = this.fitness compare that.fitness
}