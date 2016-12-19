package com.agh.io.core

import com.agh.io.map.Point

// Based on pl.edu.agh.capo.robot.CapoRobotMotionModel
// TODO copy-pasted from Java, refactor
class RobotMotionModelPositionCalculator(val position: Position, var velocityLeft: Double, var velocityRight: Double) {
    /**
      * First calculate the Center of the circle:
      * arcCenter = [x − R sin(θ) , y + R cos(θ)]
      * Then calculate new position:
      * rotating a distance R about its ICC with an angular velocity of ω.
      * <p>
      * http://chess.eecs.berkeley.edu/eecs149/documentation/differentialDrive.pdf
      *
      * @param deltaTime in seconds
      */
    def getPositionAfterTime(deltaTime: Double): Position = {
        if (!checkFeasibility) return position
        val radius: Double = getArcRadius
        if (radius == Double.PositiveInfinity) {
            Position(Point(position.point.x + getLinearVelocity * Math.cos(position.angle * deltaTime), position.point.y + getLinearVelocity * Math.sin(position.angle * deltaTime)), position.angle)
        } else {
            val arcCenterX: Double = position.point.x - radius * Math.sin(position.angle)
            val arcCenterY: Double = position.point.y + radius * Math.cos(position.angle)
            val angularVelocityDeltaTime: Double = getAngularVelocity * deltaTime
            val newX: Double = Math.cos(angularVelocityDeltaTime) * (position.point.x - arcCenterX) - Math.sin(angularVelocityDeltaTime) * (position.point.y - arcCenterY) + arcCenterX
            val newY: Double = Math.sin(angularVelocityDeltaTime) * (position.point.x - arcCenterX) + Math.cos(angularVelocityDeltaTime) * (position.point.y - arcCenterY) + arcCenterY
            Position(Point(newX, newY), position.angle + angularVelocityDeltaTime)
        }
    }

    /// <summary>
    /// Corrects robot velocity, if it exceedes maxLinearVelocity value.
    ///
    /// TODO - cannot just restrict wheels velocity, linear v of the robot is to be restricted
    /// </summary>
    /// <returns></returns>
    private def checkFeasibility(): Boolean = {
        if (Math.abs(velocityLeft) < RobotMotionModelPositionCalculator.maxLinearVelocity && Math.abs(velocityRight) < RobotMotionModelPositionCalculator.maxLinearVelocity) return true
        val divider: Double = Math.max(Math.abs(velocityLeft), Math.abs(velocityRight)) / RobotMotionModelPositionCalculator.maxLinearVelocity
        velocityLeft /= divider
        velocityRight /= divider
        false
    }

    private def getArcRadius: Double = {
        if (Math.abs(velocityLeft - velocityRight) < RobotMotionModelPositionCalculator.verySmallDouble) return Double.PositiveInfinity
        RobotMotionModelPositionCalculator.wheelsHalfDistance * (velocityLeft + velocityRight) / (velocityLeft - velocityRight)
    }

    private def getLinearVelocity: Double = (velocityLeft + velocityRight) / 2

    private def getAngularVelocity: Double = (velocityLeft - velocityRight) / (2 * RobotMotionModelPositionCalculator.wheelsHalfDistance)
}

// TODO copy-pasted from Java, refactor
object RobotMotionModelPositionCalculator {
    val maxLinearVelocity: Double = 1000.0
    val wheelsHalfDistance: Double = 140
    val robotDiameter: Double = 500
    val robotHalfDiameter: Double = robotDiameter / 2
    val verySmallDouble: Double = 0.1 // nedded in some calculations; 0.1 milimeter is assumed smaller than accuracy
}