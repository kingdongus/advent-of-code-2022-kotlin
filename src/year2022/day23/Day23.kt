package year2022.day23

import Point2D
import readInputFileByYearAndDay
import readTestFileByYearAndDay

enum class Direction(val offset: Point2D) {
    N(Point2D(0, -1)),
    NE(Point2D(1, -1)),
    E(Point2D(1, 0)),
    SE(Point2D(1, 1)),
    S(Point2D(0, 1)),
    SW(Point2D(-1, 1)),
    W(Point2D(-1, 0)),
    NW(Point2D(-1, -1));
}

fun toCheck(direction: Direction): List<Direction> = when (direction) {
    Direction.N -> listOf(Direction.N, Direction.NE, Direction.NW)
    Direction.E -> listOf(Direction.E, Direction.NE, Direction.SE)
    Direction.S -> listOf(Direction.S, Direction.SE, Direction.SW)
    else -> listOf(Direction.W, Direction.SW, Direction.NW)
}

data class Elf(var position: Point2D) {
    var proposal: Point2D = position
    fun performMove(): Point2D {
        position = proposal
        proposal = position
        return position
    }

    fun propose(direction: Direction): Point2D {
        proposal = position + direction.offset
        return proposal
    }

    fun surroundingsInDirection(direction: Direction) =
        toCheck(direction).map { it.offset + position }

    fun surroundings() = sequence {
        for (i in -1..1)
            for (j in -1..1)
                if (!(i == 0 && j == 0))
                    yield(Point2D(position.x + i, position.y + j))
    }
}

fun main() {

    fun hasNearbyElves(elf: Elf, elfPositions: Array<BooleanArray>): Boolean =
        elf.surroundings().map { elfPositions[it.y][it.x] }.count { it } > 0


    fun initializeElves(
        parsedInput: Array<List<String>>,
        buffer: Int
    ): MutableList<Elf> {
        val elves = mutableListOf<Elf>() // easy iteration over elves
        parsedInput.forEachIndexed { indexY, strings ->
            strings.forEachIndexed { indexX, s ->
                if (s == "#") elves.add(Elf(Point2D(indexX + buffer, indexY + buffer)))
            }
        }
        return elves
    }


    // return true if someone moved, false if not
    fun simulateRound(
        dimYNew: Int,
        dimXNew: Int,
        elves: MutableList<Elf>,
        checkOrder: MutableList<Direction>
    ): Boolean {
        val elfPositions = Array(dimYNew) { BooleanArray(dimXNew) { false } } // easy elf position lookup
        elves.forEach { elfPositions[it.position.y][it.position.x] = true }

        val elvesWithNeighbours = elves.filter { elf -> hasNearbyElves(elf, elfPositions) }
        if (elvesWithNeighbours.isEmpty()) return false

        // compute proposals
        val proposals = Array(dimYNew) { IntArray(dimXNew) { 0 } } // store proposals
        elvesWithNeighbours
            .forEach { elf ->
                for (direction in checkOrder) {
                    val hasNoElves = elf.surroundingsInDirection(direction)
                        .map { point -> elfPositions[point.y][point.x] }
                        .count { !it } == 3
                    if (hasNoElves) {
                        val (x, y) = elf.propose(direction)
                        proposals[y][x] += 1
                        break
                    }
                }
            }

        // find proposals with duplicate targets, then make elves not move
        val duplicates = mutableListOf<Point2D>()
        proposals.forEachIndexed { y, line ->
            line.forEachIndexed { x, i ->
                if (i > 1) duplicates.add(Point2D(x, y))
            }
        }
        elvesWithNeighbours.forEach { elf -> if (duplicates.contains(elf.proposal)) elf.proposal = elf.position }

        // move elves with unique targets
        elvesWithNeighbours.forEach { elf -> elf.performMove() }
        elves.forEach { elf -> elfPositions[elf.position.y][elf.position.x] = true }
        // change priority of directions for next iteration
        checkOrder.add(checkOrder.first())
        checkOrder.removeAt(0)
        return true
    }

    fun part1(input: List<String>): Int {
        val parsedInput = input.map { it.split("") }
            .map { it.drop(1) }
            .map { it.dropLast(1) }
            .toTypedArray()

        // big brain assumption
        // in 10 moves, the field cannot extend by more than 10 in each direction
        val buffer = 10
        val dimYNew = parsedInput.size + 2 * buffer
        val dimXNew = parsedInput[0].size + 2 * buffer

        val elves = initializeElves(parsedInput, buffer) // easy iteration over elves
        val checkOrder = mutableListOf(Direction.N, Direction.S, Direction.W, Direction.E)

        repeat(10) {
            simulateRound(dimYNew, dimXNew, elves, checkOrder)
        }

        val minX = elves.minOf { it.position.x }
        val minY = elves.minOf { it.position.y }
        val maxX = elves.maxOf { it.position.x }
        val maxY = elves.maxOf { it.position.y }

        return ((maxX - minX + 1) * (maxY - minY + 1)) - elves.size
    }


    fun part2(input: List<String>): Int {
        val parsedInput = input.map { it.split("") }
            .map { it.drop(1) }
            .map { it.dropLast(1) }
            .toTypedArray()

        // big brain assumption
        // the field will not extend by more than 1000 in each direction
        val buffer = 1000
        val dimYNew = parsedInput.size + 2 * buffer
        val dimXNew = parsedInput[0].size + 2 * buffer

        val checkOrder = mutableListOf(Direction.N, Direction.S, Direction.W, Direction.E)
        val elves = initializeElves(parsedInput, buffer) // easy iteration over elves

        var round = 1
        while (simulateRound(dimYNew, dimXNew, elves, checkOrder)) round++

        return round
    }

    val testInput = readTestFileByYearAndDay(2022, 23)
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = readInputFileByYearAndDay(2022, 23)
    println(part1(input))
    println(part2(input))
}
