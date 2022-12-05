package year2022.day05

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {


    fun moveCrates(input: List<String>, multipleAtOnce: Boolean = false): Array<String> {
        val delimiter = input.takeWhile { it.isNotBlank() }.size
        val numStacks = input.getOrElse(delimiter - 1) { "" }.split(" ").last().toInt()

        val piles: Array<String> = Array(numStacks) { "" } // we treat piles of crates as single strings
        input.take(delimiter - 1) // excludes numberings
            .map { it.chunked(4) }
            .forEach {
                it.forEachIndexed { index, s ->
                    if (s.isNotBlank()) piles[index] = piles[index] + s[1]
                }
            }

        input.drop(delimiter + 1)
            .map { Regex("[0-9]+").findAll(it) }
            .map { it.map { v -> v.value } }
            .map { it.toList() }
            .forEach {
                val count = it[0].toInt()
                val from = it[1].toInt() - 1
                val to = it[2].toInt() - 1

                val toMove = if (multipleAtOnce) piles[from].take(count)
                else piles[from].take(count).reversed()  // one-at-a-time reverses the order
                piles[from] = piles[from].drop(count)
                piles[to] = toMove + piles[to]
            }
        return piles
    }

    fun part1(input: List<String>): String {
        val parsedStacks: Array<String> = moveCrates(input)
        return parsedStacks.map { it.first() }.joinToString(separator = "")
    }


    fun part2(input: List<String>): String {
        val parsedStacks: Array<String> = moveCrates(input, multipleAtOnce = true)
        return parsedStacks.map { it.first() }.joinToString(separator = "")
    }


    val testInput = readTestFileByYearAndDay(2022, 5)
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInputFileByYearAndDay(2022, 5)
    println(part1(input))
    println(part2(input))
}
