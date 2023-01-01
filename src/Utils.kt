import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.abs

fun readByYearAndDay(year: Int, day: Int, name: String) =
    File("src/year$year/day${day.toString().padStart(2, '0')}/$name.txt")

fun readTestFileByYearAndDay(year: Int, day: Int) = readByYearAndDay(year, day, "input_test").readLines()
fun readInputFileByYearAndDay(year: Int, day: Int) = readByYearAndDay(year, day, "input").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

data class Point(val x: Int, val y: Int) {
    infix fun isHorizontalTo(other: Point): Boolean = this.y == other.y
    infix fun isVerticalTo(other: Point): Boolean = this.x == other.x
    infix fun isDiagonalTo(other: Point): Boolean = abs(this.x - other.x) == abs(this.y - other.y)
    fun moveBy(x: Int, y: Int): Point = Point(this.x + x, this.y + y)
    infix fun moveBy(by: Point): Point = Point(this.x + by.x, this.y + by.y)

    infix fun manhattanDistance(to: Point) = abs(this.x - to.x) + abs(this.y - to.y)

    // only works for points that are either horizontal, vertical or diagonal to each other
    operator fun rangeTo(other: Point): List<Point> {
        return if (this isHorizontalTo other) {
            val minX = Integer.min(this.x, other.x)
            val maxX = Integer.max(this.x, other.x)
            (minX..maxX).map { Point(it, this.y) }.toList()
        } else if (this isVerticalTo other) {
            val minY = Integer.min(this.y, other.y)
            val maxY = Integer.max(this.y, other.y)
            (minY..maxY).map { Point(this.x, it) }.toList()
        } else if (this isDiagonalTo other) {
            val dirX = if (this.x > other.x) -1 else 1
            val dirY = if (this.y > other.y) -1 else 1
            (0..abs(this.x - other.x)).map { this.moveBy(it * dirX, it * dirY) }.toList()
        } else listOf()
    }

    operator fun times(other: Int): Point = Point(x * other, y * other)
    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
    operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
}
