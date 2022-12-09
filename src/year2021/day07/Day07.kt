package year2021.day07

import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.abs

fun main() {


    fun part1(input: List<String>): Int {
        val crabPositions = input[0].split(",").map { it.toInt() }
        return (crabPositions.min()..crabPositions.max())
            .map { crabPositions.sumOf { p -> abs(it - p) } }
            .min()
    }


    fun part2(input: List<String>): Int {
        val crabPositions = input[0].split(",").map { it.toInt() }
        return (crabPositions.min()..crabPositions.max())
            .map { crabPositions.sumOf { p -> (0..abs(it - p)).sum() } }
            .min()
    }

    val testInput = readTestFileByYearAndDay(2021, 7)
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInputFileByYearAndDay(2021, 7)
    println(part1(input))
    println(part2(input))
}
