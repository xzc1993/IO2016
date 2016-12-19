package com.agh.io.output

import java.awt.geom._
import java.awt.image.{AffineTransformOp, BufferedImage}
import java.awt.{BasicStroke, Color, Graphics2D}

import com.agh.io.core.{RatedPosition, RatedPositionWithPrediction}
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

    def drawPath(positionsWithPredictions: Seq[RatedPositionWithPrediction]): Unit = {
        val maxMotionModelDifference = positionsWithPredictions.map(_.differenceNorm).max
        val positions = positionsWithPredictions.map(_.ratedPosition)
        val maxFitness = positions.map(_.fitness).max
        createImage(() => {
            drawMap()
            drawTrace(positionsWithPredictions, maxMotionModelDifference)
            positions.foreach(drawRobot(_, maxFitness))
            positions.foreach(drawAngle)
        })
    }


    private def createImage(draftingFunction: () => Unit): Unit = {
        canvas = new BufferedImage(imageSize._1 + 5, imageSize._2 + 5, BufferedImage.TYPE_INT_RGB)

        graphics = canvas.createGraphics()
        graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)

        graphics.setStroke(new BasicStroke(StrokeWidth))

        draftingFunction()

        val tx = AffineTransform.getScaleInstance(1, -1)
        tx.translate(0, -canvas.getHeight(null))
        val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
        canvas = op.filter(canvas, null)

        graphics.dispose()
        javax.imageio.ImageIO.write(canvas, "png", new java.io.File("map.png"))
    }

    private def drawMap() = {
        graphics.setColor(Color.BLACK)
        graphics.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

        graphics.setStroke(new BasicStroke())
        graphics.setColor(Color.CYAN)
        map.data.walls.foreach(wall => drawLine(wall.from.x, wall.from.y, wall.to.x, wall.to.y))
    }

    private def drawTrace(positionsWithPredictions: Seq[RatedPositionWithPrediction], maxMotionModelDifference: Double): Unit = {
        positionsWithPredictions.reduce((positionA: RatedPositionWithPrediction, positionB: RatedPositionWithPrediction) => {
            setErrorColor(positionB.differenceNorm / maxMotionModelDifference)
            drawLine(positionA.ratedPosition.position.point.x, positionA.ratedPosition.position.point.y, positionB.ratedPosition.position.point.x, positionB.ratedPosition.position.point.y)
            positionB
        })
    }

    private def drawRobot(position: RatedPosition, maxFitness: Double) = {
        setErrorColor(position.fitness / maxFitness)
        graphics.fill(new Ellipse2D.Double((position.position.point.x / mapWidth) * ImageWidth - RobotRadius/2, (position.position.point.y / mapHeight) * imageHeight - RobotRadius/2, RobotRadius, RobotRadius))
    }

    private def setErrorColor(errorFraction: Double): Unit = {
        val r = Math.min(2.0 * errorFraction, 1.0)
        val g = Math.min(2.0 * (1.0 - errorFraction), 1.0)
        val b = 0.0
        graphics.setColor(new Color(r.toFloat, g.toFloat, b.toFloat))
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

    private def drawLine(x1: Double, y1: Double, x2: Double, y2: Double): Unit = {
        graphics.draw(new Line2D.Double(scaleWidth(x1), scaleHeight(y1), scaleWidth(x2), scaleHeight(y2)))
    }

    private def scaleWidth(width: Double) = width * widthScale

    private def scaleHeight(height: Double) = height * heightScale

    private val mapWidth = map.getMapWidth
    private val mapHeight = map.getMapHeight
    private val heightToWidthRatio = mapHeight / mapWidth

    private val ImageWidth = 600
    private val RobotRadius = 7
    private val StrokeWidth = 10
    private val RayLength = 200
    private val imageHeight = (ImageWidth * heightToWidthRatio).toInt
    private val imageSize = (ImageWidth, imageHeight)
    private val widthScale = ImageWidth / mapWidth
    private val heightScale = imageHeight / mapHeight
}
