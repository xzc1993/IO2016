package com.agh.io.Map

object LineCalculator {

    def getLineBasedOnTwoPoints(a: Point, b: Point): Line = {
        new Line( a.y - b.y, b.x - a.x, - a.y * (b.x - a.x) + a.x * (b.y - a.y))
    }

    def getLineFromPointWithGivenAngle(point: Point, angle: Double): Line ={
        if( angle != 90.0){
            val a = Math.tan(Math.toRadians(angle))
            new Line( a, 1.0, -(a * point.x + point.y))
        }
        else{
            new Line( 1.0, 0.0, point.x)
        }
    }
}
