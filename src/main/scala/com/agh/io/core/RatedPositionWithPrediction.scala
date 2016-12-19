package com.agh.io.core

case class RatedPositionWithPrediction(ratedPosition: RatedPosition, nextPositionPrediction: Position) {
    def difference = ratedPosition.position - nextPositionPrediction

    def differenceNorm = ratedPosition.position.point.getDistanceToPoint(nextPositionPrediction.point) // TODO angle ignored for now
}
