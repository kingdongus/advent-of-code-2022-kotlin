package year2022.day07

import readInputFileByYearAndDay
import readTestFileByYearAndDay
import java.util.*

fun main() {

    data class File(val name: String, val size: Int)

    class Folder(val name: String = "/", val parent: Folder? = null) {
        val subFolders = mutableListOf<Folder>()
        val files = mutableListOf<File>()
        fun size(): Int = subFolders.sumOf { it.size() } + files.sumOf { it.size }
    }

    operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)

    fun parseFileSystem(input: List<String>): Folder {
        val root = Folder()
        var currentFolder = root

        input.forEach {
            when (it) {
                "$ cd /" -> currentFolder = root
                "$ cd .." -> currentFolder = currentFolder.parent!!
                in Regex(""". cd [a-zA-Z]+""") -> {
                    val name = it.split(" ").last()
                    currentFolder = currentFolder.subFolders.first { folder -> folder.name == name }
                }

                in Regex("""dir [a-zA-Z]+""") -> {
                    val name = it.split(" ").last()
                    currentFolder.subFolders.add(Folder(name, currentFolder))
                }

                in Regex("""[0-9]+ [a-zA-Z/.]+""") -> {
                    val name = it.split(" ").last()
                    val size = it.split(" ").first().toInt()
                    currentFolder.files.add(File(name, size))
                }

                else -> {
                    // do nothing on ls and unrecognized commands
                }
            }
        }
        return root
    }

    fun part1(input: List<String>): Int {
        val root = parseFileSystem(input)

        val toProcess = Stack<Folder>()
        toProcess.push(root)

        var sub100kSum = 0
        while (toProcess.isNotEmpty()) {
            val next = toProcess.pop()
            val size = next.size()
            if (size < 100000) sub100kSum += size
            next.subFolders.forEach { toProcess.push(it) }
        }

        return sub100kSum
    }

    fun part2(input: List<String>): Int {
        val root = parseFileSystem(input)


        val totalSpace = 70000000
        val unusedSpaceNeeded = 30000000
        val unusedSpace = totalSpace - root.size()
        val freeSpaceNeeded = unusedSpaceNeeded - unusedSpace

        val toProcess = Stack<Folder>()
        toProcess.push(root)
        var bestMatch = root

        while (toProcess.isNotEmpty()) {
            val next = toProcess.pop()
            val size = next.size()
            if (size < freeSpaceNeeded) continue
            next.subFolders.forEach { toProcess.push(it) }
            if (size < bestMatch.size()) bestMatch = next
        }

        return bestMatch.size()
    }

    val testInput = readTestFileByYearAndDay(2022, 7)
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInputFileByYearAndDay(2022, 7)
    println(part1(input))
    println(part2(input))
}
