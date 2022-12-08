package year2022.day08

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {


    fun part1(input: List<String>): Int {

        val dimension = input[0].length
        var visibility = Array(dimension) { Array(dimension) { false } }

        // edges always visible
        visibility.indices.forEach {
            visibility[it][0] = true
            visibility[it][dimension - 1] = true
        }

        visibility[0].indices.forEach { visibility[0][it] = true }
        visibility[dimension - 1].indices.forEach { visibility[dimension - 1][it] = true }

        // check visible from left
        for (i in 0 until dimension) {
            var max = input[i][0].digitToInt()
            for (j in 1 until dimension) {
                val current = input[i][j].digitToInt()
                if (current > max) {
                    visibility[i][j] = true
                    max = current
                }
            }
        }
        // check visible from right
        for (i in dimension - 1 downTo 1) {
            var max = input[i][dimension - 1].digitToInt()
            for (j in dimension - 1 downTo 1) {
                val current = input[i][j].digitToInt()
                if (current > max) {
                    visibility[i][j] = true
                    max = current
                }
            }
        }
        // check visible from top
        for (i in 0 until dimension) {
            var max = input[0][i].digitToInt()
            for (j in 1 until dimension) {
                val current = input[j][i].digitToInt()
                if (current > max) {
                    visibility[j][i] = true
                    max = current
                }
            }
        }
        // check visible from bottom
        for (i in dimension - 1 downTo 1) {
            var max = input[dimension - 1][i].digitToInt()
            for (j in dimension - 1 downTo 1) {
                val current = input[j][i].digitToInt()
                if (current > max) {
                    visibility[j][i] = true
                    max = current
                }
            }
        }

        return visibility.flatten().count { it }
    }

    fun part2(input: List<String>): Int {

        val dimension = input[0].length
        var scenicScore = Array(dimension) { Array(dimension) { 0 } }

        for (x in 1 until dimension - 1)
            for (y in 1 until dimension - 1) {
                val treeHeight = input[x][y].digitToInt()

                var top = 1
                for (j in x - 1 downTo 1) {
                    if (input[j][y].digitToInt() >= treeHeight) break
                    top++
                }

                var bottom = 1
                for (j in x + 1 until dimension - 1) {
                    if (input[j][y].digitToInt() >= treeHeight) break
                    bottom++
                }

                val left = 1 + input[x].substring(1, y).reversed().takeWhile { it.digitToInt() < treeHeight }.count()
                val right = 1 + input[x].substring(y + 1).takeWhile { it.digitToInt() < treeHeight }.count()

                scenicScore[x][y] = top * bottom * left * right
            }

        return scenicScore.flatten().max()
    }

    val testInput = readTestFileByYearAndDay(2022, 8)
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInputFileByYearAndDay(2022, 8)
    println(part1(input))
    println(part2(input))
}