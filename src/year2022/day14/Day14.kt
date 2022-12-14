package year2022.day14

import Point
import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.max
import kotlin.math.min

class Cave(val dimension: Point) {

    // we go cheap and model the field as characters, similar to the visualization on the page
    var fieldState: Array<CharArray> = arrayOf(CharArray(1) { '.' })
    var leftEdge = Int.MAX_VALUE
    var rightEdge = -1
    var bottomEdge = -1

    init {
        fieldState = Array(dimension.y) { CharArray(dimension.x) { '.' } }
    }

    fun addRocks(locations: List<Point>) {
        // very brittle code, does no checks against the dimensions
        locations.forEach { fieldState[it.y][it.x] = '#' }
        rightEdge = max(rightEdge, locations.maxOf { it.x })
        leftEdge = min(leftEdge, locations.minOf { it.y })
        bottomEdge = max(bottomEdge, locations.maxOf { it.y })
    }

    private fun isOutOfBounds(position: Point) =
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

    private fun nextPosition(sand: Point): Point {
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
        val spawnPoint = Point(0, 500)
        val directions = listOf(Point(1, 0), Point(1, -1), Point(1, 1))
    }
}

fun main() {
    fun parseRockLocations(input: List<String>): List<Point> {
        val rockLocations = input.map { it.split(" -> ") }
            .map {
                it.map { numbers -> numbers.split(",") }
                    .map { number -> Point(number.first().toInt(), number.last().toInt()) }
            }.flatMap { it.dropLast(1).zip(it.drop(1)) }
            .flatMap { it.first..it.second }
            .distinct()
        return rockLocations
    }

    fun part1(input: List<String>): Int {
        val rockLocations = parseRockLocations(input)
        val cave = Cave(dimension = Point(rockLocations.maxOf { it.x } + 1, rockLocations.maxOf { it.y } + 1))
        cave.addRocks(rockLocations)
        var maxSand = 0
        while (cave.addSand()) maxSand++
        return maxSand
    }

    fun part2(input: List<String>): Int {
        val rockLocations = parseRockLocations(input).toMutableList()
        val depthFromReadings = rockLocations.maxOf { it.y }
        // we manually add another layer of rocks slightly lower than whatever the reading tells us
        // when the description says "infinite in both directions", it really meant the range 0..9999
        rockLocations.addAll((0 until 1000).map { Point(it, depthFromReadings + 2) })
        val cave = Cave(dimension = Point(rockLocations.maxOf { it.x } + 1, rockLocations.maxOf { it.y } + 1))
        cave.addRocks(rockLocations)
        var maxSand = 0
        while (cave.addSand()) maxSand++
        return maxSand + 1 // +1 since the code will not allow us to block the sand spawn point
    }

    val testInput = readTestFileByYearAndDay(2022, 14)

    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInputFileByYearAndDay(2022, 14)
    println(part1(input))
    println(part2(input))
}
