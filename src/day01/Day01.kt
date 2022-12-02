package day01

import readInput

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
            }
            else partialSum += it.toInt()
        }
        // dangling element
        partialSums += partialSum

        return partialSums.sorted().reversed().take(3).sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day01/day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("day01/day01_input")
    println(part1(input))
    println(part2(input))
}
