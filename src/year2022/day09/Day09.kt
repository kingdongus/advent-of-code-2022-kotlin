package year2022.day09

import Point2D
import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    infix fun Point2D.moveTowards(other: Point2D): Point2D {
        if (this == other || this isAdjacentTo other) return this

        // move by 1 in direction of dx and dy each
        val newX = this.x + (if (other.x > this.x) 1 else if (other.x < this.x) -1 else 0)
        val newY = this.y + (if (other.y > this.y) 1 else if (other.y < this.y) -1 else 0)

        return Point2D(newX, newY)
    }

    fun calculateRopeMovements(headMovements: List<String>, ropeLength: Int = 2): MutableSet<Pair<Int, Int>> {
        val rope = Array(ropeLength) { Point2D(0, 0) }
        val track = mutableSetOf<Pair<Int, Int>>()

        headMovements.map { it.split(" ") }
            .map { it.first() to it.last().toInt() } // direction string and step count
            .forEach {
                val direction =
                    when (it.first) {
                        "U" -> Point2D(0, 1)
                        "D" -> Point2D(0, -1)
                        "L" -> Point2D(-1, 0)
                        "R" -> Point2D(1, 0)
                        else -> Point2D(0, 0)
                    }
                repeat(it.second) {
                    rope[0] = rope[0].moveBy(direction)
                    rope.indices.drop(1).forEach { i ->
                        rope[i] = rope[i] moveTowards rope[i - 1]
                    }
                    track.add(rope.last().x to rope.last().y)
                }
            }
        return track
    }

    fun part1(input: List<String>): Int = calculateRopeMovements(input).size

    fun part2(input: List<String>): Int = calculateRopeMovements(input, ropeLength = 10).size

    val testInput = readTestFileByYearAndDay(2022, 9)
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)

    val input = readInputFileByYearAndDay(2022, 9)
    println(part1(input))
    println(part2(input))
}