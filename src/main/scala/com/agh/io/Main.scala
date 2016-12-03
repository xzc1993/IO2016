package com.agh.io

import com.agh.io.Configuration.CommandLineParser
import com.agh.io.Core._
import com.agh.io.Map.{Map, MapLoader, Point}
import com.agh.io.Sensor.{SensorLoader, SensorScan}

import scala.collection.mutable

object Main extends App {
    val configuration = new CommandLineParser().load(args)
    val map = new MapLoader(configuration.mapDataFile).load()
    val sensor = new SensorLoader(configuration.sensorDataFile).load()
    val sensorScan = sensor.scans(configuration.nodeId)
    val fitnessCalculator = new FitnessCalculator(sensorScan, configuration.sensorParameters, map)
    val clusterer = new Clusterer(map)
    val annealer = new Annealer(map, fitnessCalculator)

    val IterationCount = 500000 // TODO optimize; as params?
    val AcceptableGuessThreshold = 1.1

    def computeHypothesisClusters = {
        var guessNo = 0
        var bestPosition = RatedPosition(Position(Point(0, 0), 0), Double.PositiveInfinity)

        val guessesByWorst = new mutable.PriorityQueue[RatedPosition]

        while (guessNo < IterationCount) {
            guessNo += 1
            val position = PositionRandomizer.getRandomPositionOnMap(map)
            val fitness = fitnessCalculator.calculateFitness(position)
            val ratedPosition = RatedPosition(position, fitness)
            if (ratedPosition.fitness < AcceptableGuessThreshold * bestPosition.fitness) {
                guessesByWorst += ratedPosition
                if (ratedPosition.fitness < bestPosition.fitness) {
                    bestPosition = ratedPosition
                    while (guessesByWorst.head.fitness >= AcceptableGuessThreshold * bestPosition.fitness) {
                        guessesByWorst.dequeue()
                    }
                }
            }

            if (guessNo % 10000 == 0) { // TODO proper logging?
                println(f"guess: $guessNo%9d, acceptable guess count: ${guessesByWorst.length}%6d")
                println

                guessesByWorst.foreach(guess => printPositionStats(map, guess, sensorScan))
                println

                val errors = fitnessCalculator.calculateDistanceReadingErrors(bestPosition.position)
                println(s"best guess errors: ${errors.map(e => f"$e%6.1f").mkString(", ")}")
                println

                val clusters = clusterer.cluster(guessesByWorst.toSeq)
                clusters.foreach(c => println(f"cluster (${c.positions.size}%2d, centroid: ${c.centroid}): " +
                    s"${c.positions.map(_.position).mkString(", ")}"))
                println
                println("-------------------------------------------------------------------------------------------")
                println
            }
        }

        clusterer.cluster(guessesByWorst.toSeq)
    }

    def printPositionStats(map: Map, position: RatedPosition, sensorScan: SensorScan) = {
        val errorStats = fitnessCalculator.calculateErrorStats(position.position)
        println(f"position: (${position.position.point.x}%6.1f, ${position.position.point.y}%6.1f), " +
            f"angle: ${position.position.angle}%6.2f, fitness: ${position.fitness}%9.1f, " +
            f"min error: ${errorStats.min}%6.1f, max error: ${errorStats.max}%6.1f, " +
            f"mean error: ${errorStats.mean}%6.1f, error stddev: ${errorStats.stdDev}%6.1f")
    }

    println(s"Starting computation for reading #${configuration.nodeId}")
    println
    val mapDrafter = new MapDrafter(map)
    mapDrafter.drawPath(Set(
        new Position(new Point(1500.0,2450.0), 90.0),
        new Position(new Point(0.0,0.0), 90.0)
    ))

