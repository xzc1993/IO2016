package com.agh.io.Map
import scala.collection.JavaConverters._

class Map(data: MapData) {

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

    val mapWidth = findMaxX(data.walls.asScala.toArray[Wall])
    val mapHeight = findMaxY(data.walls.asScala.toArray[Wall])

    def getMapWidth(): Double = {
        mapWidth
    }

    def getMapHeight(): Double = {
        mapHeight
    }

    def findCollisionWithWalls(sensorLine: Line, startingPoint: Point): Point = {
        var bestCollisionPoint: Point = null
        var collisionPoint: Point = null
        var collisionDistance: Double = Double.NaN
        var bestCollisionDistance: Double = Double.PositiveInfinity
        for(wall: Wall <- data.walls.asScala.toArray[Wall] ) {
            collisionPoint = LineCalculator.getCrossingPoint(sensorLine, wall.getLine())
            if( _checkIfCollisionPointIsOnGoodSideOfRobot(sensorLine, collisionPoint, startingPoint)){
                collisionDistance = collisionPoint.getDistanceToPoint(startingPoint)
                if( collisionDistance < bestCollisionDistance){
                    bestCollisionPoint = collisionPoint
                    bestCollisionDistance = collisionDistance
                }
            }
        }
        bestCollisionPoint
    }

    def _checkIfCollisionPointIsOnGoodSideOfRobot(sensorLine: Line, collisionPoint: Point, startingPoint: Point): Boolean = {
        if( sensorLine.getNormalizedA() < 0.0){
            (collisionPoint.x >= startingPoint.x)
        }
        else{
            collisionPoint.x <= startingPoint.x
        }
    }
}
