package com.agh.io.Map

import java.awt.image.BufferedImage


class Map(img: BufferedImage) {

    def getPixel(x: Int, y: Int): Int = {
        img.getRGB(x,y)
    }
}
