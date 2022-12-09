package year2022.day09

import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.abs

fun main() {

    data class Point(var x: Int, var y: Int) {

        infix fun isAdjacentTo(other: Point): Boolean {
            return abs(this.x - other.x) <= 1 && abs(this.y - other.y) <= 1
        }

        fun moveBy(delta: Point) {
            this.x += delta.x
            this.y += delta.y
        }

        fun moveTowards(other: Point) {
            if (this == other || this isAdjacentTo other) return

            // move by 1 in direction of dx and dy each
            val newX = this.x + (if (other.x > this.x) 1 else if (other.x < this.x) -1 else 0)
            val newY = this.y + (if (other.y > this.y) 1 else if (other.y < this.y) -1 else 0)

            this.x = newX
            this.y = newY
        }
    }

    fun calculateRopeMovements(headMovements: List<String>, ropeLength: Int = 2): MutableSet<Pair<Int, Int>> {
        val rope = Array(ropeLength) { Point(0, 0) }
        val head = rope.first()
        val tail = rope.last()

        val track = mutableSetOf<Pair<Int, Int>>()

        headMovements.map { it.split(" ") }
            .map { it.first() to it.last().toInt() }
            .forEach {
                val direction =
                    when (it.first) {
                        "U" -> Point(0, 1)
                        "D" -> Point(0, -1)
                        "L" -> Point(-1, 0)
                        "R" -> Point(1, 0)
                        else -> Point(0, 0)
                    }
                repeat(it.second) {
                    head.moveBy(direction)
                    rope.indices.drop(1).forEach { i ->
                        rope[i].moveTowards(rope[i - 1])
                    }
                    track.add(tail.x to tail.y)
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