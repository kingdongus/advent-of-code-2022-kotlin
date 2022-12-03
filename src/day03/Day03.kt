package day03

import readInput

fun main(){

    fun Char.toPriority():Int {
        if (this.code >= 'A'.code && this.code < 'a'.code)
            return this.code - 'A'.code + 27
        else if (this.code >= 'a'.code && this.code <= 'z'.code)
            return this.code - 'a'.code + 1
        return 0
    }

    infix fun String.intersect(other:String):String = (this.toSet() intersect other.toSet()).joinToString("")


    fun part1(input: List<String>): Int  =
        input.asSequence().map { it to it.length }
            .map { it.first.substring(0, it.second/2) to it.first.substring(it.second/2) }
            .map { it.first intersect it.second }
            .map { it.first() }
            .sumOf { it.toPriority() }

    fun part2(input: List<String>): Int  =
        input.chunked(3)
            .map { it.reduce{acc, s -> acc intersect s } }
            .map { it[0] }
            .sumOf { it.toPriority() }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day03/day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("day03/day03_input")
    println(part1(input))
    println(part2(input))
}