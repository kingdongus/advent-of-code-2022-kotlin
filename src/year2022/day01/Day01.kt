package year2022.day01

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {
    fun part1(input: List<String>): Int {
        var partialSum = 0
        var largest = 0

        input.forEach {
            if (it.isBlank()) partialSum = 0
            else partialSum += it.toInt()
            if (partialSum > largest) largest = partialSum
        }
        return largest
    }

    fun part2(input: List<String>): Int {
        var partialSum = 0
        val partialSums = mutableListOf<Int>()

        input.forEach {
            if (it.isBlank()) {
                partialSums += partialSum
                partialSum = 0
            } else partialSum += it.toInt()
        }
        // dangling element
        partialSums += partialSum

        return partialSums.sorted().reversed().take(3).sum()
    }

    val testInput = readTestFileByYearAndDay(2022, 1)
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInputFileByYearAndDay(2022, 1)
    println(part1(input))
    println(part2(input))
}
