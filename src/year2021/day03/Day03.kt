package year2021.day03

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    /**
     * Treats the input as list of binary numbers of equal length l, and returns a map that, for each position
     * 0...l-1, records how many bits are set (== '1') across all those numbers
     */
    fun countOnesByPosition(input: List<String>): MutableMap<Int, Int> {
        val counts: MutableMap<Int, Int> = mutableMapOf()
        input.forEach {
            it.forEachIndexed { index, c -> if (c == '1') counts[index] = counts.getOrDefault(index, 0) + 1 }
        }
        return counts.toSortedMap()
    }

    fun part1(input: List<String>): Int {
        val counts = countOnesByPosition(input)
        val gammaBinary =
            counts.toSortedMap().entries.joinToString(separator = "") { if (it.value > input.size / 2) "1" else "0" }
        val epsilonBinary =
            counts.toSortedMap().entries.joinToString(separator = "") { if (it.value > input.size / 2) "0" else "1" }
        return gammaBinary.toInt(2) * epsilonBinary.toInt(2)
    }

    // looks like trash, a good solution would probably include a 'counting trie' data structure
    // (e.g. a trie that counts the number of children at each node)

    fun part2(input: List<String>): Int {

        var remaining = input
        var filterPrefix = ""
        var position = 0
        while (remaining.size > 1) {
            val ones = countOnesByPosition(remaining).getOrDefault(position++, 0)
            filterPrefix += if (ones >= remaining.size - ones) "1" else "0"
            remaining = remaining.filter { it.startsWith(prefix = filterPrefix) }
        }
        val oxygenGeneratorRating = remaining.first()

        remaining = input
        filterPrefix = ""
        position = 0
        while (remaining.size > 1) {
            val ones = countOnesByPosition(remaining).getOrDefault(position++, 0)
            filterPrefix += if (ones < remaining.size - ones) "1" else "0"
            remaining = remaining.filter { it.startsWith(prefix = filterPrefix) }
        }
        val c02ScrubberRating = remaining.first()

        return oxygenGeneratorRating.toInt(2) * c02ScrubberRating.toInt(2)

    }

    val testInput = readTestFileByYearAndDay(2021, 3)
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInputFileByYearAndDay(2021, 3)
    println(part1(input))
    println(part2(input))
}
