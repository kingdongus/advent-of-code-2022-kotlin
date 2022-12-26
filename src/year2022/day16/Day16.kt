package year2022.day16

import readInputFileByYearAndDay
import readTestFileByYearAndDay
import java.lang.Integer.max
import java.lang.Integer.min

data class Valve(val name: String, val flowRate: Int, val connections: MutableList<Valve> = mutableListOf()) {

    val cost = mutableMapOf(this to 0)
    fun findWayTo(other: Valve, seen: List<Valve> = listOf()): Int {

        if (connections.contains(other)) {
            cost[other] = 1
            return 1
        }
        val nextHops = connections.filterNot { seen.contains(it) }
        if (nextHops.isEmpty()) return Int.MAX_VALUE

        val min = 1 + nextHops.minOf { it.findWayTo(other, seen + listOf(this)) }
        cost[other] = min(cost.getOrDefault(other, Int.MAX_VALUE), min)
        return cost[other]!!
    }

    override fun toString(): String =
        "Valve (name=$name, flowRate=$flowRate, connections=${connections.map { it.name }}, cost=${cost.map { it.key.name to it.value }})"

    override fun hashCode(): Int = name.hashCode() + flowRate.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Valve

        if (name != other.name) return false
        if (flowRate != other.flowRate) return false

        return true
    }
}

// could use this directly as memoization key, but ints are faster,
// and I don't trust myself to specify a correct hash code by hand
data class State(val totalFlow: Int, val position: Int, val timeLeft: Int)

fun main() {

    fun initializeValves(input: List<String>): List<Valve> {
        val regex = Regex("""Valve ([A-Z]{2})(.*)rate=(\d*)(.*)valves? (.*)""")

        val valves = input.map { regex.find(it) }
            .map { Valve(name = it!!.groups[1]!!.value, flowRate = it.groups[3]!!.value.toInt()) }
            .toList()

        input.map { regex.find(it) }
            .map { it!!.groups[1]!!.value to it.groups[5]!!.value }
            .map { valves.first { valve -> valve.name == it.first } to it.second.split(",") }
            .map { it.first to it.second.map { name -> valves.first { valve -> valve.name == name.trim() } } }
            .forEach { it.first.connections.addAll(it.second) }

        valves.forEach { valve -> valves.forEach { valve.findWayTo(it) } }
        return valves
    }

    fun maximizeSteam(valves: List<Valve>, maxTime: Int): Int {
        val memory = mutableMapOf<Int, Int>()

        fun aux(current: Valve, seen: List<Valve>, totalFlow: Int, maxTime: Int, timeLeft: Int): Int {

            if (timeLeft == 0) return 0

            val key = State(totalFlow, valves.indexOf(current), timeLeft).hashCode()
            if (memory.contains(key)) return memory[key]!!

            // if current valve has flow greater 0 and is not opened, might open it
            var res = 0
            if (current.flowRate > 0 && !(seen.contains(current))) {
                res = max(
                    res,
                    (timeLeft - 1) * current.flowRate + aux(
                        current,
                        seen + listOf(current),
                        totalFlow + current.flowRate,
                        maxTime,
                        timeLeft - 1
                    )
                )
            }
            // for all connections, check what happens if we go there without opening the current one
            current.connections.forEach {
                res = max(res, aux(it, seen, totalFlow, maxTime, timeLeft - 1))
            }

            memory[key] = res

            return res
        }

        return aux(valves.first(), listOf(), 0, maxTime, maxTime)
    }

    fun part1(input: List<String>): Int = maximizeSteam(initializeValves(input), 30)

    fun part2(input: List<String>): Int = 0


    val testInput = readTestFileByYearAndDay(2022, 16)
    check(part1(testInput) == 1651)
    check(part2(testInput) == 0)

    val input = readInputFileByYearAndDay(2022, 16)
    println(part1(input))
    println(part2(input))
}
