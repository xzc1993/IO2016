package com.agh.io.Map

class LineCalculator {

    def getLineBasedOnTwoPoints(a: Point, b: Point): Line = {
        new Line( a.y - b.y, b.x - a.x, - a.y * (b.x - a.x) + a.x * (b.y - a.y))
    }
}
