fun main(){

    // just do a hard lookup, since we have 9 values only
    // 1 / 2 / 3 + 0 / 3 / 6
    val scores = mapOf(
        "A X" to 4, // 1 + 3
        "A Y" to 8, // 2 + 6
        "A Z" to 3, // 3 + 0
        "B X" to 1, // 1 + 0
        "B Y" to 5, // 2 + 3
        "B Z" to 9, // 3 + 0
        "C X" to 7, // 1 + 6
        "C Y" to 2, // 2 + 0
        "C Z" to 6, // 3 + 3
    )

    // again, a hard lookup is enough
    val opponentMovesAndOutcomes = mapOf(
        "A X" to 3, // 3 + 0
        "A Y" to 4, // 1 + 3
        "A Z" to 8, // 2 + 6
        "B X" to 1, // 1 + 0
        "B Y" to 5, // 2 + 3
        "B Z" to 9, // 3 + 6
        "C X" to 2, // 2 + 0
        "C Y" to 6, // 3 + 3
        "C Z" to 7, // 1 + 6
    )


    fun part1(input: List<String>): Int = input.map { scores[it] }.mapNotNull { it }.sum()

    fun part2(input: List<String>): Int = input.map { opponentMovesAndOutcomes[it] }.mapNotNull { it }.sum()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("day02_input")
    println(part1(input))
    println(part2(input))
}