package com.agh.io.Map

class Map(val data: MapData) {
    import com.agh.io.Util.MathUtils._

    def getMapWidth: Double = data.walls.map(_.getMaxX()).max

    def getMapHeight: Double = data.walls.map(_.getMaxY()).max

    def findCollisionWithWalls(sensorLine: Line, startingPoint: Point, angle: Double): Option[Point] = {
        var bestCollisionPoint: Point = null
        var collisionPoint: Point = null
        var collisionDistance: Double = Double.NaN
        var bestCollisionDistance: Double = Double.PositiveInfinity
        for (wall <- data.walls) {
            LineCalculator.getCrossingPoint(sensorLine, wall.getLine()).foreach {
                collisionPoint =>
                if( _checkIfCollisionPointIsOnGoodSideOfRobot(angle, collisionPoint, startingPoint)
                    && _checkIfCollidedWithWall(wall, collisionPoint)){
                    collisionDistance = collisionPoint.getDistanceToPoint(startingPoint)
                    if( collisionDistance < bestCollisionDistance){
                        bestCollisionPoint = collisionPoint
                        bestCollisionDistance = collisionDistance
                    }
                }
            }
        }
        Option(bestCollisionPoint)
    }

    def _checkIfCollisionPointIsOnGoodSideOfRobot(angle: Double, collisionPoint: Point, startingPoint: Point): Boolean = {
        if( 0.0 <= angle && angle < 90.0){
            collisionPoint.x >=~ startingPoint.x && collisionPoint.y >=~ startingPoint.y
        }
        else if( 90.0 <= angle && angle < 180.0) {
            collisionPoint.x <=~ startingPoint.x && collisionPoint.y >=~ startingPoint.y
        }
        else if( 180.0 <= angle && angle < 270.0) {
            collisionPoint.x <=~ startingPoint.x && collisionPoint.y <=~ startingPoint.y
        }
        else { //if( 270.0 >= angle && angle < 360.0) {
            collisionPoint.x >=~ startingPoint.x && collisionPoint.y <=~ startingPoint.y
        }
    }

    def _checkIfCollidedWithWall(wall: Wall, collisionPoint: Point): Boolean = {
        (wall.getMinX() <=~ collisionPoint.x && collisionPoint.x <=~ wall.getMaxX()
            && wall.getMinY() <=~ collisionPoint.y && collisionPoint.y <=~ wall.getMaxY())
    }
}
