package year2022.day05

import partitionBy
import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {


    fun moveCrates(input: List<String>, multipleAtOnce: Boolean = false): Array<String> {

        val cratesAndInstructions = input.partitionBy { it.isBlank() }

        val initialCrates = cratesAndInstructions.first()
        val numStacks = initialCrates.last().split(" ").last().toInt()

        val workspace: Array<String> = Array(numStacks) { "" } // we treat piles of crates as single strings
        initialCrates
            .dropLast(1)// excludes numberings
            .map { it.chunked(4) }
            .forEach {
                it.forEachIndexed { index, s ->
                    if (s.isNotBlank()) workspace[index] = workspace[index] + s[1]
                }
            }

        // process instructions
        cratesAndInstructions.last()
            .map { Regex("[0-9]+").findAll(it) }
            .map { it.map { v -> v.value } }
            .map { it.toList() }
            .map { it.map { i -> i.toInt() } }.toList()
            .forEach {
                val count = it[0]
                val from = it[1] - 1
                val to = it[2] - 1

                val toMove = if (multipleAtOnce) workspace[from].take(count)
                else workspace[from].take(count).reversed()  // one-at-a-time reverses the order
                workspace[from] = workspace[from].drop(count)
                workspace[to] = toMove + workspace[to]
            }
        return workspace
    }

    fun part1(input: List<String>): String = moveCrates(input).map { it.first() }.joinToString(separator = "")
    fun part2(input: List<String>): String =
        moveCrates(input, multipleAtOnce = true).map { it.first() }.joinToString(separator = "")


    val testInput = readTestFileByYearAndDay(2022, 5)
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInputFileByYearAndDay(2022, 5)
    println(part1(input))
    println(part2(input))
}
