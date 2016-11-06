package com.agh.io.Map

import java.io.File
import javax.imageio.ImageIO

class MapLoader {

    val PATH = "data/maze.png";

    def load(): Map = {
        new Map(ImageIO.read(new File(PATH)))
    }

}
