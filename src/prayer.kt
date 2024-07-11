import java.time.LocalDate
import java.time.temporal.ChronoField

fun toJulianDate(year: Int, month: Int, day: Int): Int {
    val a = (14 - month) / 12
    val y = year + 4800 - a
    val m = month + 12 * a - 3
    return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
}

fun main() {
    val gregorianDate = LocalDate.of(2024, 7, 11)
    val year = gregorianDate.get(ChronoField.YEAR)
    val month = gregorianDate.get(ChronoField.MONTH_OF_YEAR)
    val day = gregorianDate.get(ChronoField.DAY_OF_MONTH)

    val julianDate = toJulianDate(year, month, day)
    println("Julian Date: $julianDate")
}