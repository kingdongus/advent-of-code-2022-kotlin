package year2022.day13

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    fun findMatchingClosingBracketIndex(input: String): Int {
        var openingBrackets = 0
        for (i in input.indices) {
            if (input[i] == '[') {
                openingBrackets += 1
            } else if (input[i] == ']') {
                openingBrackets -= 1
            }
            if (openingBrackets == 0) return i
        }
        return -1
    }

    fun String.startsWithList(): Boolean = this.startsWith("[")
    fun String.extractFirstInt(): Int = this.split(",").first().toInt()
    infix fun String.extractListIndicesStartingFrom(start: Int): Pair<Int, Int> {
        if (this[start] != '[') return -1 to -1
        var openingBrackets = 0
        for (i in start..indices.last) {
            if (this[i] == '[') {
                openingBrackets += 1
            } else if (this[i] == ']') {
                openingBrackets -= 1
            }
            if (openingBrackets == 0) return start to i
        }
        return -1 to -1
    }

    fun compare(left: String, right: String): Int {

        //println("compare $left to $right")

        if (left.isBlank() and right.isNotBlank()) return -1
        if (right.isBlank() and left.isNotBlank()) return 1

        var idLeft = 0
        var idRight = 0

        while (idLeft < left.length && idRight < right.length) {
            val remainingLeft = left.substring(idLeft)
            val remainingRight = right.substring(idRight)
            //println("remaining $remainingLeft and $remainingRight")

            val leftIsList = remainingLeft.startsWithList()
            val rightIsList = remainingRight.startsWithList()

            // if left and right are ints, compare ints
            if (!leftIsList and !rightIsList) {
                // extract ints
                val leftInt = remainingLeft.extractFirstInt()
                val rightInt = remainingRight.extractFirstInt()
                // compare ints
                when (leftInt compareTo rightInt) {
                    1 -> return 1
                    -1 -> return -1
                    else -> {
                        // move pointers onwards by int length + 1 (comma) if not conclusive
                        idLeft += leftInt.toString().length + 1
                        idRight += rightInt.toString().length + 1
                    }// not conclusive
                }

            }
            // if left and right are lists, compare the content
            else if (leftIsList and rightIsList) {
                val listLeft = remainingLeft extractListIndicesStartingFrom 0
                val listRight = remainingRight extractListIndicesStartingFrom 0

                when (
                    compare(
                        remainingLeft.substring(listLeft.first + 1, listLeft.second),
                        remainingRight.substring(listRight.first + 1, listRight.second)
                    )
                ) {
                    1 -> return 1
                    -1 -> return -1
                    else -> {// not conclusive, move by list length
                        idLeft += listLeft.second + 2 // todo might be wrong
                        idRight += listRight.second + 2
                    }
                }
            }
            // if one is a list and the other is not, wrap the non-list in brackets and compare the results
            else if (!leftIsList and rightIsList) {
                val leftInt = remainingLeft.extractFirstInt()
                val leftList = "[$leftInt]"
                val rightListIndices = remainingRight extractListIndicesStartingFrom 0
                val rightList = remainingRight.substring(rightListIndices.first, rightListIndices.second + 1)
                when (compare(leftList, rightList)) {
                    1 -> return 1
                    -1 -> return -1
                    else -> {
                        // move pointers onwards by int length if not conclusive
                        // left: move until after int
                        idLeft += leftInt.toString().length + 1
                        // right: move until after ]
                        idRight += rightListIndices.second + 1
                    }
                }
            } else {
                val rightInt = remainingRight.extractFirstInt()
                val rightList = "[$rightInt]"
                val leftListIndices = remainingLeft extractListIndicesStartingFrom 0
                val leftList = remainingLeft.substring(leftListIndices.first, leftListIndices.second + 1)
                when (compare(leftList, rightList)) {
                    1 -> return 1
                    -1 -> return -1
                    else -> {
                        // move pointers onwards by int length if not conclusive
                        // left: move until after int
                        idRight += rightInt.toString().length + 1
                        // right: move until after ]
                        idLeft += leftListIndices.second + 1
                    }
                }
            }
        }
        // if left is empty and right is not, return -1
        if (idLeft >= left.length && idRight < right.length) return -1
        // if right is empty and left is not, return 1
        if (idLeft < left.length && idRight >= right.length) return 1
        // if both are blank, undecided
        return 0
    }

    fun part1(input: List<String>): Int {
        val correctOrder = input.chunked(3)
            .mapIndexed { index, strings ->
                val result = compare(strings[0], strings[1])
                println("compare ${strings[0]} to ${strings[1]}: $result")
                if (result < 1) index + 1 else 0
            }
        return correctOrder.sum()
    }

    class Packet(val content: String) : Comparable<Packet> {
        override fun compareTo(other: Packet): Int {
            val result = compare(this.content, other.content)
            println("${this.content}, ${other.content}: $result")
            return result
        }
    }

    fun part2(input: List<String>): Int {
        val dividerPackets = listOf(Packet("[[2]]"), Packet("[[6]]"))
        val packets = input.filterNot { it.isBlank() }
            .map { Packet(it) }
            .toMutableList()
        packets.addAll(dividerPackets)
        val result = dividerPackets
            .map { packets.count { packet -> packet < it } }
            .map { it + 1 }
            .reduce(Int::times)
        return result
    }

    check(compare("[7,7,7,7]", "[7,7,7]") == 1)
    check(compare("[7,7,7]", "[7,7,7,7]") == -1)
    check(compare("[7,7,7]", "[7,7,7]") == 0)
    val testInput = readTestFileByYearAndDay(2022, 13)
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInputFileByYearAndDay(2022, 13)
    println(part1(input))
    println(part2(input))
}
