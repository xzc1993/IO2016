package com.agh.io.core

import java.awt.geom._
import java.awt.image.{AffineTransformOp, BufferedImage}
import java.awt.{BasicStroke, Color, Graphics2D}

import com.agh.io.map.Map

class MapDrafter(map: Map) {
    var canvas: BufferedImage = _
    var graphics: Graphics2D = _

    def draw(position: RatedPosition): Unit = {
        createImage(() => {
            drawMap()
            drawRobot(position, maxFitness = position.fitness)
            drawAngle(position)
        })
    }

    def drawPath(positions: Seq[RatedPosition]): Unit = {
        val maxFitness = positions.map(_.fitness).max
        createImage(() => {
            drawMap()
            drawTrace(positions)
            positions.foreach(drawRobot(_, maxFitness))
            positions.foreach(drawAngle)
        })
    }

    private def createImage(draftingFunction: () => Unit): Unit = {
        canvas = new BufferedImage(imageSize._1 + 5, imageSize._2 + 5, BufferedImage.TYPE_INT_RGB)

        graphics = canvas.createGraphics()
        graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)

        draftingFunction()

        val tx = AffineTransform.getScaleInstance(1, -1)
        tx.translate(0, -canvas.getHeight(null))
        val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
        canvas = op.filter(canvas, null)

        graphics.dispose()
        javax.imageio.ImageIO.write(canvas, "png", new java.io.File("map.png"))
    }

    private def drawMap() = {
        graphics.setColor(Color.WHITE)
        graphics.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

        graphics.setStroke(new BasicStroke())
        graphics.setColor(Color.BLUE)
        map.data.walls.foreach(wall => drawLine(wall.from.x, wall.from.y, wall.to.x, wall.to.y))
    }

    private def drawTrace(positions: Seq[RatedPosition]): RatedPosition = {
        graphics.setColor(Color.RED)
        positions.reduce((positionA: RatedPosition, positionB: RatedPosition) => {
            drawLine(positionA.position.point.x, positionA.position.point.y, positionB.position.point.x, positionB.position.point.y)
            positionB
        })
    }

    private def drawRobot(position: RatedPosition, maxFitness: Double) = {
        setColorWithError(position, maxFitness)
        graphics.fill(new Ellipse2D.Double((position.position.point.x / mapWidth) * imageWidth - robotRadius/2, (position.position.point.y / mapHeight) * imageHeight - robotRadius/2, robotRadius, robotRadius))
    }

    private def drawAngle(position: RatedPosition): Unit = {
        graphics.setColor(Color.GREEN)
        val x = position.position.point.x
        val y = position.position.point.y
        val angle = Math.toRadians(position.position.angle)
        val ray_x = RayLength * math.cos(angle)
        val ray_y = RayLength * math.sin(angle)
        drawLine(x, y, x + ray_x, y + ray_y)
    }

    private def setColorWithError(position: RatedPosition, maxFitness: Double): Unit = {
        graphics.setColor(new Color(255, 192 - ((position.fitness / maxFitness) * 128).toInt, 0))
    }

    private def drawLine(x1: Double, y1: Double, x2: Double, y2: Double): Unit = {
        graphics.draw(new Line2D.Double(scaleWidth(x1), scaleHeight(y1), scaleWidth(x2), scaleHeight(y2)))
    }

    private def scaleWidth(width: Double) = width * widthScale

    private def scaleHeight(height: Double) = height * heightScale

    private val mapWidth = map.getMapWidth
    private val mapHeight = map.getMapHeight
    private val heightToWidthRatio = mapHeight / mapWidth

    private val imageWidth = 477
    private val robotRadius = 6
    private val RayLength = 200
    private val imageHeight = (imageWidth * heightToWidthRatio).toInt
    private val imageSize = (imageWidth, imageHeight)
    private val widthScale = imageWidth / mapWidth
    private val heightScale = imageHeight / mapHeight
}
