package year2022.day22

import Point
import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.max
import kotlin.math.min

enum class Orientation(val direction: Point, val facing: Int) {
    UP(Point(0, -1), 3),
    DOWN(Point(0, 1), 1),
    LEFT(Point(-1, 0), 2),
    RIGHT(Point(1, 0), 0);

    fun turnRight(): Orientation =
        when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            else -> UP
        }

    fun turnLeft(): Orientation =
        when (this) {
            UP -> LEFT
            LEFT -> DOWN
            DOWN -> RIGHT
            else -> UP
        }
}

fun main() {

    val OPEN_TILE = '.'
    val SOLID_TILE = '#'
    val WRAP_TILE = ' '

    fun tokenizeInstructions(instructions: String): List<String> {
        var buffer = ""
        val result = mutableListOf<String>()

        for (c in instructions.split("")) {
            when (c) {
                "" -> continue
                "L", "R" -> {
                    if (buffer.isNotBlank()) {
                        result.add(buffer)
                        buffer = ""
                    }
                    result.add(c)
                }

                else -> buffer += c
            }
        }
        if (buffer.isNotBlank()) result.add(buffer)
        return result
    }

    fun walk(field: Array<String>, startPosition: Point, orientation: Orientation, numSteps: Int): Point {
        val fieldDimensionX = field[0].length
        val fieldDimensionY = field.size
        var (x, y) = startPosition

        repeat(numSteps) {
            var newX = x + orientation.direction.x
            var newY = y + orientation.direction.y

            if (newX < 0) newX = max(field[y].lastIndexOf(OPEN_TILE), field[y].lastIndexOf(SOLID_TILE))
            else if (newX >= fieldDimensionX) newX = min(field[y].indexOf(OPEN_TILE), field[y].indexOf(SOLID_TILE))

            if (newY < 0) {
                // search in column from bottom (fieldDimensionY-1)
                for (i in field.indices.reversed()) {
                    if (field[i][x] != WRAP_TILE) {
                        newY = i
                        break
                    }
                }
            } else if (newY >= fieldDimensionY) {
                // search in column from top (0)
                for (i in field.indices)
                    if (field[i][x] != WRAP_TILE) {
                        newY = i
                        break
                    }
            }

            // if newX, newY is a wrap tile, search for the first non-wrap tile, factoring in direction
            if (field[newY][newX] == WRAP_TILE) {
                if (orientation.direction.x > 0) {
                    newX = min(field[y].indexOf(OPEN_TILE), field[y].indexOf(SOLID_TILE))
                } else if (orientation.direction.x < 0) {
                    newX = max(field[y].lastIndexOf(OPEN_TILE), field[y].lastIndexOf(SOLID_TILE))
                } else if (orientation.direction.y > 0) {
                    for (i in field.indices)
                        if (field[i][x] != WRAP_TILE) {
                            newY = i
                            break
                        }
                } else if (orientation.direction.y < 0) {

                    for (i in field.indices.reversed()) {
                        if (field[i][x] != WRAP_TILE) {
                            newY = i
                            break
                        }
                    }
                }
            }

            // only update if we ultimately ended up on a free tile
            if (field[newY][newX] == OPEN_TILE) {
                x = newX
                y = newY
            }
        }
        return Point(x, y)
    }

    fun part1(input: List<String>): Int {

        val fieldLines = input.takeWhile { it.isNotBlank() }
        val fieldDimensionX = fieldLines.maxOf { it.length }
        val paddedFieldByRow = fieldLines.map { it.padEnd(fieldDimensionX, ' ') }.toTypedArray()

        val instructions = input.last()
        val tokenizedInstructions = tokenizeInstructions(instructions)

        var y = 0
        var x = paddedFieldByRow[0].indexOf(OPEN_TILE)
        var orientation = Orientation.RIGHT

        tokenizedInstructions.forEach {
            when (it) {
                "L" -> orientation = orientation.turnLeft()
                "R" -> orientation = orientation.turnRight()
                else -> {
                    val newPos = walk(paddedFieldByRow, Point(x, y), orientation, it.toInt())
                    x = newPos.x
                    y = newPos.y
                }
            }
        }

        return ((y + 1) * 1000) + (4 * (x + 1)) + orientation.facing
    }


    fun part2(input: List<String>): Int = 0

    val testInput = readTestFileByYearAndDay(2022, 22)
    check(part1(testInput) == 6032)
    check(part2(testInput) == 0)

    val input = readInputFileByYearAndDay(2022, 22)
    println(part1(input))
    println(part2(input))
}
