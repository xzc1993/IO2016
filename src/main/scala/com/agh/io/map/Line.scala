package com.agh.io.map

/**
  * Created by XZC on 11/8/2016.
  */
class Line(val a: Double, val b: Double, val c:Double) {

    def getNormalizedA(): Double = {
        return -a/b
    }

    def getNormalizedC() : Double = {
        return -c/b
    }
}
