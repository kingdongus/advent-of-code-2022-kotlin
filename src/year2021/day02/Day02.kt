package year2021.day02

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {
    fun part1(input: List<String>): Int {

        fun sumNumbersByPrefix(input: List<String>, prefix: String): Int = input.filter { it.startsWith(prefix) }
            .map { it.split(" ")[1] }
            .map { it.toInt() }
            .sum()

        val horizontal = sumNumbersByPrefix(input, "forward")
        val depth = sumNumbersByPrefix(input, "down") - sumNumbersByPrefix(input, "up")

        return horizontal * depth
    }

    fun part2(input: List<String>): Int {
        var aim = 0
        var depth = 0
        var horizontal = 0

        input.forEach {
            val parts = it.split(" ")
            val magnitude = parts[1].toInt()
            when (parts[0]) {
                "up" -> aim += magnitude
                "down" -> aim -= magnitude
                else -> {
                    horizontal += magnitude
                    depth -= aim * magnitude
                }
            }
        }

        return horizontal * depth
    }

    val testInput = readTestFileByYearAndDay(2021, 2)
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInputFileByYearAndDay(2021, 2)
    println(part1(input))
    println(part2(input))
}
