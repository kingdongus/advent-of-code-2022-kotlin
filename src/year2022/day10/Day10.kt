package year2022.day10

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    fun calculateXOverTime(input: List<String>): Array<Int> {
        val history = Array(input.size * 3) { 1 }
        var cycle = 1
        var x = 1

        input
            .map { if (it == "noop") "noop 0" else it } // append the 0 for easier parsing later
            .map { it.split(" ") }
            .map { it.first() to it.last().toInt() }
            .forEach {
                when (it.first) {
                    "noop" -> history[cycle++] = x
                    "addx" -> {
                        history[cycle++] = x
                        x += it.second
                        history[cycle++] = x
                    }

                    else -> println("oh no")
                }
            }
        return history
    }

    fun part1(input: List<String>): Int {
        val history = calculateXOverTime(input)
        return listOf(20, 60, 100, 140, 180, 220).sumOf { it * history[it - 1] }
    }

    fun part2(input: List<String>): String {
        val spritePositions = calculateXOverTime(input)

        return listOf(0, 40, 80, 120, 160, 200)
            .map { it..it + 39 }
            .joinToString("\n") { row ->
                row.joinToString(separator = "") {
                    val spriteRange = spritePositions[it] - 1..spritePositions[it] + 1
                    if (spriteRange.contains(it % 40)) "#" else "."
                }
            }
    }

    val testInput = readTestFileByYearAndDay(2022, 10)
    check(part1(testInput) == 13140)
    check(
        part2(testInput) == "##..##..##..##..##..##..##..##..##..##..\n" +
                "###...###...###...###...###...###...###.\n" +
                "####....####....####....####....####....\n" +
                "#####.....#####.....#####.....#####.....\n" +
                "######......######......######......####\n" +
                "#######.......#######.......#######....."
    )

    val input = readInputFileByYearAndDay(2022, 10)
    println(part1(input))
    println(part2(input))
}