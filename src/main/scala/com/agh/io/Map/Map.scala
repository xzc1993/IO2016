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
}
