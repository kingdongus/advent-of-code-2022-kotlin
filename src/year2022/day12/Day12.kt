package year2022.day12

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    infix fun List<CharArray>.findMatching(toSearch: Char): Set<Pair<Int, Int>> {
        val ret = mutableSetOf<Pair<Int, Int>>()
        for (i in this.indices)
            for (j in this[0].indices)
                if (this[i][j] == toSearch)
                    ret.add(i to j)
        return ret
    }

    fun countMinSteps(
        startingPositions: MutableSet<Pair<Int, Int>>,
        grid: List<CharArray>,
        goal: Pair<Int, Int>
    ): Int {
        val visited = Array(grid.size) { Array(grid[0].size) { false } }
        var currentPositions = startingPositions.toMutableSet()
        val directions = listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)
        var steps = 0

        while (true) {
            steps++
            val tmp: MutableSet<Pair<Int, Int>> = mutableSetOf()

            currentPositions
                .filterNot { visited[it.first][it.second] }
                .forEach {
                    visited[it.first][it.second] = true

                    // find surrounding area that can be visited
                    directions.map { d -> it.first + d.first to it.second + d.second }
                        .filterNot { n -> (n.first < 0 || n.second < 0 || n.first >= grid.size || n.second >= grid[0].size || visited[n.first][n.second]) }
                        .filterNot { n -> grid[n.first][n.second] - 1 > grid[it.first][it.second] } // max height difference
                        .forEach { n -> tmp.add(n) }
                    if (tmp.contains(goal)) return steps
                }
            currentPositions = tmp
        }
    }

    fun part1(input: List<String>): Int =
        input.map { it.toCharArray() }.let {
            val start = (it findMatching 'S').first()
            it[start.first][start.second] = 'a' // to allow for easy ordering via Int value
            val goal = (it findMatching 'E').first()
            it[goal.first][goal.second] = 'z' // to allow for easy ordering via Int value
            return countMinSteps(mutableSetOf(start), it, goal)
        }

    // What is Big O?
    // https://i.redd.it/b9zi50qene5a1.png
    fun part2(input: List<String>): Int =
        input.map { it.toCharArray() }.let {
            val start = (it findMatching 'S').first()
            it[start.first][start.second] = 'a' // to allow for easy ordering via Int value
            val goal = (it findMatching 'E').first()
            it[goal.first][goal.second] = 'z' // to allow for easy ordering via Int value
            return countMinSteps((it findMatching 'a').toMutableSet(), it, goal)
        }

    val testInput = readTestFileByYearAndDay(2022, 12)
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInputFileByYearAndDay(2022, 12)
    println(part1(input))
    println(part2(input))
}