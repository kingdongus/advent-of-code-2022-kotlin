package year2022.day13

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {


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

    infix fun String.compareAsPacket(other: String): Int {

        var idLeft = 0
        var idRight = 0

        while (idLeft < this.length && idRight < other.length) {
            val remainingLeft = this.substring(idLeft)
            val remainingRight = other.substring(idRight)

            val leftIsList = remainingLeft.startsWithList()
            val rightIsList = remainingRight.startsWithList()

            // if left and right are ints, compare ints
            if (!leftIsList and !rightIsList) {
                val leftInt = remainingLeft.extractFirstInt()
                val rightInt = remainingRight.extractFirstInt()
                when (leftInt compareTo rightInt) {
                    1 -> return 1
                    -1 -> return -1
                    else -> {
                        idLeft += leftInt.toString().length + 1
                        idRight += rightInt.toString().length + 1
                    }
                }

            }
            // if left and right are lists, compare the content
            else if (leftIsList and rightIsList) {
                val listLeft = remainingLeft extractListIndicesStartingFrom 0
                val listRight = remainingRight extractListIndicesStartingFrom 0

                when (
                    remainingLeft.substring(listLeft.first + 1, listLeft.second) compareAsPacket
                            remainingRight.substring(listRight.first + 1, listRight.second)
                ) {
                    1 -> return 1
                    -1 -> return -1
                    else -> {
                        idLeft += listLeft.second + 2
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
                when (leftList compareAsPacket rightList) {
                    1 -> return 1
                    -1 -> return -1
                    else -> {
                        idLeft += leftInt.toString().length + 1
                        idRight += rightListIndices.second + 1
                    }
                }
            } else {
                val rightInt = remainingRight.extractFirstInt()
                val rightList = "[$rightInt]"
                val leftListIndices = remainingLeft extractListIndicesStartingFrom 0
                val leftList = remainingLeft.substring(leftListIndices.first, leftListIndices.second + 1)
                when (leftList compareAsPacket rightList) {
                    1 -> return 1
                    -1 -> return -1
                    else -> {
                        idRight += rightInt.toString().length + 1
                        idLeft += leftListIndices.second + 1
                    }
                }
            }
        }
        // if left is empty and right is not, return -1
        if (idLeft >= this.length && idRight < other.length) return -1
        // if right is empty and left is not, return 1
        if (idLeft < this.length && idRight >= other.length) return 1
        // if both are blank, undecided
        return 0
    }

    fun part1(input: List<String>): Int = input.chunked(3)
        .mapIndexed { index, strings -> if (strings[0] compareAsPacket strings[1] < 1) index + 1 else 0 }
        .sum()


    // just count the number of packets that are smaller, no need for full sort
    fun part2(input: List<String>): Int {
        val dividerPackets = listOf("[[2]]", "[[6]]")
        val packets = input.filterNot { it.isBlank() }.toMutableList()
        packets.addAll(dividerPackets)
        return dividerPackets
            .map { packets.count { packet -> (packet compareAsPacket it) == -1 } }
            .map { it + 1 }
            .reduce(Int::times)
    }

    val testInput = readTestFileByYearAndDay(2022, 13)
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInputFileByYearAndDay(2022, 13)
    println(part1(input))
    println(part2(input))
}
