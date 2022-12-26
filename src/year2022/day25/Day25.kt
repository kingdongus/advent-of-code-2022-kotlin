package year2022.day25

import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.pow

val snafuToDec = mapOf('=' to -2, '-' to -1, '0' to 0, '1' to 1, '2' to 2)

fun main() {

    fun decimalToSnafu(decimal: String): String {
        val dec = decimal.toLong()
        var digits = ""
        var remaining = dec

        while (remaining > 0) {
            digits = "012=-"[(remaining % 5).toInt()] + digits
            remaining -= ((remaining + 2) % 5) - 2
            remaining /= 5
        }

        return digits
    }

    fun snafuToDecimal(snafu: String) =
        snafu.reversed().mapIndexed { index, c -> snafuToDec[c]!! * 5.0.pow(index) }.map { it.toLong() }.sum()


    fun part1(input: List<String>): String =
        input.map { snafuToDecimal(it) }.sum().let { decimalToSnafu(it.toString()) }


    fun part2(input: List<String>): String {
        return "🙏"
    }

    val testInput = readTestFileByYearAndDay(2022, 25)
    check(part1(testInput) == "2=-1=0")
    check(part2(testInput) == "🙏")

    val input = readInputFileByYearAndDay(2022, 25)
    println(part1(input))
    println(part2(input))
}
