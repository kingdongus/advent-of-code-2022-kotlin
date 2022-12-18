package year2022.day18

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    val offsets = arrayOf(
        Triple(1, 0, 0),
        Triple(-1, 0, 0),
        Triple(0, 1, 0),
        Triple(0, -1, 0),
        Triple(0, 0, 1),
        Triple(0, 0, -1),
    )

    fun parseInput(input: List<String>) = input.map { it.split(",") }
        .map { it.map { s -> s.toInt() } }

    fun initStateAndCountExposed(
        input: List<String>,
        state: Array<Array<BooleanArray>>,
        bound: Int
    ): Int {
        var exposed = 0
        parseInput(input).forEach {
            exposed += 6
            state[it[0]][it[1]][it[2]] = true

            for (offset in offsets) {
                val xn = it[0] + offset.first
                val yn = it[1] + offset.second
                val zn = it[2] + offset.third
                if (xn < 0 || yn < 0 || zn < 0 || xn >= bound || yn >= bound || zn >= bound) continue
                // placing a block next to a block reduces this block's surface and the other block's surface
                if (state[xn][yn][zn]) exposed -= 2
            }
        }
        return exposed
    }

    fun initStateAndDisplace(
        input: List<String>,
        state: Array<Array<BooleanArray>>,
    ) =
        parseInput(input)
            .forEach { state[it[0] + 1][it[1] + 1][it[2] + 1] = true } // move one away from any edge


    fun part1(input: List<String>): Int {
        val bound = parseInput(input).maxOf { it.max() } + 3 // leave some space to all sides
        val state = Array(bound) { Array(bound) { BooleanArray(bound) } }
        return initStateAndCountExposed(input, state, bound)
    }

    fun floodFill(blocks: Array<Array<BooleanArray>>): Int {
        val visited = Array(blocks.size) { Array(blocks[0].size) { BooleanArray(blocks[0][0].size) } }
        val toProcess = mutableSetOf(Triple(0, 0, 0))
        var reachable = 0

        while (toProcess.isNotEmpty()) {
            val next = toProcess.first()
            toProcess.remove(next)
            visited[next.first][next.second][next.third] = true

            for (offset in offsets) {
                val x = next.first + offset.first
                val y = next.second + offset.second
                val z = next.third + offset.third

                if (x < 0 || y < 0 || z < 0 || x > blocks.lastIndex || y > blocks[0].lastIndex || z > blocks[0][0].lastIndex) continue
                if (blocks[x][y][z]) reachable++ // see if there is a surface in that direction
                else if (!visited[x][y][z]) toProcess.add(Triple(x, y, z))  // calculate next places to check out
            }
        }
        return reachable
    }

    fun part2(input: List<String>): Int {
        val bound = parseInput(input).maxOf { it.max() } + 3 // leave some space to all sides
        val state = Array(bound) { Array(bound) { BooleanArray(bound) } }
        initStateAndDisplace(input, state)
        return floodFill(state)
    }

    val testInput = readTestFileByYearAndDay(2022, 18)
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInputFileByYearAndDay(2022, 18)
    println(part1(input))
    println(part2(input))
}
