package com.agh.io.Map

import org.scalactic.TolerantNumerics
import org.scalatest.FlatSpec

class LineParameterCalculator_Test extends FlatSpec{

    implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(1e-4f)

    "getLineBasedOnTwoPoints" must "return vertical line" in {
        val line = LineCalculator.getLineBasedOnTwoPoints( new Point(1, 1), new Point(1,2))
        assert( line.a/1 === line.c/(-1))
        assert( line.b === 0.0)
    }

    it must "return horizontal line" in {
        val line = LineCalculator.getLineBasedOnTwoPoints( new Point(0, 1), new Point(1,1))
        assert( line.a === 0.0)
        assert( line.b/1 === line.c/(-1))
    }

    it must "return oblique line" in {
        val line = LineCalculator.getLineBasedOnTwoPoints( new Point(1, 1), new Point(0,0))
        assert( line.a/1 === line.b/(-1))
        assert( line.c === 0.0)
    }

    it must "return translated oblique line" in {
        val line = LineCalculator.getLineBasedOnTwoPoints( new Point(1, 2), new Point(0,1))
        assert( line.a/1 === line.c/1)
        assert( line.a/1 === line.b/(-1))
    }

    "getLineFromPointWithGivenAngle" must "return flat horizontal line" in {
        val line = LineCalculator.getLineFromPointWithGivenAngle( new Point(0.0, 0.0), 0.0)
        assert( line.a === 0.0)
        assert( line.b === 1.0)
        assert( line.c === 0.0)
    }

    it must "return flat vertical line" in {
        val line = LineCalculator.getLineFromPointWithGivenAngle( new Point(1.0, 0.0), 90.0)
        assert( line.a/1 === line.c/1)
        assert( line.b === 0.0)
    }

    it must "return flat x + y + 0 = 0 line" in {
        val line = LineCalculator.getLineFromPointWithGivenAngle( new Point(0.0, 0.0), 45.0)
        assert( line.a/1 === line.b/1)
        assert( line.c === 0.0)
    }

    it must "also return flat x + y + 0 = 0 line" in {
        val line = LineCalculator.getLineFromPointWithGivenAngle( new Point(0.0, 0.0), 225.0)
        assert( line.a/1 === line.b/1)
        assert( line.c === 0.0)
    }

    it must "return flat -x + y + 0 = 0 line" in {
        val line = LineCalculator.getLineFromPointWithGivenAngle( new Point(0.0, 0.0), 135.0)
        assert( line.a/(-1) === line.b/1)
        assert( line.c === 0.0)
    }

    it must "also return flat -x + y + 0 = 0 line" in {
        val line = LineCalculator.getLineFromPointWithGivenAngle( new Point(0.0, 0.0), 315.0)
        assert( line.a/(-1) === line.b/1)
        assert( line.c === 0.0)
    }

    it must "also return flat -x + y + 1 = 0 line" in {
        val line = LineCalculator.getLineFromPointWithGivenAngle( new Point(2.0, 1.0), 315.0)
        assert( line.a/(-1) === line.b/1)
        assert( line.a/(-1)=== line.c/1)
    }

    "getCrossingPoint" must "return (0,0)" in {
        val point = LineCalculator.getCrossingPoint( new Line(-1.0, 1, 0), new Line(1.0, 1, 0))
        assert( point.x === 0.0)
        assert( point.y === 0.0)
    }

    "getCrossingPoint" must "return (5,5)" in {
        val point = LineCalculator.getCrossingPoint( new Line( 0, 1, -5), new Line(1, 0, -5))
        assert( point.x === 5.0)
        assert( point.y === 5.0)
    }

    "getCrossingPoint" must "return (1,4)" in {
        val point = LineCalculator.getCrossingPoint( new Line( -1.0, 1, -3), new Line(1.0, 1, -5))
        assert( point.x === 1.0)
        assert( point.y === 4.0)
    }
}
