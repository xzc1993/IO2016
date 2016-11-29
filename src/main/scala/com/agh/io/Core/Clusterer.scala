package com.agh.io.Core

import com.agh.io.Map.Map
import org.apache.commons.math3.ml.clustering.{Clusterable, DBSCANClusterer}

import scala.collection.JavaConverters._
import scala.collection.mutable

class Clusterer(val map: Map) {
    private val Epsilon = 20.0 // TODO parameterize if needed
    private val MinClusterSize = 1
    private val IncludeOutliers = true

    def cluster(positions: Seq[RatedPosition]): Seq[Cluster] = {
        val wrappedPositions = positions.map(ClusteringWrapper).asJava
        val clusterer = new DBSCANClusterer[ClusteringWrapper](Epsilon, MinClusterSize)
        val wrappedClusters = clusterer.cluster(wrappedPositions)
        val clusters = wrappedClusters.asScala.map(wc => Cluster(wc.getPoints.asScala.map(_.ratedPosition)))
        if (IncludeOutliers) addOutliers(clusters, positions)
        clusters
    }

    private def addOutliers(clusters: mutable.Buffer[Cluster], positions: Seq[RatedPosition]): Unit = {
        val clusteredPositions = clusters.flatMap(_.positions).toSet
        val outliers = positions.toSet -- clusteredPositions
        outliers.foreach(outlier => clusters += Cluster(Seq(outlier)))
    }

    private case class ClusteringWrapper(ratedPosition: RatedPosition) extends Clusterable {
        val normalizedPoint = Array(
            (ratedPosition.position.point.x / map.getMapWidth) * 360.0,
            (ratedPosition.position.point.y / map.getMapHeight) * 360.0,
            ratedPosition.position.angle
        )
        override def getPoint = normalizedPoint
    }
}