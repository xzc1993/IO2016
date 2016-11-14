package com.agh.io.Map
import scala.collection.JavaConverters._

class Map(val data: MapData) {

    def findMaxX(a: Array[Wall]) : Double = {
        a.foldLeft(a(0).getMaxX()) {
            case (max, e) => Math.max(max, e.getMaxX())
        }
    }

    def findMaxY(a: Array[Wall]) : Double = {
        a.foldLeft(a(0).getMaxY()) {
            case (max, e) => Math.max(max, e.getMaxY())
        }
    }

    def getMapWidth(): Double = {
        findMaxX(data.walls.asScala.toArray[Wall])
    }

    def getMapHeight(): Double = {
        findMaxY(data.walls.asScala.toArray[Wall])
    }

    def findCollisionWithWalls(sensorLine: Line, startingPoint: Point, angle: Double): Point = {
        var bestCollisionPoint: Point = null
        var collisionPoint: Point = null
        var collisionDistance: Double = Double.NaN
        var bestCollisionDistance: Double = Double.PositiveInfinity
        for(wall: Wall <- data.walls.asScala.toArray[Wall] ) {
            collisionPoint = LineCalculator.getCrossingPoint(sensorLine, wall.getLine())
            if( _checkIfCollisionPointIsOnGoodSideOfRobot(angle, collisionPoint, startingPoint)){
                collisionDistance = collisionPoint.getDistanceToPoint(startingPoint)
                if( collisionDistance < bestCollisionDistance){
                    bestCollisionPoint = collisionPoint
                    bestCollisionDistance = collisionDistance
                }
            }
        }
        bestCollisionPoint
    }

    def _checkIfCollisionPointIsOnGoodSideOfRobot(angle: Double, collisionPoint: Point, startingPoint: Point): Boolean = {
        if( angle >= 90.0 && angle < 270.0){
            collisionPoint.x <= startingPoint.x
        }
        else{
            collisionPoint.x >= startingPoint.x
        }
    }
}
