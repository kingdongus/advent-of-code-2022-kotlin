package year2022.day06

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    fun String.positionOfFirstDistinctSubstring(ofLength: Int): Int {
        for (i in ofLength - 1 until this.length)
            if (this.substring(i - ofLength + 1, i + 1).toSet().size == ofLength) return i + 1
        return -1 // not found
    }

    fun part1(input: String): Int = input.positionOfFirstDistinctSubstring(4)
    fun part2(input: String): Int = input.positionOfFirstDistinctSubstring(14)
    
    val testInput = readTestFileByYearAndDay(2022, 6)
    check(part1(testInput[0]) == 7)
    check(part2(testInput[0]) == 19)

    val input = readInputFileByYearAndDay(2022, 6)
    println(part1(input[0]))
    println(part2(input[0]))
}
