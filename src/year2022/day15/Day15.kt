package year2022.day15

import Point2D
import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.abs

fun main() {
    data class Reading(val signal: Point2D, val closestBeacon: Point2D) {
        fun reach(): Int = signal manhattanDistance closestBeacon
        fun minReachableY(): Int = signal.y - reach()
        fun maxReachableY(): Int = signal.y + reach()

        infix fun canReachY(toReach: Int): Boolean =
            if (this.signal.y > toReach) minReachableY() <= toReach else maxReachableY() >= toReach

        infix fun canReach(other: Point2D): Boolean = (this.signal manhattanDistance other) <= this.reach()

        infix fun intersectWithY(y: Int): List<Point2D> {
            if (!this.canReachY(y)) return listOf()

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

    // sorry
    fun parseReadings(input: List<String>): List<Reading> {
        val readings = input.map { line ->
            line.split(":")
                .map { it.split("at ") }
                .map { it.last() }
                .flatMap { it.split(",") }
                .map { it.split("=") }
                .map { it.last() }
                .map { it.toInt() }
        }.map { Reading(signal = Point2D(it[0], it[1]), closestBeacon = Point2D(it[2], it[3])) }
            .toList()
        return readings
    }

    fun part1(input: List<String>, y: Int): Int {

        val readings = parseReadings(input)
        val beaconsOnTargetDepth = readings.map { it.closestBeacon }.filter { it.y == y }
        val readingsInReach = readings.filter { it canReachY y }
        return readingsInReach.flatMap { it intersectWithY y }
            .distinct()
            .filterNot { beaconsOnTargetDepth.contains(it) }
            .size
    }

    fun part2(input: List<String>, searchSpace: Int): Long {
        val readings = parseReadings(input)

        for (reading in readings) {
            for (candidate in reading.expandManhattanCircleBy(1)) {
                if (candidate.x < 0 || candidate.y < 0 || candidate.x > searchSpace || candidate.y > searchSpace) continue
                if (readings.any { it canReach candidate }) continue
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
