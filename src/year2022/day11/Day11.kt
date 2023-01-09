package year2022.day11

fun main() {
    data class Monkey(
        val items: MutableList<Long>,
        val worryOperation: (Long) -> Long,
        val divisor: Int,
        val targetDivisible: Int,
        val targetNotDivisible: Int
    ) {
        var inspected = 0L
        fun playRound(otherMonkeys: List<Monkey>, postAction: (Long) -> Long) {
            inspected += items.size
            items.map(worryOperation)
                .map(postAction)
                .forEach { otherMonkeys[if (it % divisor == 0L) targetDivisible else targetNotDivisible].catch(it) }
            items.clear()
        }

        fun catch(item: Long) = this.items.add(item)
    }

    fun part1(input: List<Monkey>): Long = input.apply {
        repeat(20) {
            this.forEach { monkey -> monkey.playRound(input) { it / 3 } }
        }
    }.map { it.inspected }.sortedDescending().take(2).reduce(Long::times)


    fun part2(input: List<Monkey>): Long = input.apply {
        val wrapAround = this.map { it.divisor }.reduce(Int::times)
        repeat(10_000) {
            this.forEach { monkey -> monkey.playRound(input) { it % wrapAround } }
        }
    }.map { it.inspected }.sortedDescending().take(2).reduce(Long::times)


    // parsing the input is annoying, so I hardcode functions that give me the monkeys
    fun testInput(): List<Monkey> = listOf(
        Monkey(
            items = mutableListOf(79, 98),
            worryOperation = { it * 19 },
            divisor = 23,
            targetDivisible = 2,
            targetNotDivisible = 3,
        ),
        Monkey(
            items = mutableListOf(54, 65, 75, 74),
            worryOperation = { it + 6 },
            divisor = 19,
            targetDivisible = 2,
            targetNotDivisible = 0,
        ),
        Monkey(
            items = mutableListOf(79, 60, 97),
            worryOperation = { it * it },
            divisor = 13,
            targetDivisible = 1,
            targetNotDivisible = 3,
        ),
        Monkey(
            items = mutableListOf(74),
            worryOperation = { it + 3 },
            divisor = 17,
            targetDivisible = 0,
            targetNotDivisible = 1,
        ),
    )

    fun input(): List<Monkey> = listOf(
        Monkey(
            items = mutableListOf(66, 71, 94),
            worryOperation = { it * 5 },
            divisor = 3,
            targetDivisible = 7,
            targetNotDivisible = 4,
        ),
        Monkey(
            items = mutableListOf(70),
            worryOperation = { it + 6 },
            divisor = 17,
            targetDivisible = 3,
            targetNotDivisible = 0,
        ),
        Monkey(
            items = mutableListOf(62, 68, 56, 65, 94, 78),
            worryOperation = { it + 5 },
            divisor = 2,
            targetDivisible = 3,
            targetNotDivisible = 1,
        ),
        Monkey(
            items = mutableListOf(89, 94, 94, 67),
            worryOperation = { it + 2 },
            divisor = 19,
            targetDivisible = 7,
            targetNotDivisible = 0,
        ),
        // 4
        Monkey(
            items = mutableListOf(71, 61, 73, 65, 98, 98, 63),
            worryOperation = { it * 7 },
            divisor = 11,
            targetDivisible = 5,
            targetNotDivisible = 6,
        ),
        Monkey(
            items = mutableListOf(55, 62, 68, 61, 60),
            worryOperation = { it + 7 },
            divisor = 5,
            targetDivisible = 2,
            targetNotDivisible = 1,
        ),
        Monkey(
            items = mutableListOf(93, 91, 69, 64, 72, 89, 50, 71),
            worryOperation = { it + 1 },
            divisor = 13,
            targetDivisible = 5,
            targetNotDivisible = 2,
        ),
        Monkey(
            items = mutableListOf(76, 50),
            worryOperation = { it * it },
            divisor = 7,
            targetDivisible = 4,
            targetNotDivisible = 6,
        ),
    )

    check(part1(testInput()) == 10605L)
    check(part2(testInput()) == 2713310158)

    println(part1(input()))
    println(part2(input()))
}