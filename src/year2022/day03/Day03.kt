package year2022.day03

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    fun Char.toPriority(): Int {
        if (this.code >= 'A'.code && this.code < 'a'.code)
            return this.code - 'A'.code + 27
        else if (this.code >= 'a'.code && this.code <= 'z'.code)
            return this.code - 'a'.code + 1
        return 0
    }

    infix fun String.intersect(other: String): String = (this.toSet() intersect other.toSet()).joinToString("")

    fun part1(input: List<String>): Int =
        input.asSequence()
            .map { it.substring(0, it.length / 2) to it.substring(it.length / 2) }
            .map { it.first intersect it.second }
            .map { it.first() }
            .sumOf { it.toPriority() }

    fun part2(input: List<String>): Int =
        input.chunked(3)
            .map { it.reduce { acc, s -> acc intersect s } }
            .map { it[0] }
            .sumOf { it.toPriority() }

    val testInput = readTestFileByYearAndDay(2022, 3)
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInputFileByYearAndDay(2022, 3)
    println(part1(input))
    println(part2(input))
}