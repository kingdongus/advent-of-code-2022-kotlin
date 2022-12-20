package year2022.day20

import readInputFileByYearAndDay
import readTestFileByYearAndDay

fun main() {

    data class Node(var value: Long, var previous: Node? = null, var next: Node? = null) {
        override fun toString(): String = "$value, previous:${previous?.value}, next:${next?.value}"
    }


    fun mix(nodes: List<Node>) {
        nodes.forEach {
            var delta = it.value % (nodes.size - 1)
            if (delta != 0L) {
                it.next!!.previous = it.previous
                it.previous!!.next = it.next
                var newPreviousNode = it.previous
                while (delta > 0) {
                    newPreviousNode = newPreviousNode?.next
                    delta--
                }
                while (delta < 0) {
                    newPreviousNode = newPreviousNode?.previous
                    delta++
                }
                it.previous = newPreviousNode
                it.next = newPreviousNode?.next
                newPreviousNode?.next = it
                it.next?.previous = it
            }
        }
    }

    fun initializeDoubleLinkedList(input: List<String>): List<Node> {
        val nodes = input.map { it.toLong() }.toLongArray().map { Node(it) }
        for (i in 0 until nodes.lastIndex) nodes[i].next = nodes[i + 1]
        for (i in 1..nodes.lastIndex) nodes[i].previous = nodes[i - 1]
        nodes.last().next = nodes.first()
        nodes.first().previous = nodes.last()
        return nodes
    }

    fun calculateCoordinates(nodes: List<Node>): Long {
        val nullNode = nodes.first { it.value == 0L }
        return listOf(1000L, 2000L, 3000L).map { it % nodes.size }.sumOf {
            var node = nullNode
            repeat(it.toInt()) { node = node.next!! }
            node.value
        }
    }

    fun part1(input: List<String>): Long = initializeDoubleLinkedList(input).let {
        mix(it)
        return calculateCoordinates(it)
    }

    fun part2(input: List<String>): Long = initializeDoubleLinkedList(input)
        .onEach { node -> node.value *= 811589153 }
        .let {
            repeat(10) { _ -> mix(it) }
            return calculateCoordinates(it)
        }

    val testInput = readTestFileByYearAndDay(2022, 20)
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInputFileByYearAndDay(2022, 20)
    println(part1(input))
    println(part2(input))
}
