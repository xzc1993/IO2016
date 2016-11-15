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

trait TestMapCreator2 extends BeforeAndAfterEach { this: Suite =>

    val map: Map = new Map(new MapData(new util.ArrayList[Wall]()))

    override def beforeEach() {
        /*
        Map looks more or less like...
         /\
        //\\
        \\//
         \/
        */
        //outer square
        map.data.walls.add(new Wall( new Point(2.0, 0.0), new Point(0.0, 2.0)))
        map.data.walls.add(new Wall( new Point(0.0, 2.0), new Point(2.0, 4.0)))
        map.data.walls.add(new Wall( new Point(2.0, 4.0), new Point(4.0, 2.0)))
        map.data.walls.add(new Wall( new Point(4.0, 2.0), new Point(2.0, 0.0)))

        //inner square
        map.data.walls.add(new Wall( new Point(2.0, 1.0), new Point(1.0, 2.0)))
        map.data.walls.add(new Wall( new Point(1.0, 2.0), new Point(2.0, 3.0)))
        map.data.walls.add(new Wall( new Point(2.0, 3.0), new Point(3.0, 2.0)))
        map.data.walls.add(new Wall( new Point(3.0, 2.0), new Point(2.0, 1.0)))
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

class Map_Test2 extends FlatSpec with TestMapCreator2{

    "findCollisionWithWalls" must "collide with horizontal line at (1.0,2.0)" in {
        val collsionPoint: Point = map.findCollisionWithWalls( new Line(0.0, 1.0, -2), new Point(2, 2), 180.0)
        assert( collsionPoint.x == 1.0)
        assert( collsionPoint.y == 2.0)
    }

    it must "collide with horizontal line at (3.0,2.0)" in {
        val collsionPoint: Point = map.findCollisionWithWalls( new Line(0.0, 1.0, -2), new Point(2, 2), 0.0)
        assert( collsionPoint.x == 3.0)
        assert( collsionPoint.y == 2.0)
    }

    it must "collide with vertical line at (3.0,2.0)" in {
        val collsionPoint: Point = map.findCollisionWithWalls( new Line(1.0, 0.0, -2), new Point(2, 2), 90.0)
        assert( collsionPoint.x == 2.0)
        assert( collsionPoint.y == 3.0)
    }

    it must "collide with vertical line at (2.0,1.0)" in {
        val collsionPoint: Point = map.findCollisionWithWalls( new Line(1.0, 0.0, -2), new Point(2, 2), 270.0)
        assert( collsionPoint.x == 2.0)
        assert( collsionPoint.y == 1.0)
    }

    it must "collide at (2.5,1.5)" in {
        val collsionPoint: Point = map.findCollisionWithWalls( new Line(1.0, 1.0, -4), new Point(2, 2), 315.0)
        assert( collsionPoint.x == 2.5)
        assert( collsionPoint.y == 1.5)
    }

    it must "collide at (2.5,2.5)" in {
        val collsionPoint: Point = map.findCollisionWithWalls( new Line(-1.0, 1.0, 0), new Point(2, 2), 45.0)
        assert( collsionPoint.x == 2.5)
        assert( collsionPoint.y == 2.5)
    }

    it must "collide at (1.5,1.5)" in {
        val collsionPoint: Point = map.findCollisionWithWalls( new Line(-1.0, 1.0, 0), new Point(2, 2), 225.0)
        assert( collsionPoint.x == 1.5)
        assert( collsionPoint.y == 1.5)
    }
}