    val clusters = computeHypothesisClusters

//    // -i 201 -s data/DaneLabirynt2.csv -m data/MazeRoboLabFullMap.roson
//    val c40 = Seq(Position(Point(1937.0683043771332,1952.5670391434935),93.32994643569604), Position(Point(1872.7263720571646,1960.7214830692467),91.79573555914028), Position(Point(1871.3451525053058,1948.5915188354975),97.72100802347092), Position(Point(1932.1853753395344,1939.4922586696139),93.77618849662103), Position(Point(1863.3058005752507,1820.8927667147304),88.69352909563058), Position(Point(1917.4300078497506,2021.8685835434749),96.94295330954266), Position(Point(1916.8441064207232,2145.1771874189762),104.90449189719334), Position(Point(1855.7780354506644,2030.163773280228),96.83355930308109), Position(Point(1900.126313521689,1954.856955927682),94.93061335664956), Position(Point(1800.480716688845,1934.7072151808752),91.10023206145179), Position(Point(1816.4172484003639,1845.01540143968),86.86355105410206), Position(Point(1982.2491558996305,2072.3694767283027),101.15028962978278), Position(Point(1899.079899016608,1856.4756981033074),89.86277891221403), Position(Point(1883.9120106559278,2080.060703434353),101.58960136360126), Position(Point(1828.4188670147457,1813.5235259257527),86.91192930456496), Position(Point(1877.7883199345952,1857.402065405593),89.37926288231412), Position(Point(1881.5969112386256,1803.7976836340822),87.08191096168811), Position(Point(1888.888991569856,2070.0800890259607),101.70261586148949), Position(Point(1859.876819358365,2023.8453356511393),95.60267782877528), Position(Point(1835.2062575657267,1913.0166314629644),89.86421419653142), Position(Point(1820.3838031911782,1853.2753274284044),88.05392756680128), Position(Point(1884.0814063355408,2008.353193180754),96.74425751333783), Position(Point(1768.972832689756,2008.879115421065),96.73566126328848), Position(Point(1781.9090982723055,1851.4001851224375),84.94945695886412), Position(Point(1788.871110633554,1782.1885122342917),83.78933866066068), Position(Point(1770.4316633886694,1886.032953719292),87.29126730674797), Position(Point(1858.2483640266012,2155.5398380586735),106.29523801055267), Position(Point(1712.7596308614702,1766.543699511733),81.51703058812501), Position(Point(1723.4547615277088,1748.8992696108267),79.83574367193235), Position(Point(1893.50279860409,2188.0552934292605),109.07755902496295), Position(Point(1917.3914708863704,2212.121628863107),107.24996913360098), Position(Point(1890.454976758184,2208.771723450154),108.95078025895157), Position(Point(1948.2178547803321,2188.1584188768475),107.60304639951349), Position(Point(1654.7247945600238,1862.8960187470664),83.42381090622837), Position(Point(1685.2700897039213,1821.9032300699698),81.31172296688183), Position(Point(1701.4230854198222,1891.507454398665),85.76924480053067), Position(Point(1729.8795470452674,1738.721936258907),78.28985328573688), Position(Point(1577.9422805864483,1750.4500112215053),73.28181955369473), Position(Point(1626.085584133944,1754.6861814109625),76.60747062359033), Position(Point(1590.7351610137332,1752.8925978131736),74.2476506049609))
//    val c08 = Seq(Position(Point(2027.957517876917,1771.3471615532255),112.69831245683015), Position(Point(2071.6583140467433,1858.6092564160913),116.98664256140282), Position(Point(2092.60439397278,1954.8127052871973),120.4022528763844), Position(Point(2062.7012843291145,1916.7111032001787),119.0291274853552), Position(Point(2103.677229556201,1978.1549925689187),120.0409475883423), Position(Point(2083.967357209792,1904.1159430147745),118.13420834961622), Position(Point(2079.4177730014003,1986.3199254786057),121.79390419484228), Position(Point(2051.7874869990756,2051.7036014819023),124.0934141520324))
//    val c05 = Seq(Position(Point(1479.7925707433608,2035.642619038837),195.6431093174788), Position(Point(1397.3710759518804,2040.9910305747626),202.0531630354248), Position(Point(1478.1513537016735,1982.2546755032056),192.78614868952897), Position(Point(1442.2721585891811,2042.8813424418406),196.93195234210773), Position(Point(1407.1239608528167,2080.4897134618254),201.67328380908225))
//    val c03 = Seq(Position(Point(947.5575035099836,2835.0168583211835),16.79875457583583), Position(Point(942.2495845670508,2797.2431964954017),19.137142246567358), Position(Point(1017.8897635739979,2889.457662282464),12.458598433049035))
//    val clusters = Seq(c40, c08, c05, c03).map(c => Cluster(c.map(p => RatedPosition(p, fitnessCalculator.calculateFitness(p)))))

    clusters.foreach(cluster => {
        val centroid = cluster.centroid // TODO take best sample(s) from the cluster instead of the centroid?
        val ratedCentroid = RatedPosition(centroid, fitnessCalculator.calculateFitness(centroid))
        val annealedPosition = annealer.anneal(ratedCentroid)
        println(s"centroid before annealing: $ratedCentroid")
        println(s"position after annealing:  $annealedPosition")
        println
    })
}