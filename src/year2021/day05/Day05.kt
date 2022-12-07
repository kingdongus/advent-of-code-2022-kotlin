package year2021.day05

import readInputFileByYearAndDay
import readTestFileByYearAndDay
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs

fun main() {

    data class Point(val x: Int, val y: Int) {
        infix fun isHorizontalTo(other: Point): Boolean = this.y == other.y
        infix fun isVerticalTo(other: Point): Boolean = this.x == other.x
        infix fun isDiagonalTo(other: Point): Boolean = abs(this.x - other.x) == abs(this.y - other.y)
        fun moveBy(x: Int, y: Int): Point = Point(this.x + x, this.y + y)
        operator fun rangeTo(other: Point): List<Point> {
            return if (this isHorizontalTo other) {
                val minX = min(this.x, other.x)
                val maxX = max(this.x, other.x)
                (minX until maxX + 1).map { Point(it, this.y) }.toList()
            } else if (this isVerticalTo other) {
                val minY = min(this.y, other.y)
                val maxY = max(this.y, other.y)
                (minY until maxY + 1).map { Point(this.x, it) }.toList()
            } else if (this isDiagonalTo other) {
                val dirX = if (this.x > other.x) -1 else 1
                val dirY = if (this.y > other.y) -1 else 1
                (0 until abs(this.x - other.x) + 1).map { this.moveBy(it * dirX, it * dirY) }.toList()
            } else listOf()
        }
    }

    fun countOverlappingPoints(input: List<String>, lineCondition: (Point, Point) -> Boolean): Int =
        input.asSequence()
            .map { it.split(" -> ") }
            .map { it[0].split(",") to it[1].split(",") }
            .map {
                Point(it.first[0].toInt(), it.first[1].toInt()) to Point(
                    it.second[0].toInt(),
                    it.second[1].toInt()
                )
            }
            .filter { lineCondition(it.first, it.second) }
            .map { it.first..it.second }
            .flatten()
            .groupBy { it }
            .filter { (it.value.size >= 2) }
            .count()

    fun part1(input: List<String>): Int =
        countOverlappingPoints(input) { a, b -> a isHorizontalTo b || a isVerticalTo b }

    fun part2(input: List<String>): Int =
        countOverlappingPoints(input) { a, b -> a isHorizontalTo b || a isVerticalTo b || a isDiagonalTo b }

    val testInput = readTestFileByYearAndDay(2021, 5)
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInputFileByYearAndDay(2021, 5)
    println(part1(input))
    println(part2(input))
}
