package year2022.day01

import partitionBy
import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {
    fun part1(input: List<String>): Int =
        input.partitionBy { it.isBlank() }
            .map { it.map { s -> s.toInt() } }
            .maxOf { it.sum() }

    fun part2(input: List<String>): Int =
        input.partitionBy { it.isBlank() }
            .asSequence()
            .map { it.map { s -> s.toInt() } }
            .map { it.sum() }
            .sortedDescending()
            .take(3)
            .sum()
    
    val testInput = readTestFileByYearAndDay(2022, 1)
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInputFileByYearAndDay(2022, 1)
    println(part1(input))
    println(part2(input))
}
