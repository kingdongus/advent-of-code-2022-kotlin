package year2021.day05

import Point
import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    fun countOverlappingPoints(input: List<String>, lineCondition: (Point, Point) -> Boolean): Int =
        input.asSequence()
            .map { it.split(" -> ") }
            .map { it[0].split(",") to it[1].split(",") }
            .map {
                Point(it.first[0].toInt(), it.first[1].toInt()) to Point(
                    it.second[0].toInt(), it.second[1].toInt()
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
