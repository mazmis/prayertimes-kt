import java.time.LocalDate


fun main() {
    val city = "Cape Town"
    val date = LocalDate.of(2024, 7, 11)
    val latitude = -33.9221 // Cape Town latitude
    val longitude = 18.4231 // Cape Town longitude
    val timezone = 2.0 // Cape Town timezone

    val prayerTimesCapeTown = calculatePrayerTimes(date, latitude, longitude, timezone)
    println("Prayer Times on $date in $city:")
    prayerTimesCapeTown.forEach { (name, time) ->
        println("$name: $time")
    }

}
