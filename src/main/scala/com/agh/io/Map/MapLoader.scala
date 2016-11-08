package com.agh.io.Map

import java.io.File

import com.google.gson.Gson

class MapLoader(mapDataFile: File) {

    def load(): Map = {
        val content: String = scala.io.Source.fromFile(mapDataFile).mkString
        val gson = new Gson
        val tmp: MapData = gson.fromJson(content, classOf[MapData])
        new Map(tmp)
    }

}
