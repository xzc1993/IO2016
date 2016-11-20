package com.agh.io.Util

object MathUtils {
    case class Tolerance(tolerance: Double) {
        require(tolerance >= 0.0)
    }

    implicit class TolerantDouble(number: Double)(implicit tolerance: Tolerance) {
        def >=~(aNumber: Double) = number >= aNumber - tolerance.tolerance
        def <=~(aNumber: Double) = number <= aNumber + tolerance.tolerance
        def ==~(aNumber: Double) = >=~(aNumber) && <=~(aNumber)
        def !=~(aNumber: Double) = ! ==~(aNumber)
    }

    implicit val DefaultTolerance = Tolerance(1.0e-6)
}