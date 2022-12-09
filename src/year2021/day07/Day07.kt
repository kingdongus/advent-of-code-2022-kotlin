package year2021.day07

import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.abs

fun main() {

    fun minimizeCost(input: List<String>, costFunction: (List<Int>, Int) -> Int): Int {
        val crabPositions = input[0].split(",").map { it.toInt() }

        // start in the middle
        // binary search adjust the interval to converge towards a minimum
        var left = crabPositions.min()
        var right = crabPositions.max()
        var min = Int.MAX_VALUE

        while (right > left) {
            val pivot = (left + right) / 2
            val pivotCost = costFunction(crabPositions, pivot)
            val costLeft = costFunction(crabPositions, pivot - 1)
            if (costLeft < pivotCost) {
                right = pivot - 1
                min = costLeft
                continue
            } else {
                val costRight = costFunction(crabPositions, pivot + 1)
                if (costRight < pivotCost) {
                    left = pivot + 1
                    min = costRight
                    continue
                }
            }
            return pivotCost
        }

        return min
    }

    fun part1(input: List<String>): Int = minimizeCost(input) { crabPositions, targetPosition ->
        crabPositions.sumOf { abs(it - targetPosition) }
    }

    fun part2(input: List<String>): Int = minimizeCost(input) { crabPositions, targetPosition ->
        crabPositions.sumOf { (0..abs(targetPosition - it)).sum() }
    }

    val testInput = readTestFileByYearAndDay(2021, 7)
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInputFileByYearAndDay(2021, 7)
    println(part1(input))
    println(part2(input))
}
