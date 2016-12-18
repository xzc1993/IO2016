package com.agh.io.util

import com.agh.io.util.MathUtils.Tolerance
import org.scalatest.{FlatSpec, Matchers}

class TolerantDouble_Test extends FlatSpec with Matchers {
    implicit val tolerance = Tolerance(0.1)

    import com.agh.io.util.MathUtils.TolerantDouble

    "equality" should "be satisfied within the tolerance" in {
        0.91 ==~ 1.0 shouldBe true
        1.09 ==~ 1.0 shouldBe true
    }

    "equality" should "not be satisfied outside the tolerance" in {
        0.89 ==~ 1.0 shouldBe false
        1.11 ==~ 1.0 shouldBe false
    }

    "inequality" should "not be satisfied within the tolerance" in {
        0.91 !=~ 1.0 shouldBe false
        1.09 !=~ 1.0 shouldBe false
    }

    "inequality" should "be satisfied outside the tolerance" in {
        0.89 !=~ 1.0 shouldBe true
        1.11 !=~ 1.0 shouldBe true
    }

    "less than" should "be satisfied within the tolerance" in {
        0.89 <=~ 1.0 shouldBe true
        1.00 <=~ 1.0 shouldBe true
        1.09 <=~ 1.0 shouldBe true
    }

    "less than" should "not be satisfied outside the tolerance" in {
        1.11 <=~ 1.0 shouldBe false
    }

    "greater than" should "be satisfied within the tolerance" in {
        0.91 >=~ 1.0 shouldBe true
        1.00 >=~ 1.0 shouldBe true
        1.11 >=~ 1.0 shouldBe true
    }

    "greater than" should "not be satisfied outside the tolerance" in {
        0.89 >=~ 1.0 shouldBe false
    }
}
