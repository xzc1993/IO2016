package com.agh.io.Core

import java.awt.geom._
import java.awt.image.{AffineTransformOp, BufferedImage}
import java.awt.{BasicStroke, Color}

import com.agh.io.Map.Map

class MapDrafter(map: Map) {
    def draw(position: Position): Unit = {
        var canvas = new BufferedImage(imageSize._1 + 5, imageSize._2 + 5, BufferedImage.TYPE_INT_RGB)

        val graphics = canvas.createGraphics()

        graphics.setColor(Color.WHITE)
        graphics.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

        graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)

        graphics.setStroke(new BasicStroke())
        graphics.setColor(Color.BLUE)
        map.data.walls.foreach(wall => {
            val x1 = (wall.from.x / mapWidth) * imageWidth
            val y1 = (wall.from.y / mapHeight) * imageHeight
            val x2 = (wall.to.x / mapWidth) * imageWidth
            val y2 = (wall.to.y / mapHeight) * imageHeight
            graphics.draw(new Line2D.Double(x1, y1, x2, y2))
        })

        graphics.setColor(Color.RED)
        graphics.fill(new Ellipse2D.Double((position.point.x / mapWidth) * imageWidth, (position.point.y / mapHeight) * imageHeight, 10.0, 10.0))

        val tx = AffineTransform.getScaleInstance(1, -1)
        tx.translate(0, -canvas.getHeight(null))
        val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
        canvas = op.filter(canvas, null)

        graphics.dispose()

        javax.imageio.ImageIO.write(canvas, "png", new java.io.File("map.png"))
    }

    private val mapWidth = map.getMapWidth
    private val mapHeight = map.getMapHeight
    private val heightToWidthRatio = mapHeight / mapWidth

    private val imageWidth = 477
    private val imageHeight = (imageWidth * heightToWidthRatio).toInt
    private val imageSize = (imageWidth, imageHeight)
}
