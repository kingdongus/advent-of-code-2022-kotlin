package year2022.day17

import Point
import readInputFileByYearAndDay
import readTestFileByYearAndDay

interface Shape {
    fun edgePoints(): Set<Point>
}

class Plank : Shape {
    override fun edgePoints(): Set<Point> {
        return setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0))
    }
}

class Cross : Shape {
    override fun edgePoints(): Set<Point> {
        return setOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2))
    }
}

class InverseL : Shape {
    override fun edgePoints(): Set<Point> {
        return setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, 1), Point(2, 2))
    }
}

class Stick : Shape {
    override fun edgePoints(): Set<Point> {
        return setOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3))
    }
}

class Block : Shape {
    override fun edgePoints(): Set<Point> {
        return setOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1))
    }
}


infix fun Set<*>.containsAny(other: Set<*>): Boolean = other.any { this.contains(it) }

fun main() {

    // 2 from left, 3 from top
    fun spawnRock(id: Int, top: Int): Set<Point> = when (id) {
        0 -> Plank().edgePoints().map { Point(it.x + 2, it.y + top + 4) }.toSet()
        1 -> Cross().edgePoints().map { Point(it.x + 2, it.y + top + 4) }.toSet()
        2 -> InverseL().edgePoints().map { Point(it.x + 2, it.y + top + 4) }.toSet()
        3 -> Stick().edgePoints().map { Point(it.x + 2, it.y + top + 4) }.toSet()
        else -> Block().edgePoints().map { Point(it.x + 2, it.y + top + 4) }.toSet()
    }

    fun moveLeft(points: Set<Point>): Set<Point> {
        if (points.any { it.x == 0 }) return points
        return points.map { Point(it.x - 1, it.y) }.toSet()
    }

    fun moveRight(points: Set<Point>): Set<Point> {
        if (points.any { it.x == 6 }) return points
        return points.map { Point(it.x + 1, it.y) }.toSet()
    }

    fun moveDown(points: Set<Point>): Set<Point> {
        return points.map { Point(it.x, it.y - 1) }.toSet()
    }

    fun moveUp(points: Set<Point>): Set<Point> {
        return points.map { Point(it.x, it.y + 1) }.toSet()
    }

    fun topRowsNormalized(rocks: Set<Point>): Set<Point> {
        val maxY = rocks.maxOf { it.y }
        val offset = 40
        return rocks.filter { it.y >= maxY - offset }.map { Point(it.x, maxY - it.y) }.toSet()
    }

    data class MemoryItem(val rocks: Set<Point>, val steamIndex: Int, val shapeIndex: Int)

    fun part1(input: List<String>): Long {
        val steam = input.first()
        val placedRocks = (0..6).map { Point(it, 0) }.toMutableSet() // bottom row
        var top = 0
        var idxSteam = 0
        var idxRock = 0

        while (idxRock < 2022) {

            var rock = spawnRock(idxRock++ % 5, top)

            // get pushed -> fall down
            var settled = false
            while (!settled) {
                // get pushed
                when (steam[idxSteam++ % steam.length]) {
                    '>' -> {
                        rock = moveRight(rock)
                        val collided = placedRocks containsAny rock
                        if (collided) rock = moveLeft(rock)
                    }

                    '<' -> {
                        rock = moveLeft(rock)
                        val collided = placedRocks containsAny rock
                        if (collided) rock = moveRight(rock)
                    }

                    else -> throw IllegalStateException("que pasa")
                }
                // fall down
                rock = moveDown(rock)
                val collided = placedRocks containsAny rock
                if (collided) {
                    // undo, then move to placedRocks
                    rock = moveUp(rock)
                    placedRocks.addAll(rock)
                    top = placedRocks.maxOf { it.y }
                    settled = true
                }
            }
        }

        return top.toLong()
    }

    fun part2(input: List<String>): Long {
        val steam = input.first()
        val placedRocks = (0..6).map { Point(it, 0) }.toMutableSet() // bottom row
        var top = 0
        var idxSteam = 0
        var idxRock = 0

        val numRocks = 1_000_000_000_000
        var totalRocksCalculated = 0L
        var addedTop = 0L

        val memory = mutableMapOf<MemoryItem, Pair<Int, Int>>()

        while (totalRocksCalculated < numRocks) {

            var rock = spawnRock(idxRock % 5, top)

            // get pushed -> fall down
            var settled = false
            while (!settled) {
                // get pushed
                when (steam[idxSteam % steam.length]) {
                    '>' -> {
                        rock = moveRight(rock)
                        val collided = placedRocks containsAny rock
                        if (collided) rock = moveLeft(rock)
                    }

                    '<' -> {
                        rock = moveLeft(rock)
                        val collided = placedRocks containsAny rock
                        if (collided) rock = moveRight(rock)
                    }

                    else -> throw IllegalStateException("que pasa")
                }
                // fall down
                rock = moveDown(rock)
                val collided = placedRocks containsAny rock
                if (collided) {
                    // undo, then move to placedRocks
                    rock = moveUp(rock)
                    placedRocks.addAll(rock)
                    top = placedRocks.maxOf { it.y }

                    val memoryItem = MemoryItem(topRowsNormalized(placedRocks), idxSteam % steam.length, idxRock % 5)
                    if (memory.containsKey(memoryItem)) {
                        // found cycle
                        val (oldIdxRock, oldTop) = memory[memoryItem]!!
                        val deltaTop = top - oldTop
                        val deltaRocks = idxRock - oldIdxRock
                        // this is how often we add the repeating pattern "on top"
                        val repetitions = (numRocks - totalRocksCalculated) / deltaTop
                        addedTop += repetitions * deltaTop
                        // skip ahead - once we find a pattern, we insert it as often as possible, then skip ahead to the end
                        // and calc what is missing
                        totalRocksCalculated += repetitions * deltaRocks
                    }
                    memory[memoryItem] = idxRock to top
                    settled = true
                }
                idxSteam += 1
            }
            idxRock += 1
            totalRocksCalculated += 1
        }

        return top.toLong() + addedTop
    }

    val testInput = readTestFileByYearAndDay(2022, 17)
    check(part1(testInput) == 3068L)
    check(part2(testInput) == 1514285714288L)

    val input = readInputFileByYearAndDay(2022, 17)
    println(part1(input))
    println(part2(input))
}
