package year2022.day21

import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.abs

fun String.containsAny(of: List<String>): Boolean {
    for (keyword in of)
        if (this.contains(keyword, true)) return true
    return false
}

fun main() {

    fun resolvePart1(monkey: String, monkeys: Map<String, String>): Long {
        val task = monkeys[monkey]!!
        if (!task.containsAny(listOf("*", "/", "+", "-"))) return task.toLong()
        val components = task.split(" ")
        val resultLeft = resolvePart1(components.first(), monkeys)
        val resultRight = resolvePart1(components.last(), monkeys)
        return if (task.contains("*")) resultLeft * resultRight
        else if (task.contains("/")) resultLeft / resultRight
        else if (task.contains("+")) resultLeft + resultRight
        else resultLeft - resultRight
    }

    // we additionally memorize which recursive path depends on the output of the human
    // if a path does not, we substitute its entry in monkeys to reduce the number of recursive
    // calls in the future
    fun resolvePart2(monkey: String, monkeys: MutableMap<String, String>): Pair<Double, Boolean> {
        val task = monkeys[monkey]!!
        if (!task.containsAny(listOf("*", "/", "+", "-"))) return monkeys[monkey]!!.toDouble() to (monkey == "humn")

        val components = task.split(" ")
        val resultLeft = resolvePart2(components.first(), monkeys)
        val resultRight = resolvePart2(components.last(), monkeys)
        val calcResult =
            if (task.contains("*")) resultLeft.first * resultRight.first
            else if (task.contains("/")) resultLeft.first / resultRight.first
            else if (task.contains("+")) resultLeft.first + resultRight.first
            else resultLeft.first - resultRight.first
        val dependsOnHuman = resultLeft.second || resultRight.second
        if (!dependsOnHuman) monkeys[monkey] = calcResult.toString()
        return calcResult to dependsOnHuman
    }

    fun resolveRootFor(monkeys: MutableMap<String, String>, humanNumber: Double): Pair<Long, Long> {
        monkeys["humn"] = humanNumber.toString()
        val listeningTo = monkeys["root"]!!.split(" + ")
        val solution0 = resolvePart2(listeningTo.first(), monkeys)
        val solution1 = resolvePart2(listeningTo.last(), monkeys)
        return solution0.first.toLong() to solution1.first.toLong()
    }


    fun part1(input: List<String>): Long = input.map { it.split(": ") }
        .associate { it.first() to it.last() }
        .let { resolvePart1("root", it) }


    fun part2(input: List<String>): Long {
        val monkeys = input.map { it.split(": ") }.associate { it.first() to it.last() }.toMutableMap()
        var left = 0L
        var right = Long.MAX_VALUE

        // binary search
        while (true) {
            val solutionLeft = resolveRootFor(monkeys, left.toDouble())
            val solutionRight = resolveRootFor(monkeys, right.toDouble())

            if (solutionLeft.first == solutionLeft.second) return left
            if (solutionRight.first == solutionRight.second) return right

            val middle = (left + right) / 2

            if (abs(solutionLeft.first - solutionLeft.second) > abs(solutionRight.first - solutionRight.second))
                left = middle
            else right = middle

        }
    }

    val testInput = readTestFileByYearAndDay(2022, 21)
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInputFileByYearAndDay(2022, 21)
    println(part1(input))
    println(part2(input))
}
