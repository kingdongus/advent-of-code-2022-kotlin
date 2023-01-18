package year2022.day15

import Point2D
import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.abs

data class Reading(val signal: Point2D, val closestBeacon: Point2D) {
    private fun reach(): Int = signal manhattanDistance closestBeacon
    private fun minReachableY(): Int = signal.y - reach()
    private fun maxReachableY(): Int = signal.y + reach()

    infix fun canReachY(toReach: Int): Boolean =
        if (this.signal.y > toReach) minReachableY() <= toReach else maxReachableY() >= toReach

    infix fun canReach(other: Point2D): Boolean = (this.signal manhattanDistance other) <= this.reach()

    infix fun intersectWithY(y: Int): List<Point2D> {
        if (!(this canReachY y)) return listOf()

        val dyToY = abs(signal.y - y)
        val offsetXMin = -(reach() - dyToY)
        val offsetXMax = reach() - dyToY

        return Point2D(signal.x + offsetXMin, y)..Point2D(signal.x + offsetXMax, y)
    }

    infix fun expandManhattanCircleBy(by: Int): List<Point2D> {
        val radius = (this.signal manhattanDistance this.closestBeacon) + by
        val result = mutableListOf<Point2D>()

        result.addAll(
            Point2D(this.signal.x, this.signal.y + radius)..Point2D(
                this.signal.x + radius,
                this.signal.y
            )
        )
        result.addAll(
            Point2D(this.signal.x + radius, this.signal.y)..Point2D(
                this.signal.x,
                this.signal.y - radius
            )
        )
        result.addAll(
            Point2D(this.signal.x, this.signal.y - radius)..Point2D(
                this.signal.x - radius,
                this.signal.y
            )
        )
        result.addAll(
            Point2D(this.signal.x - radius, this.signal.y)..Point2D(
                this.signal.x,
                this.signal.y + radius
            )
        )

        return result.distinct()
    }
}

fun main() {

    val regex = """Sensor at x=(-?\d*), y=(-?\d*): closest beacon is at x=(-?\d*), y=(-?\d*)""".toRegex()

    // this is much slower than the previous repeated String::split, but looks nicer
    fun parseReadings(input:List<String>):List<Reading> = input.asSequence()
            .map { regex.find(it) }
            .map {
                Reading(
                    signal = Point2D(it!!.groupValues.get(1).toInt(), it!!.groupValues.get(2).toInt()),
                    closestBeacon = Point2D(it!!.groupValues.get(3).toInt(), it!!.groupValues.get(4).toInt()))
            }
            .toList()


    fun part1(input: List<String>, y: Int): Int = parseReadings(input).let {
        val beaconsOnTargetDepth = it.map { it.closestBeacon }.filter { it.y == y }
        val readingsInReach = it.filter { reading -> reading canReachY y }
            readingsInReach.flatMap { it intersectWithY y }
                .distinct()
                .filterNot { beaconsOnTargetDepth.contains(it) }
                .size
        }


    fun part2(input: List<String>, searchSpace: Int): Long = parseReadings(input).let {
        for (reading in it) {
            for (candidate in reading.expandManhattanCircleBy(1)) {
                if (candidate.x < 0 || candidate.y < 0 || candidate.x > searchSpace || candidate.y > searchSpace) continue
                if (it.any { r -> r canReach candidate }) continue
                return (candidate.x.toLong() * 4000000) + candidate.y
            }
        }
        return -1
    }

    val testInput = readTestFileByYearAndDay(2022, 15)
    check(part1(testInput, 10) == 26)
    check(part2(testInput, 20) == 56000011L)

    val input = readInputFileByYearAndDay(2022, 15)
    println(part1(input, 2_000_000))
    println(part2(input, 4_000_000))
}
