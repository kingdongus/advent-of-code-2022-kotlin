package year2021.day06

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    // move each fish one age group farther down
    // for each fish that would go below 0, reset them to 6 and add one fish at 8
    fun ageFishPopulation(fishByAge: MutableMap<Int, Long>, days: Int) =
        repeat(days) {
            val newFish = fishByAge.getOrDefault(0, 0)
            (1..8).forEach { fishByAge[it - 1] = fishByAge.getOrDefault(it, 0) }
            fishByAge[6] = fishByAge.getOrDefault(6, 0) + newFish
            fishByAge[8] = newFish
        }

    // for each possible age, count the number of fish, and return as MutableMap
    fun parseInputToFish(input: String) =
        (0 until 8).associateWith {
            input.split(",")
                .map { c -> c.toInt() }
                .count { c -> c == it }
                .toLong()
        }.toMutableMap()

    fun part1(input: List<String>): Long {
        val fishByAge = parseInputToFish(input[0])
        ageFishPopulation(fishByAge, 80)
        return fishByAge.values.sum()
    }


    fun part2(input: List<String>): Long {
        val fishByAge = parseInputToFish(input[0])
        ageFishPopulation(fishByAge, 256)
        return fishByAge.values.sum()
    }

    val testInput = readTestFileByYearAndDay(2021, 6)
    check(part1(testInput) == 5934L)
    check(part2(testInput) == 26984457539)

    val input = readInputFileByYearAndDay(2021, 6)
    println(part1(input))
    println(part2(input))
}
