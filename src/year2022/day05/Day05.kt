package year2022.day05

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {


    fun moveCrates(input: List<String>, multipleAtOnce: Boolean = false): Array<String> {
        var delimiter = 0
        while (input.getOrElse(delimiter) {} != "") delimiter++

        val numStacks = input.getOrElse(delimiter - 1) { "" }.split(" ").last().toInt()
        val parsedStacks: Array<String> = Array(numStacks) { "" }

        input.take(delimiter - 1) // excludes numberings
            .map { it.chunked(4) }
            .forEach {
                it.forEachIndexed { index, s ->
                    if (s.isNotBlank()) parsedStacks[index] = parsedStacks[index] + s[1]
                }
            }
        input.drop(delimiter + 1).forEach {
            val numbers = Regex("[0-9]+").findAll(it)
                .map(MatchResult::value)
                .toList()

            val count = numbers[0].toInt()
            val from = numbers[1].toInt() - 1
            val to = numbers[2].toInt() - 1

            val toMove = if (multipleAtOnce) parsedStacks[from].take(count) else parsedStacks[from].take(count)
                .reversed()  // one-at-a-time reverses the order
            parsedStacks[from] = parsedStacks[from].drop(count)
            parsedStacks[to] = toMove + parsedStacks[to]
        }
        return parsedStacks
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
