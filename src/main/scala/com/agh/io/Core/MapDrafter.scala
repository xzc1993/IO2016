package com.agh.io.Core

import java.awt.geom._
import java.awt.image.{AffineTransformOp, BufferedImage}
import java.awt.{BasicStroke, Color, Graphics2D}

import com.agh.io.Map.Map

class MapDrafter(map: Map) {

    def draw(position: Position): Unit = {
        var canvas = new BufferedImage(imageSize._1 + 5, imageSize._2 + 5, BufferedImage.TYPE_INT_RGB)

        val graphics = canvas.createGraphics()
        graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)

        drawMap(canvas, graphics)
        drawRobot(position, graphics)

        val tx = AffineTransform.getScaleInstance(1, -1)
        tx.translate(0, -canvas.getHeight(null))
        val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
        canvas = op.filter(canvas, null)

        graphics.dispose()
        javax.imageio.ImageIO.write(canvas, "png", new java.io.File("map.png"))
    }

    def drawPath(positions: List[Position]): Unit = {
        var canvas = new BufferedImage(imageSize._1 + 5, imageSize._2 + 5, BufferedImage.TYPE_INT_RGB)

        val graphics = canvas.createGraphics()
        graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)

        drawMap(canvas, graphics)
        graphics.setColor(Color.RED)
        positions.reduce((positionA: Position, positionB: Position) => {
            val x1 = (positionA.point.x / mapWidth) * imageWidth
            val y1 = (positionA.point.y / mapHeight) * imageHeight
            val x2 = (positionB.point.x / mapWidth) * imageWidth
            val y2 = (positionB.point.y / mapHeight) * imageHeight
            graphics.draw(new Line2D.Double(x1, y1, x2, y2))
            positionB
        })
        positions.foreach(position => {
            drawRobot(position, graphics)
        })

        val tx = AffineTransform.getScaleInstance(1, -1)
        tx.translate(0, -canvas.getHeight(null))
        val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
        canvas = op.filter(canvas, null)

        graphics.dispose()
        javax.imageio.ImageIO.write(canvas, "png", new java.io.File("map.png"))
    }

    private def drawMap(canvas: BufferedImage, graphics: Graphics2D) = {
        graphics.setColor(Color.WHITE)
        graphics.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

        graphics.setStroke(new BasicStroke())
        graphics.setColor(Color.BLUE)
        map.data.walls.foreach(wall => {
            val x1 = (wall.from.x / mapWidth) * imageWidth
            val y1 = (wall.from.y / mapHeight) * imageHeight
            val x2 = (wall.to.x / mapWidth) * imageWidth
            val y2 = (wall.to.y / mapHeight) * imageHeight
            graphics.draw(new Line2D.Double(x1, y1, x2, y2))
        })
    }

    private def drawRobot(position: Position, graphics: Graphics2D) = {
        graphics.setColor(Color.RED)
        graphics.fill(new Ellipse2D.Double((position.point.x / mapWidth) * imageWidth - robotRadius/2, (position.point.y / mapHeight) * imageHeight - robotRadius/2, robotRadius, robotRadius))
    }

    private val mapWidth = map.getMapWidth
    private val mapHeight = map.getMapHeight
    private val heightToWidthRatio = mapHeight / mapWidth

    private val imageWidth = 477
    private val robotRadius = 10
    private val imageHeight = (imageWidth * heightToWidthRatio).toInt
    private val imageSize = (imageWidth, imageHeight)
}
