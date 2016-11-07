package com.agh.io.Map

import java.io.File
import javax.imageio.ImageIO

class MapLoader(mapDataFile: File) {

    def load(): Map = {
        new Map(ImageIO.read(mapDataFile))
    }

}
