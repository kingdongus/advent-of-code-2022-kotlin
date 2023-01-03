package year2022.day17

import Point2D
import readInputFileByYearAndDay
import readTestFileByYearAndDay

val shapes = mapOf(
    0 to setOf(Point2D(0, 0), Point2D(1, 0), Point2D(2, 0), Point2D(3, 0)), // plank
    1 to setOf(Point2D(1, 0), Point2D(0, 1), Point2D(1, 1), Point2D(2, 1), Point2D(1, 2)), // cross
    2 to setOf(Point2D(0, 0), Point2D(1, 0), Point2D(2, 0), Point2D(2, 1), Point2D(2, 2)), // inverse L
    3 to setOf(Point2D(0, 0), Point2D(0, 1), Point2D(0, 2), Point2D(0, 3)), // stick
    4 to setOf(Point2D(0, 0), Point2D(0, 1), Point2D(1, 0), Point2D(1, 1)) // block
)

infix fun Set<*>.containsAny(other: Set<*>): Boolean = other.any { this.contains(it) }

// 2 from left, "3 free rows" from top
val spawnOffset = Point2D(2, 4)
fun spawnRock(id: Int, top: Int): Set<Point2D> =
    shapes[id]!!.map { it + spawnOffset }.map { it + Point2D(0, top) }.toSet()


fun moveLeft(points: Set<Point2D>): Set<Point2D> =
    if (points.any { it.x == 0 }) points // on left edge
    else points.map { Point2D(it.x - 1, it.y) }.toSet()

fun moveRight(points: Set<Point2D>): Set<Point2D> =
    if (points.any { it.x == 6 }) points  // on right edge
    else points.map { Point2D(it.x + 1, it.y) }.toSet()

fun moveDown(points: Set<Point2D>): Set<Point2D> = points.map { Point2D(it.x, it.y - 1) }.toSet()

fun moveUp(points: Set<Point2D>): Set<Point2D> = points.map { Point2D(it.x, it.y + 1) }.toSet()

fun topRowsNormalized(rocks: Set<Point2D>, offset: Int): Set<Point2D> {
    val maxY = rocks.maxOf { it.y }
    return rocks.filter { it.y >= maxY - offset }.map { Point2D(it.x, maxY - it.y) }.toSet()
}

data class MemoryItem(val rocks: Set<Point2D>, val steamIndex: Int, val shapeIndex: Int)

fun calculateHighestColumn(input: List<String>, numRocks: Long): Long {
    val steam = input.first()
    val placedRocks = (0..6).map { Point2D(it, 0) }.toMutableSet() // bottom row
    var top = 0
    var idxSteam = 0
    var idxRock = 0

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

                val memoryItem = MemoryItem(topRowsNormalized(placedRocks, 40), idxSteam % steam.length, idxRock % 5)
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

fun main() {

    val testInput = readTestFileByYearAndDay(2022, 17)
    check(calculateHighestColumn(testInput, 2022) == 3068L)
    check(calculateHighestColumn(testInput, 1_000_000_000_000) == 1514285714288L)

    val input = readInputFileByYearAndDay(2022, 17)
    println(calculateHighestColumn(input, 2022))
    println(calculateHighestColumn(input, 1_000_000_000_000))
}
