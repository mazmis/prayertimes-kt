import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import kotlin.math.*

fun toJulianDate(year: Int, month: Int, day: Int): Double {
    val a = (14 - month) / 12
    val y = year + 4800 - a
    val m = month + 12 * a - 3
    return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045.toDouble()
}

fun calculateSolarDeclination(julianDate: Double): Double {
    val n = julianDate - 2451545.0
    val g = (357.529 + 0.98560028 * n) % 360.0
    val q = (280.459 + 0.98564736 * n) % 360.0
    val lambda = q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g))
    val epsilon = 23.439 - 0.00000036 * n
    return Math.toDegrees(asin(sin(Math.toRadians(epsilon)) * sin(Math.toRadians(lambda))))
}

fun calculateTimeOfAngle(latitude: Double, declination: Double, angle: Double): Double {
    val latitudeRad = Math.toRadians(latitude)
    val declinationRad = Math.toRadians(declination)
    val angleRad = Math.toRadians(angle)
    val hourAngle = acos((sin(angleRad) - sin(latitudeRad) * sin(declinationRad)) / (cos(latitudeRad) * cos(declinationRad)))
    return 24.0 * hourAngle / (2 * Math.PI)
}

fun calculateSolarNoon(longitude: Double, timezone: Double, julianDate: Double): Double {
    val n = julianDate - 2451545.0 + 0.0008
    val jStar = n - longitude / 360.0
    val m = (357.5291 + 0.98560028 * jStar) % 360.0
    val c = 1.9148 * sin(Math.toRadians(m)) + 0.0200 * sin(Math.toRadians(2 * m)) + 0.0003 * sin(Math.toRadians(3 * m))
    val lambda = (m + 102.9372 + c + 180) % 360.0
    val jTransit = jStar + 0.0053 * sin(Math.toRadians(m)) - 0.0069 * sin(Math.toRadians(2 * lambda))
    return 12.0 + 24.0 * ((jTransit + 2451545.0 - julianDate) - 0.5 - timezone / 24.0)
}

fun toLocalTime(hours: Double, timezone: Double): LocalTime {
    val totalMinutes = ((hours + timezone) * 60).toInt()
    val adjustedMinutes = (totalMinutes % 1440 + 1440) % 1440  // Ensure within 0-1440 range
    val hoursPart = adjustedMinutes / 60
    val minutesPart = adjustedMinutes % 60
    return LocalTime.of(hoursPart, minutesPart)
}

fun calculatePrayerTimes(date: LocalDate, latitude: Double, longitude: Double, timezone: Double): Map<String, String> {
    val year = date.get(ChronoField.YEAR)
    val month = date.get(ChronoField.MONTH_OF_YEAR)
    val day = date.get(ChronoField.DAY_OF_MONTH)

    val julianDate = toJulianDate(year, month, day)
    val declination = calculateSolarDeclination(julianDate)
    val solarNoon = calculateSolarNoon(longitude, timezone, julianDate)

    val fajrTime = solarNoon - calculateTimeOfAngle(latitude, declination, -18.0)
    val dhuhrTime = solarNoon
    val asrTime = solarNoon + calculateTimeOfAngle(latitude, declination, atan(1 / (tan(Math.toRadians(abs(latitude - declination))) + Math.PI / 2)))
    val maghribTime = solarNoon + calculateTimeOfAngle(latitude, declination, 0.833)
    val ishaTime = solarNoon + calculateTimeOfAngle(latitude, declination, -17.0)

    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val fajr = toLocalTime(fajrTime, timezone).format(formatter)
    val dhuhr = toLocalTime(dhuhrTime, timezone).format(formatter)
    val asr = toLocalTime(asrTime, timezone).format(formatter)
    val maghrib = toLocalTime(maghribTime, timezone).format(formatter)
    val isha = toLocalTime(ishaTime, timezone).format(formatter)

    return mapOf(
        "Fajr" to fajr,
        "Dhuhr" to dhuhr,
        "Asr" to asr,
        "Maghrib" to maghrib,
        "Isha" to isha
    )
}

fun main() {
    val date = LocalDate.of(2024, 7, 11)
    val latitude = -33.9221 // Cape Town latitude
    val longitude = 18.4231 // Cape Town longitude
    val timezone = 2.0 // Cape Town timezone

    val prayerTimes = calculatePrayerTimes(date, latitude, longitude, timezone)
    println("Prayer Times on $date in Cape Town:")
    prayerTimes.forEach { (name, time) ->
        println("$name: $time")
    }
}
