package year2022.day14

import Point2D
import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.max
import kotlin.math.min

class Cave(val dimension: Point2D) {

    // we go cheap and model the field as characters, similar to the visualization on the page
    private var fieldState: Array<CharArray> = arrayOf(CharArray(1) { '.' })
    private var leftEdge = Int.MAX_VALUE
    private var rightEdge = -1
    private var bottomEdge = -1

    init {
        fieldState = Array(dimension.y) { CharArray(dimension.x) { '.' } }
    }

    fun addRocks(locations: List<Point2D>) {
        // very brittle code, does no checks against the dimensions
        locations.forEach { fieldState[it.y][it.x] = '#' }
        rightEdge = max(rightEdge, locations.maxOf { it.x })
        leftEdge = min(leftEdge, locations.minOf { it.y })
        bottomEdge = max(bottomEdge, locations.maxOf { it.y })
    }

    private fun isOutOfBounds(position: Point2D) =
        position.y < leftEdge || position.y > rightEdge || position.x > bottomEdge

    // true if sand was placed, false if it fell into the void OR would block the spawn
    fun addSand(): Boolean {
        var position = spawnPoint
        while (true) {
            val next = nextPosition(position)
            if (isOutOfBounds(next)) return false
            if (spawnPoint == next) return false
            if (position == next) {
                fieldState[next.x][next.y] = 'o'
                return true
            }
            position = next
        }
    }

    private fun nextPosition(sand: Point2D): Point2D {
        for (direction in directions) {
            val newPosition = sand.moveBy(direction)
            if (isOutOfBounds(newPosition)) return newPosition
            if (fieldState[newPosition.x][newPosition.y] == '.') {
                return newPosition
            }
        }
        return sand
    }

    companion object {
        val spawnPoint = Point2D(0, 500)
        val directions = listOf(Point2D(1, 0), Point2D(1, -1), Point2D(1, 1))
    }
}

fun main() {
    fun parseRockLocations(input: List<String>): List<Point2D> {
        val rockLocations = input.map { it.split(" -> ") }
            .map {
                it.map { numbers -> numbers.split(",") }
                    .map { number -> Point2D(number.first().toInt(), number.last().toInt()) }
            }.flatMap { it.dropLast(1).zip(it.drop(1)) }
            .flatMap { it.first..it.second }
            .distinct()
        return rockLocations
    }

    fun part1(input: List<String>): Int =
        parseRockLocations(input).let {
            val cave = Cave(dimension = Point2D(it.maxOf { it.x } + 1, it.maxOf { it.y } + 1))
            cave.addRocks(it)
            cave
        }.let {
            var maxSand = 0
            while (it.addSand()) maxSand++
            return maxSand
        }

    fun part2(input: List<String>): Int =
        parseRockLocations(input).toMutableList()
            .also {
                val depthFromReadings = it.maxOf { it.y }
                // we manually add another layer of rocks slightly lower than whatever the reading tells us
                // when the description says "infinite in both directions", it really meant the range 0..9999
                it.addAll((0 until 1000).map { Point2D(it, depthFromReadings + 2) })
            }
            .let {
                val cave = Cave(dimension = Point2D(it.maxOf { it.x } + 1, it.maxOf { it.y } + 1))
                cave.addRocks(it)
                cave
            }.let {
                var maxSand = 0
                while (it.addSand()) maxSand++
                return maxSand + 1// +1 since the code will not allow us to block the sand spawn point
            }

    val testInput = readTestFileByYearAndDay(2022, 14)

    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInputFileByYearAndDay(2022, 14)
    println(part1(input))
    println(part2(input))
}
