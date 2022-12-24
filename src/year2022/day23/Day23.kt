package year2022.day23

import Point
import readInputFileByYearAndDay
import readTestFileByYearAndDay

enum class Direction(val offset: Point) {
    N(Point(0, -1)),
    NE(Point(1, -1)),
    E(Point(1, 0)),
    SE(Point(1, 1)),
    S(Point(0, 1)),
    SW(Point(-1, 1)),
    W(Point(-1, 0)),
    NW(Point(-1, -1));
}

fun toCheck(direction: Direction): List<Direction> = when (direction) {
    Direction.N -> listOf(Direction.N, Direction.NE, Direction.NW)
    Direction.E -> listOf(Direction.E, Direction.NE, Direction.SE)
    Direction.S -> listOf(Direction.S, Direction.SE, Direction.SW)
    else -> listOf(Direction.W, Direction.SW, Direction.NW)
}

data class Elf(var position: Point) {
    var proposal: Point = position
    fun performMove(): Point {
        position = proposal
        proposal = position
        return position
    }

    fun propose(direction: Direction): Point {
        proposal = position + direction.offset
        return proposal
    }

    fun surroundingsInDirection(direction: Direction) =
        toCheck(direction).map { it.offset + position }

    fun surroundings() = sequence {
        for (i in -1..1)
            for (j in -1..1)
                if (!(i == 0 && j == 0))
                    yield(Point(position.x + i, position.y + j))
    }
}

fun main() {

    fun hasNearbyElves(elf: Elf, elfPositions: Array<BooleanArray>): Boolean =
        elf.surroundings().map { elfPositions[it.y][it.x] }.count { it } > 0


    fun part1(input: List<String>): Int {
        val parsedInput = input.map { it.split("") }
            .map { it.drop(1) }
            .map { it.dropLast(1) }
            .toTypedArray()

        val checkOrder = mutableListOf(Direction.N, Direction.S, Direction.W, Direction.E)

        // in 10 moves, the field cannot extend by more than 10 in each direction

        val buffer = 10
        val dimYNew = parsedInput.size + 2 * buffer
        val dimXNew = parsedInput[0].size + 2 * buffer

        var elfPositions = Array(dimYNew) { BooleanArray(dimXNew) { false } } // easy elf position lookup
        var proposals = Array(dimYNew) { IntArray(dimXNew) { 0 } } // store proposals
        val elves = mutableListOf<Elf>() // easy iteration over elves

        // init elves
        parsedInput.forEachIndexed { indexY, strings ->
            strings.forEachIndexed { indexX, s ->
                if (s == "#") elves.add(Elf(Point(indexX + buffer, indexY + buffer)))
            }
        }
        elves
            .forEach { elfPositions[it.position.y][it.position.x] = true }

        repeat(10) {
            // make proposals

            // elves without nearby elves don't move
            val elvesWithNeighbours = elves.filter { elf -> hasNearbyElves(elf, elfPositions) }

            // compute proposals
            elvesWithNeighbours.forEach { elf ->
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
            val duplicates = mutableListOf<Point>()
            proposals.forEachIndexed { y, line ->
                line.forEachIndexed { x, i ->
                    if (i > 1) duplicates.add(Point(x, y))
                }
            }
            elvesWithNeighbours.forEach { elf -> if (duplicates.contains(elf.proposal)) elf.proposal = elf.position }

            // move elves with unique targets
            elvesWithNeighbours.forEach { elf -> elf.performMove() }
            elfPositions = Array(dimYNew) { BooleanArray(dimXNew) { false } }
            elves.forEach { elf -> elfPositions[elf.position.y][elf.position.x] = true }
            proposals = Array(dimYNew) { IntArray(dimXNew) { 0 } }
            // change priority of directions for next iteration
            checkOrder.add(checkOrder.first())
            checkOrder.removeAt(0)

            println("after iteration $it")

        }

        val minX = elves.map { it.position.x }.min()
        val minY = elves.map { it.position.y }.min()
        val maxX = elves.map { it.position.x }.max()
        val maxY = elves.map { it.position.y }.max()

        return ((maxX - minX + 1) * (maxY - minY + 1)) - elves.size
    }


    fun part2(input: List<String>): Int {
        val parsedInput = input.map { it.split("") }
            .map { it.drop(1) }
            .map { it.dropLast(1) }
            .toTypedArray()

        val checkOrder = mutableListOf(Direction.N, Direction.S, Direction.W, Direction.E)

        // in 10 moves, the field cannot extend by more than 10 in each direction

        val buffer = 1000
        val dimYNew = parsedInput.size + 2 * buffer
        val dimXNew = parsedInput[0].size + 2 * buffer

        var elfPositions = Array(dimYNew) { BooleanArray(dimXNew) { false } } // easy elf position lookup
        var proposals = Array(dimYNew) { IntArray(dimXNew) { 0 } } // store proposals
        val elves = mutableListOf<Elf>() // easy iteration over elves

        // init elves
        parsedInput.forEachIndexed { indexY, strings ->
            strings.forEachIndexed { indexX, s ->
                if (s == "#") elves.add(Elf(Point(indexX + buffer, indexY + buffer)))
            }
        }
        elves
            .forEach { elfPositions[it.position.y][it.position.x] = true }

        var round = 0
        while (true) {
            round++
            // make proposals

            // elves without nearby elves don't move
            val elvesWithNeighbours = elves.filter { elf -> hasNearbyElves(elf, elfPositions) }

            if (elvesWithNeighbours.isEmpty()) break

            // compute proposals
            elvesWithNeighbours.forEach { elf ->
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
            val duplicates = mutableListOf<Point>()
            proposals.forEachIndexed { y, line ->
                line.forEachIndexed { x, i ->
                    if (i > 1) duplicates.add(Point(x, y))
                }
            }
            elvesWithNeighbours.forEach { elf -> if (duplicates.contains(elf.proposal)) elf.proposal = elf.position }

            // move elves with unique targets
            elvesWithNeighbours.forEach { elf -> elf.performMove() }
            elfPositions = Array(dimYNew) { BooleanArray(dimXNew) { false } }
            elves.forEach { elf -> elfPositions[elf.position.y][elf.position.x] = true }
            proposals = Array(dimYNew) { IntArray(dimXNew) { 0 } }
            // change priority of directions for next iteration
            checkOrder.add(checkOrder.first())
            checkOrder.removeAt(0)

            println("after iteration $round")

        }

        return round
    }

    val testInput = readTestFileByYearAndDay(2022, 23)
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = readInputFileByYearAndDay(2022, 23)
    println(part1(input))
    println(part2(input))
}
