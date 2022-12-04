package year2021.day01

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {
    fun part1(input: List<String>): Int {
        var prev = input.first().toInt()
        var numIncreases = 0
        input.drop(1)
            .map { it.toInt() }
            .forEach {
                if (it > prev) numIncreases++
                prev = it
            }
        return numIncreases
    }

    fun part2(input: List<String>): Int {
        val ints = input.map { it.toInt() }
        var numIncreases = 0
        var windowSum = ints.take(3).sum()
        for (i in 3 until ints.size) {
            val temp = ints.subList(i - 2, i + 1).sum()
            if (temp > windowSum) numIncreases++
            windowSum = temp
        }
        return numIncreases
    }

    val testInput = readTestFileByYearAndDay(2021, 1)
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInputFileByYearAndDay(2021, 1)
    println(part1(input))
    println(part2(input))
}
