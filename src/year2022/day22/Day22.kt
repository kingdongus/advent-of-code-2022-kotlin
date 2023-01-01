package year2022.day22

import Point
import readInputFileByYearAndDay
import readTestFileByYearAndDay
import year2022.day22.Orientation.*
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


fun main() {

    fun walkFlat(field: Array<String>, startPosition: Point, orientation: Orientation, numSteps: Int): Point {
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

    fun parseMap(input: List<String>): Array<String> {
        val fieldLines = input.takeWhile { it.isNotBlank() }
        val fieldDimensionX = fieldLines.maxOf { it.length }
        return fieldLines.map { it.padEnd(fieldDimensionX, ' ') }.toTypedArray()
    }

    fun part1(input: List<String>): Int {

        val parsedMapByRow = parseMap(input)
        val tokenizedInstructions = tokenizeInstructions(input.last())

        var y = 0
        var x = parsedMapByRow[0].indexOf(OPEN_TILE)
        var orientation = RIGHT

        tokenizedInstructions.forEach {
            when (it) {
                "L" -> orientation = orientation.turnLeft()
                "R" -> orientation = orientation.turnRight()
                else -> {
                    val newPos = walkFlat(parsedMapByRow, Point(x, y), orientation, it.toInt())
                    x = newPos.x
                    y = newPos.y
                }
            }
        }

        return ((y + 1) * 1000) + (4 * (x + 1)) + orientation.facing
    }


    //  21
    //  3
    // 54
    // 6
    data class CubeFace(val topLeft: Point)

    // overlay map with cube faces
    // hardcoded for part 2
    val faceDim = 50
    val face1 = CubeFace(topLeft = Point(faceDim * 2, 0))
    val face2 = CubeFace(topLeft = Point(faceDim, 0))
    val face3 = CubeFace(topLeft = Point(faceDim, faceDim))
    val face4 = CubeFace(topLeft = Point(faceDim, faceDim * 2))
    val face5 = CubeFace(topLeft = Point(0, faceDim * 2))
    val face6 = CubeFace(topLeft = Point(0, faceDim * 3))

    val faces = listOf(face1, face2, face3, face4, face5, face6)

    val top = 0
    val left = 0
    val right = faceDim - 1
    val bottom = faceDim - 1

    fun startAtTopKeepX(p: Point) = Point(p.x, top)
    fun startAtTopTransformX(p: Point) = Point(p.y, top)
    fun startAtBottomKeepX(p: Point) = Point(p.x, bottom)
    fun startAtBottomTransformX(p: Point) = Point(p.y, bottom)
    fun startAtLeftTransformXtoY(p: Point) = Point(left, p.x)
    fun startAtLeftKeepY(p: Point) = Point(left, p.y)
    fun startAtLeftInvertY(p: Point) = Point(left, faceDim - p.y - 1) // might be off by one
    fun startAtRightInvertY(p: Point) = Point(right, faceDim - p.y - 1) // might be off by one
    fun startAtRightKeepY(p: Point) = Point(right, p.y)
    fun startAtRightTransformXToY(p: Point) = Point(right, p.x) // might be off by one

    // (a,b):(c,d, e) if we exit face a in direction b, enter face c facing direction d transforming coordinates using function e

    val transformationRules = mapOf(
        (face1 to UP) to Triple(face6, UP, ::startAtBottomKeepX),
        (face1 to RIGHT) to Triple(face4, LEFT, ::startAtRightInvertY),
        (face1 to DOWN) to Triple(face3, LEFT, ::startAtRightTransformXToY),
        (face1 to LEFT) to Triple(face2, LEFT, ::startAtRightKeepY),

        (face2 to UP) to Triple(face6, RIGHT, ::startAtLeftTransformXtoY), // ??
        (face2 to RIGHT) to Triple(face1, RIGHT, ::startAtLeftKeepY),
        (face2 to DOWN) to Triple(face3, DOWN, ::startAtTopKeepX),
        (face2 to LEFT) to Triple(face5, RIGHT, ::startAtLeftInvertY), // flipped

        (face3 to UP) to Triple(face2, UP, ::startAtBottomKeepX),
        (face3 to RIGHT) to Triple(face1, UP, ::startAtBottomTransformX),
        (face3 to DOWN) to Triple(face4, DOWN, ::startAtTopKeepX),
        (face3 to LEFT) to Triple(face5, DOWN, ::startAtTopTransformX),

        (face4 to UP) to Triple(face3, UP, ::startAtBottomKeepX),
        (face4 to RIGHT) to Triple(face1, LEFT, ::startAtRightInvertY),
        (face4 to DOWN) to Triple(face6, LEFT, ::startAtRightTransformXToY), // probably ok
        (face4 to LEFT) to Triple(face5, LEFT, ::startAtRightKeepY),

        (face5 to UP) to Triple(face3, RIGHT, ::startAtLeftTransformXtoY),
        (face5 to RIGHT) to Triple(face4, RIGHT, ::startAtLeftKeepY),
        (face5 to DOWN) to Triple(face6, DOWN, ::startAtTopKeepX),
        (face5 to LEFT) to Triple(face2, RIGHT, ::startAtLeftInvertY),

        (face6 to UP) to Triple(face5, UP, ::startAtBottomKeepX),
        (face6 to RIGHT) to Triple(face4, UP, ::startAtBottomTransformX),
        (face6 to DOWN) to Triple(face1, DOWN, ::startAtTopKeepX),
        (face6 to LEFT) to Triple(face2, DOWN, ::startAtTopTransformX),

        )

    fun isOutOfCubeFaceBounds(p: Point): Boolean =
        p.x < 0 || p.y < 0 || p.y >= faceDim || p.x >= faceDim

    fun walkCube(
        startFace: CubeFace,
        startPosition: Point,
        orientation: Orientation,
        numSteps: Int,
        slicedFaces: Map<CubeFace, List<String>>
    ): Triple<Point, Orientation, CubeFace> {
        var currentPosition = startPosition
        var currentOrientation = orientation
        var currentCubeFace = startFace

        repeat(numSteps) {
            val newPosition = currentPosition + currentOrientation.direction

            if (isOutOfCubeFaceBounds(newPosition)) {
                val stuff = transformationRules[currentCubeFace to currentOrientation]
                val newFace = stuff!!.first
                val positionOnNewFace = stuff.third.invoke(newPosition)

                val slice = slicedFaces[newFace]

                // only update if we ultimately ended up on a free tile
                if (slice!![positionOnNewFace.y][positionOnNewFace.x] == OPEN_TILE) {
                    currentCubeFace = newFace
                    currentOrientation = stuff.second
                    currentPosition = positionOnNewFace
                }
            } else {
                val slice = slicedFaces[currentCubeFace]
                // only update if we ultimately ended up on a free tile
                if (slice!![newPosition.y][newPosition.x] == OPEN_TILE) {
                    currentPosition = newPosition
                }
            }
        }
        return Triple(currentPosition, currentOrientation, currentCubeFace)
    }

    fun part2(input: List<String>): Int {


        val parsedMapByRow = parseMap(input)
        // one string array per cube face
        val slicedFaces = faces.map { it.topLeft }
            .map { it.x to parsedMapByRow.drop(it.y).take(faceDim) }
            .map { xAndLines ->
                val (x, lines) = xAndLines
                lines.map { it.substring(x until x + faceDim) }
            }.mapIndexed { index, strings -> faces[index] to strings }
            .toMap()
        // quick check that we got no off-by-1 errors
        slicedFaces.values.onEach { if (it.any { line -> line.contains(" ") }) throw Exception(" oh no") }


        val tokenizedInstructions = tokenizeInstructions(input.last())

        // start position: top left of face 2 hardcoded
        var currentCubeFace = face2
        var orientation = RIGHT
        var position = Point(0, 0)

        tokenizedInstructions.forEach {
            when (it) {
                "L" -> orientation = orientation.turnLeft()
                "R" -> orientation = orientation.turnRight()
                else -> {
                    val (newPos, newOrientation, newFace) = walkCube(
                        currentCubeFace,
                        position,
                        orientation,
                        it.toInt(),
                        slicedFaces
                    )
                    position = newPos
                    orientation = newOrientation
                    currentCubeFace = newFace
                }
            }
        }

        // transform local cube face coordinates into global coordinates
        return ((position.y + currentCubeFace.topLeft.y + 1) * 1000) + (4 * (position.x + currentCubeFace.topLeft.x + 1)) + orientation.facing

    }

    val testInput = readTestFileByYearAndDay(2022, 22)
    check(part1(testInput) == 6032)
    // check(part2(testInput) == 5031) // I ignore this one since the layout differs, too much headache

    val input = readInputFileByYearAndDay(2022, 22)
    println(part1(input))
    println(part2(input))
}
