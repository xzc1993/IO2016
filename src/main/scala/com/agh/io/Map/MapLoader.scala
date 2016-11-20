package com.agh.io.Map

import java.io.File
import java.util

import com.google.gson.Gson

import scala.collection.JavaConverters._

class MapLoader(mapDataFile: File) {

    def load(): Map = {
        val content: String = scala.io.Source.fromFile(mapDataFile).mkString
        val gson = new Gson
        val loadedData = gson.fromJson(content, classOf[MapData])
        val dataInMillimeters = MapData(new util.ArrayList(loadedData.walls.asScala.map(_.scale(1000)).asJava))
        new Map(dataInMillimeters)
    }

}
