package com.agh.io.Map

import org.scalatest.FlatSpec

class LineParameterCalculator_Test extends FlatSpec{

    "A LineParameterCalculator" must "return vertical line" in {
        val line = new LineCalculator().getLineBasedOnTwoPoints( new Point(1, 1), new Point(1,2))
        assert( line.a/1 === line.c/(-1))
        assert( line.b === 0)
    }

    it must "return horizontal line" in {
        val line = new LineCalculator().getLineBasedOnTwoPoints( new Point(0, 1), new Point(1,1))
        assert( line.a === 0)
        assert( line.b/1 === line.c/(-1))
    }

    it must "return oblique line" in {
        val line = new LineCalculator().getLineBasedOnTwoPoints( new Point(1, 1), new Point(0,0))
        assert( line.a/1 === line.b/(-1))
        assert( line.c === 0)
    }

    it must "return translated oblique line" in {
        val line = new LineCalculator().getLineBasedOnTwoPoints( new Point(1, 2), new Point(0,1))
        assert( line.a/1 === line.c/1)
        assert( line.a/1 === line.b/(-1))
    }
}
