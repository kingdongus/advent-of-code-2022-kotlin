package year2022.day04

import readInputFileByYearAndDay
import readTestFileByYearAndDay

// ctor expects Strings such as
// 100-200
// no validation of any kind is done
class InclusiveInterval(formatted: String) {
    val start: Int
    val end: Int

    init {
        formatted.split("-").apply {
            start = this[0].toInt()
            end = this[1].toInt()
        }
    }

    infix fun contains(other: InclusiveInterval): Boolean = other.start >= this.start && other.end <= this.end
    infix fun overlaps(other: InclusiveInterval): Boolean =
        (other.start <= this.end && other.end >= this.end) ||
                (this.start <= other.end && this.end >= other.end)

}

fun main() {

    fun part1(input: List<String>): Int =
        input.map { it.split(",") }
            .map { InclusiveInterval(it[0]) to InclusiveInterval(it[1]) }
            .count { it.first contains it.second || it.second contains it.first }


    fun part2(input: List<String>): Int =
        input.map { it.split(",") }
            .map { InclusiveInterval(it[0]) to InclusiveInterval(it[1]) }
            .count { it.first overlaps it.second }


    val testInput = readTestFileByYearAndDay(2022, 4)
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInputFileByYearAndDay(2022, 4)
    println(part1(input))
    println(part2(input))
}
