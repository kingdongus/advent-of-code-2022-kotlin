import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

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
