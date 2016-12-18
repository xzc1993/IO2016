package com.agh.io.map

import java.io.File

import spray.json._

object MapProtocol extends DefaultJsonProtocol {
    implicit val pointFormat = jsonFormat2(Point)
    implicit val wallFormat = jsonFormat2(Wall)
    implicit val mapDataFormat = jsonFormat1(MapData)
}

class MapLoader(mapDataFile: File) {
    import MapProtocol._

    def load(): Map = {
        val content = scala.io.Source.fromFile(mapDataFile).mkString
        val mapData = content.parseJson.convertTo[MapData]
        val mapDataInMillimeters = MapData(mapData.walls.map(_.scale(1000)))
        new Map(mapDataInMillimeters)
    }
}
