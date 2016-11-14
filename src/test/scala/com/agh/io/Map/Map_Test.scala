package com.agh.io.Map

import java.util
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Suite}

trait TestMapCreator extends BeforeAndAfterEach { this: Suite =>

    val map: Map = new Map(new MapData(new util.ArrayList[Wall]()))

    override def beforeEach() {
        map.data.walls.add(new Wall( new Point(0.0, 0.0), new Point(0.0, 1.0)))
        map.data.walls.add(new Wall( new Point(0.0, 1.0), new Point(1.0, 1.0)))
        map.data.walls.add(new Wall( new Point(1.0, 1.0), new Point(1.0, 0.0)))
        map.data.walls.add(new Wall( new Point(1.0, 0.0), new Point(0.0, 0.0)))
        super.beforeEach()
    }

    override def afterEach() {
        try {
            super.afterEach() // To be stackable, must call super.afterEach
        }
        finally {

        }
    }
}

class Map_Test extends FlatSpec with TestMapCreator{

    "findCollisionWithWalls" must "collide at (0.0,0.5)" in {
        val collsionPoint: Point = map.findCollisionWithWalls( new Line(0.0, 1.0, -0.5), new Point(0.5, 0.5), 180.0)
        assert( collsionPoint.x == 0.0)
        assert( collsionPoint.y == 0.5)
    }

    "findCollisionWithWalls" must "collide at (1.0,0.5)" in {
        val collsionPoint: Point = map.findCollisionWithWalls( new Line(0.0, 1.0, -0.5), new Point(0.5, 0.5), 0.0)
        assert( collsionPoint.x == 1.0)
        assert( collsionPoint.y == 0.5)
    }
}
