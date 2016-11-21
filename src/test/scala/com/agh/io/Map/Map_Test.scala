package com.agh.io.Map

import org.scalatest.FlatSpec

class Map_Test extends FlatSpec {
    val map = new Map(MapData(Set(
        Wall(Point(0.0, 0.0), Point(0.0, 1.0)),
        Wall(Point(0.0, 1.0), Point(1.0, 1.0)),
        Wall(Point(1.0, 1.0), Point(1.0, 0.0)),
        Wall(Point(1.0, 0.0), Point(0.0, 0.0))
    )))

    "findCollisionWithWalls" must "collide at (0.0,0.5)" in {
        val collsionPoint: Point = map.findCollisionWithWalls(new Line(0.0, 1.0, -0.5), Point(0.5, 0.5), 180.0).get
        assert(collsionPoint.x == 0.0)
        assert(collsionPoint.y == 0.5)
    }

    "findCollisionWithWalls" must "collide at (1.0,0.5)" in {
        val collsionPoint: Point = map.findCollisionWithWalls(new Line(0.0, 1.0, -0.5), Point(0.5, 0.5), 0.0).get
        assert(collsionPoint.x == 1.0)
        assert(collsionPoint.y == 0.5)
    }
}

class Map_Test2 extends FlatSpec {
    val map: Map = new Map(MapData(Set(
        /*
        Map looks more or less like...
         /\
        //\\
        \\//
         \/
        */

        //outer square
        Wall(Point(2.0, 0.0), Point(0.0, 2.0)),
        Wall(Point(0.0, 2.0), Point(2.0, 4.0)),
        Wall(Point(2.0, 4.0), Point(4.0, 2.0)),
        Wall(Point(4.0, 2.0), Point(2.0, 0.0)),

        //inner square
        Wall(Point(2.0, 1.0), Point(1.0, 2.0)),
        Wall(Point(1.0, 2.0), Point(2.0, 3.0)),
        Wall(Point(2.0, 3.0), Point(3.0, 2.0)),
        Wall(Point(3.0, 2.0), Point(2.0, 1.0))
    )))


    "findCollisionWithWalls" must "collide with horizontal line at (1.0,2.0)" in {
        val collsionPoint: Point = map.findCollisionWithWalls(new Line(0.0, 1.0, -2), Point(2, 2), 180.0).get
        assert(collsionPoint.x == 1.0)
        assert(collsionPoint.y == 2.0)
    }

    it must "collide with horizontal line at (3.0,2.0)" in {
        val collsionPoint: Point = map.findCollisionWithWalls(new Line(0.0, 1.0, -2), Point(2, 2), 0.0).get
        assert(collsionPoint.x == 3.0)
        assert(collsionPoint.y == 2.0)
    }

    it must "collide with vertical line at (3.0,2.0)" in {
        val collsionPoint: Point = map.findCollisionWithWalls(new Line(1.0, 0.0, -2), Point(2, 2), 90.0).get
        assert(collsionPoint.x == 2.0)
        assert(collsionPoint.y == 3.0)
    }

    it must "collide with vertical line at (2.0,1.0)" in {
        val collsionPoint: Point = map.findCollisionWithWalls(new Line(1.0, 0.0, -2), Point(2, 2), 270.0).get
        assert(collsionPoint.x == 2.0)
        assert(collsionPoint.y == 1.0)
    }

    it must "collide at (2.5,1.5)" in {
        val collsionPoint: Point = map.findCollisionWithWalls(new Line(1.0, 1.0, -4), Point(2, 2), 315.0).get
        assert(collsionPoint.x == 2.5)
        assert(collsionPoint.y == 1.5)
    }

    it must "collide at (2.5,2.5)" in {
        val collsionPoint: Point = map.findCollisionWithWalls(new Line(-1.0, 1.0, 0), Point(2, 2), 45.0).get
        assert(collsionPoint.x == 2.5)
        assert(collsionPoint.y == 2.5)
    }

    it must "collide at (1.5,1.5)" in {
        val collsionPoint: Point = map.findCollisionWithWalls(new Line(-1.0, 1.0, 0), Point(2, 2), 225.0).get
        assert(collsionPoint.x == 1.5)
        assert(collsionPoint.y == 1.5)
    }
}
