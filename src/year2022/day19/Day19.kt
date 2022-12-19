@file:OptIn(ExperimentalTime::class)

package year2022.day19

import readInputFileByYearAndDay
import readTestFileByYearAndDay
import kotlin.math.max
import kotlin.time.ExperimentalTime

data class Blueprint(
    val num: Int,
    val oreRobotCost: Int,
    val clayRobotCost: Int,
    val obsidanRobotCostOre: Int,
    val obsidanRobotCostClay: Int,
    val geodeRobotCostOre: Int,
    val geodeRobotCostObsidan: Int
) {
    companion object {

        val lineFormat =
            Regex("""Blueprint (\d*): Each ore robot costs (\d*) ore. Each clay robot costs (\d*) ore. Each obsidian robot costs (\d*) ore and (\d*) clay. Each geode robot costs (\d*) ore and (\d*) obsidian.""")

        fun from(line: String): Blueprint {
            val matchResult = lineFormat.find(line)
            return Blueprint(
                num = matchResult!!.groups[1]!!.value.toInt(),
                oreRobotCost = matchResult.groups[2]!!.value.toInt(),
                clayRobotCost = matchResult.groups[3]!!.value.toInt(),
                obsidanRobotCostOre = matchResult.groups[4]!!.value.toInt(),
                obsidanRobotCostClay = matchResult.groups[5]!!.value.toInt(),
                geodeRobotCostOre = matchResult.groups[6]!!.value.toInt(),
                geodeRobotCostObsidan = matchResult.groups[7]!!.value.toInt(),
            )
        }
    }
}

data class State(
    val ore: Int,
    val clay: Int,
    val obsidian: Int,
    val geodes: Int,
    val oreBots: Int,
    val clayBots: Int,
    val obsidianBots: Int,
    val geodeBots: Int,
    val timeLeft: Int
) {
    fun buyOreBot(blueprint: Blueprint): State = State(
        ore - blueprint.oreRobotCost + oreBots,
        clay + clayBots,
        obsidian + obsidianBots,
        geodes + geodeBots,
        oreBots + 1,
        clayBots,
        obsidianBots,
        geodeBots,
        timeLeft - 1
    )

    fun buyClayBot(blueprint: Blueprint): State = State(
        ore + oreBots - blueprint.clayRobotCost,
        clay + clayBots,
        obsidian + obsidianBots,
        geodes + geodeBots,
        oreBots,
        clayBots + 1,
        obsidianBots,
        geodeBots,
        timeLeft - 1
    )

    fun buyObsidianBot(blueprint: Blueprint): State = State(
        ore + oreBots - blueprint.obsidanRobotCostOre,
        clay + clayBots - blueprint.obsidanRobotCostClay,
        obsidian + obsidianBots,
        geodes + geodeBots,
        oreBots,
        clayBots,
        obsidianBots + 1,
        geodeBots,
        timeLeft - 1
    )

    fun buyGeodeBot(blueprint: Blueprint): State = State(
        ore + oreBots - blueprint.geodeRobotCostOre,
        clay + clayBots,
        obsidian + obsidianBots - blueprint.geodeRobotCostObsidan,
        geodes + geodeBots,
        oreBots,
        clayBots,
        obsidianBots,
        geodeBots + 1,
        timeLeft - 1
    )
}

fun maxForBlueprint(blueprint: Blueprint, time: Int): Int {
    val startState = State(0, 0, 0, 0, 1, 0, 0, 0, time)
    val pile = ArrayDeque(listOf(startState))
    val memory = mutableSetOf<State>()
    var maxGeodes = Int.MIN_VALUE

    val maxOreCost = listOf(
        blueprint.oreRobotCost,
        blueprint.clayRobotCost,
        blueprint.obsidanRobotCostOre,
        blueprint.geodeRobotCostOre
    ).max()

    while (pile.isNotEmpty()) {

        var (ore, clay, obsidian, geodes, oreBots, clayBots, obsidianBots, geodeBots, timeLeft) = pile.removeFirst()
        maxGeodes = max(maxGeodes, geodes)

        if (timeLeft == 0) continue

        // useless to have more ore bots than what you can spend per turn
        if (oreBots >= maxOreCost) oreBots = maxOreCost
        // useless to have more clay bots than what you can spend per turn
        if (clayBots >= blueprint.obsidanRobotCostClay) clayBots = blueprint.obsidanRobotCostClay
        // useless to have more obsidian bots than what you can spend per turn
        if (obsidianBots >= blueprint.geodeRobotCostObsidan) obsidianBots = blueprint.geodeRobotCostObsidan
        // useless to have more ore than what you can spend in the remaining time, adjusted for what bots produce
        if (ore >= timeLeft * maxOreCost - (oreBots * (timeLeft - 1))) ore =
            timeLeft * maxOreCost - (oreBots * (timeLeft - 1))
        // useless to have more clay than what you can spend in the remaining time, adjusted for what bots produce
        if (clay >= timeLeft * blueprint.obsidanRobotCostClay - (clayBots * (timeLeft - 1))) clay =
            timeLeft * blueprint.obsidanRobotCostClay - (clayBots * (timeLeft - 1))
        // useless to have more obsidian than what you can spend in the remaining time, adjusted for what bots produce
        if (obsidian >= timeLeft * blueprint.geodeRobotCostObsidan - (obsidianBots * (timeLeft - 1))) obsidian =
            timeLeft * blueprint.geodeRobotCostObsidan - (obsidianBots * (timeLeft - 1))


        val normalizedState =
            State(ore, clay, obsidian, geodes, oreBots, clayBots, obsidianBots, geodeBots, timeLeft)
        if (!memory.add(normalizedState)) continue

        // do nothing
        pile.addLast(
            State(
                ore + oreBots, clay + clayBots, obsidian + obsidianBots, geodes + geodeBots,
                oreBots, clayBots, obsidianBots, geodeBots, timeLeft - 1
            )
        )
        // buy ore bot
        if (ore >= blueprint.oreRobotCost) pile.addLast(normalizedState.buyOreBot(blueprint))
        // buy clay bot
        if (ore >= blueprint.clayRobotCost) pile.addLast(normalizedState.buyClayBot(blueprint))
        // buy obsidian bot
        if (ore >= blueprint.obsidanRobotCostOre && clay >= blueprint.obsidanRobotCostClay)
            pile.addLast(normalizedState.buyObsidianBot(blueprint))
        // buy geode bot
        if (ore >= blueprint.geodeRobotCostOre && obsidian >= blueprint.geodeRobotCostObsidan)
            pile.addLast(normalizedState.buyGeodeBot(blueprint))
    }

    return maxGeodes
}


fun main() {

    fun part1(input: List<String>): Int = input.map { Blueprint.from(it) }.sumOf { it.num * maxForBlueprint(it, 24) }

    fun part2(input: List<String>): Int =
        input.map { Blueprint.from(it) }.take(3).map { maxForBlueprint(it, 32) }.reduce(Int::times)

    val testInput = readTestFileByYearAndDay(2022, 19)
    check(part1(testInput) == 33)
    check(part2(testInput) == 56 * 62)

    val input = readInputFileByYearAndDay(2022, 19)
    println(part1(input))
    println(part2(input))
}
