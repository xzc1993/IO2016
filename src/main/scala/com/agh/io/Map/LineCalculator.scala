package com.agh.io.Map

object LineCalculator {
    import com.agh.io.Util.MathUtils._

    def getLineBasedOnTwoPoints(a: Point, b: Point): Line = {
        new Line( a.y - b.y, b.x - a.x, - a.y * (b.x - a.x) + a.x * (b.y - a.y))
    }

    def getLineFromPointWithGivenAngle(point: Point, angle: Double): Line ={
        if( angle !=~ 90.0){
            val a = Math.tan(Math.toRadians(angle))
            val A = -a
            new Line(A, 1.0, -(A * point.x + point.y))
        }
        else{
            new Line(-1.0, 0.0, point.x)
        }
    }

    def getCrossingPoint(lineA: Line, lineB: Line): Option[Point] = {
        if( lineA.b !=~ 0.0 && lineB.b !=~ 0.0){
            val lineADirectional: Double = lineA.getNormalizedA()
            val lineAIntersection: Double = lineA.getNormalizedC()
            val lineBDirectional: Double = lineB.getNormalizedA()
            val lineBIntersection: Double = lineB.getNormalizedC()
            if( lineADirectional ==~ lineBDirectional){
                return None
            }
            val x: Double = (lineBIntersection - lineAIntersection)/(lineADirectional - lineBDirectional)
            Some(Point(
                (lineBIntersection - lineAIntersection)/(lineADirectional - lineBDirectional),
                lineADirectional * x + lineAIntersection
            ))
        }
        else if( lineA.b ==~ 0.0 && lineB.b ==~ 0.0){
            None
        }
        else if( lineA.b !=~ 0.0){
            _calculateIntersectionWithVerticalLine(lineA, lineB)
        }
        else { //if( lineB.b !=~ 0.0)
            _calculateIntersectionWithVerticalLine(lineB, lineA)
        }
    }

    def _calculateIntersectionWithVerticalLine(line: Line, verticalLine: Line): Option[Point] = {
        val x = (-verticalLine.c)/verticalLine.a
        Some(Point(
            x,
            line.getNormalizedA() * x + line.getNormalizedC()
        ))
    }
}
