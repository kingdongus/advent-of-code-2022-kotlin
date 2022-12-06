package year2021.day04

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {


    // Stores the board data redundantly as lists of lists of strings,
    // one per column and row.
    // this eases the mental model
    // rawRows:
    // "1 2"
    // "3 4"
    //
    // becomes
    // rows: [["1", "2"], ["3","4"]]
    // columns: [["1", "3"], ["2","4"]]
    class Board(rawRows: List<String>) {
        val rows = mutableListOf<MutableList<String>>()
        val columns = mutableListOf<MutableList<String>>()

        init { // parse the input according to the description from above
            rawRows.forEach {
                rows.add(it.split(" ").filter { it.isNotBlank() }.toMutableList())
            }
            rawRows.indices.forEach {
                columns.add(
                    rawRows.map { row -> row.split(" ").filter { substring -> substring.isNotBlank() } }
                        .map { c -> c[it] }.toMutableList()
                )
            }
        }

        fun mark(n: String) {
            rows.forEach { it.remove(n) }
            columns.forEach { it.remove(n) }
        }

        fun isCleared(): Boolean = rows.any { it.isEmpty() } || columns.any { it.isEmpty() }
        fun calculateScore(): Int = rows.flatten().sumOf { it.toInt() }
    }


    fun parseBoards(input: List<String>): MutableList<Board> {
        val boards = mutableListOf<Board>()
        val buffer = mutableListOf<String>()

        input.forEach {
            if (it.isBlank()) {
                boards.add(Board(buffer))
                buffer.clear()
            } else buffer.add(it)
        }
        // dangling board
        boards.add(Board(buffer))
        return boards
    }

    fun part1(input: List<String>): Int {

        val boards = parseBoards(input.drop(2))
        val moves = input.first().split(",")

        moves.forEach {
            for (b in boards) {
                b.mark(it)
                if (b.isCleared()) return b.calculateScore() * it.toInt()
            }
        }

        return -1
    }

    // looks like trash, a good solution would probably include a 'counting trie' data structure
    // (e.g. a trie that counts the number of children at each node)

    fun part2(input: List<String>): Int {
        val boards = parseBoards(input.drop(2))
        val moves = input.first().split(",")

        val winningBoards = mutableListOf<Board>()
        var lastCalled = "-1"

        moves.forEach {
            for (b in boards) {
                b.mark(it)
                if (b.isCleared()) {
                    winningBoards.add(b)
                    lastCalled = it
                }
            }
            boards.removeAll(winningBoards)
        }

        return winningBoards.last().calculateScore() * lastCalled.toInt()
    }

    val testInput = readTestFileByYearAndDay(2021, 4)
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInputFileByYearAndDay(2021, 4)
    println(part1(input))
    println(part2(input))
}
