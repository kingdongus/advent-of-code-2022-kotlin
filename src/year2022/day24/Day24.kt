package year2022.day24

import Point
import readInputFileByYearAndDay
import readTestFileByYearAndDay

enum class Action(val offset: Point) {
    UP(Point(0, -1)),
    DOWN(Point(0, 1)),
    RIGHT(Point(1, 0)),
    LEFT(Point(-1, 0)),
    WAIT(Point(0, 0));

    companion object {
        infix fun from(s: String): Action = when (s) {
            ">" -> RIGHT
            "<" -> LEFT
            "^" -> UP
            "v" -> DOWN
            else -> WAIT
        }
    }
}

data class Blizzard(var position: Point, var direction: Action)

fun main() {

    fun simulateTrip(
        startingPoint: Point,
        goal: Point,
        field: List<String>,
        blizzards: MutableList<Blizzard>
    ): Int {
        var toCheck = listOf(startingPoint)
        var rounds = 0

        while (true) {
            // see if we already reached the goal
            if (toCheck.contains(goal)) return rounds
            rounds += 1

            // simulate where blizzards are next iteration
            // store blizzards redundantly for easy lookup in 2d space
            val blizzardPositions = Array(field.size) { BooleanArray(field[0].length) { false } }
            blizzards.onEach {
                it.position += it.direction.offset
                // blizzard preservation of energy
                if (it.position.x == 0) it.position = Point(blizzardPositions[0].lastIndex - 1, it.position.y)
                if (it.position.x == blizzardPositions[0].lastIndex) it.position = Point(1, it.position.y)
                if (it.position.y == 0) it.position = Point(it.position.x, blizzardPositions.lastIndex - 1)
                if (it.position.y == blizzardPositions.lastIndex) it.position = Point(it.position.x, 1)
            }.forEach {
                blizzardPositions[it.position.y][it.position.x] = true
            }

            // for each possible action, create a branching path

            toCheck = toCheck.flatMap { pos ->
                Action.values().map { action -> action.offset }.map { offset -> offset + pos }
            }
                .asSequence()
                .distinct()
                .filterNot { it.y < 0 || it.y > field.lastIndex } // just to avoid going upwards in first step
                .filterNot { field[it.y][it.x] == '#' } // ignore rocks
                .filterNot { blizzardPositions[it.y][it.x] }
                .toList()// ignore the position where we predict blizzards

        }
    }

    fun initializeBlizzardsFromInput(input: List<String>): MutableList<Blizzard> {
        val blizzards = mutableListOf<Blizzard>()

        for (i in 1 until input.size)
            for (j in 1 until input[0].length)
                if (listOf('<', '>', '^', 'v').contains(input[i][j]))
                    blizzards.add(Blizzard(Point(j, i), Action.from(input[i][j].toString())))
        return blizzards
    }

    fun part1(input: List<String>): Int {
        val startingPoint = Point(input.first().indexOf('.'), 0)
        val goal = Point(input.last().indexOf('.'), input.lastIndex)
        val blizzards = initializeBlizzardsFromInput(input)

        return simulateTrip(startingPoint, goal, input, blizzards)
    }

    fun part2(input: List<String>): Int {
        val startingPoint = Point(input.first().indexOf('.'), 0)
        val goal = Point(input.last().indexOf('.'), input.lastIndex)
        val blizzards = initializeBlizzardsFromInput(input)

        // assume that optimal partial solutions contribute to an optimal complete solution
        return simulateTrip(startingPoint, goal, input, blizzards) +
                simulateTrip(goal, startingPoint, input, blizzards) +
                simulateTrip(startingPoint, goal, input, blizzards)
    }

    val testInput = readTestFileByYearAndDay(2022, 24)
    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInputFileByYearAndDay(2022, 24)
    println(part1(input))
    println(part2(input))
}
