package com.islamic.app.prayer

import java.util.Calendar
import kotlin.math.*

data class CalculatedTimes(
    val fajr: Calendar,
    val dhuhr: Calendar,
    val asr: Calendar,
    val maghrib: Calendar,
    val isha: Calendar,
    val currentPrayer: String,
    val nextPrayer: String,
    val nextPrayerTimeFormatted: String,
    val remainingFormatted: String
)

object PrayerEngine {
    private fun dSin(d: Double) = sin(Math.toRadians(d))
    private fun dCos(d: Double) = cos(Math.toRadians(d))
    private fun dTan(d: Double) = tan(Math.toRadians(d))
    private fun dArcSin(x: Double) = Math.toDegrees(asin(x))
    private fun dArcCos(x: Double) = Math.toDegrees(acos(x))
    private fun dArcTan2(y: Double, x: Double) = Math.toDegrees(atan2(y, x))
    private fun fixAngle(a: Double) = a - 360.0 * floor(a / 360.0)
    private fun fixHour(h: Double) = h - 24.0 * floor(h / 24.0)

    fun calculate(lat: Double, lng: Double, cal: Calendar = Calendar.getInstance()): CalculatedTimes {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val tz = cal.timeZone.getOffset(cal.timeInMillis) / 3600000.0

        var y = year.toDouble()
        var m = month.toDouble()
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100)
        val b = 2 - a + floor(a / 4)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
        val d = jd - 2451545.0

        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * dSin(g) + 0.020 * dSin(2 * g))
        val e = 23.439 - 0.00000036 * d
        val ra = fixHour(dArcTan2(dCos(e) * dSin(l), dCos(l)) / 15.0)
        val decl = dArcSin(dSin(e) * dSin(l))
        val eqt = q / 15.0 - ra

        val noon = fixHour(12.0 + tz - lng / 15.0 - eqt)

        fun hourAngle(angle: Double): Double {
            val cosVal = (-dSin(angle) - dSin(lat) * dSin(decl)) / (dCos(lat) * dCos(decl))
            return if (cosVal in -1.0..1.0) dArcCos(cosVal) / 15.0 else 0.0
        }

        val fajrHour = noon - hourAngle(18.0)
        val sunsetHour = noon + hourAngle(0.833)
        val ishaHour = noon + hourAngle(17.0)

        val asrAngle = -dArcTan2(1.0 + dTan(abs(lat - decl)), 1.0)
        val asrHour = noon + hourAngle(abs(asrAngle))
        val maghribHour = sunsetHour

        fun toCalendar(h: Double): Calendar {
            val c = cal.clone() as Calendar
            val hr = floor(h).toInt()
            val min = floor((h - hr) * 60.0).toInt()
            val sec = floor(((h - hr) * 60.0 - min) * 60.0).toInt()
            c.set(Calendar.HOUR_OF_DAY, hr)
            c.set(Calendar.MINUTE, min)
            c.set(Calendar.SECOND, sec)
            c.set(Calendar.MILLISECOND, 0)
            return c
        }

        val fajrCal = toCalendar(fajrHour)
        val dhuhrCal = toCalendar(noon)
        val asrCal = toCalendar(asrHour)
        val maghribCal = toCalendar(maghribHour)
        val ishaCal = toCalendar(ishaHour)

        val now = System.currentTimeMillis()

        var cur = "Isha"
        var next = "Fajr"
        var nextCal = fajrCal.clone() as Calendar
        if (now < fajrCal.timeInMillis) {
            cur = "Isha"
            next = "Fajr"
            nextCal = fajrCal
        } else if (now < dhuhrCal.timeInMillis) {
            cur = "Fajr"
            next = "Dhuhr"
            nextCal = dhuhrCal
        } else if (now < asrCal.timeInMillis) {
            cur = "Dhuhr"
            next = "Asr"
            nextCal = asrCal
        } else if (now < maghribCal.timeInMillis) {
            cur = "Asr"
            next = "Maghrib"
            nextCal = maghribCal
        } else if (now < ishaCal.timeInMillis) {
            cur = "Maghrib"
            next = "Isha"
            nextCal = ishaCal
        } else {
            cur = "Isha"
            next = "Fajr"
            nextCal = (fajrCal.clone() as Calendar).apply { add(Calendar.DATE, 1) }
        }

        val diffSec = max(0L, (nextCal.timeInMillis - now) / 1000L)
        val hRem = diffSec / 3600
        val mRem = (diffSec % 3600) / 60
        val sRem = diffSec % 60

        val nextFmt = String.format("%02d:%02d", nextCal.get(Calendar.HOUR_OF_DAY), nextCal.get(Calendar.MINUTE))
        val remFmt = String.format("%02dh:%02dm:%02ds", hRem, mRem, sRem)

        return CalculatedTimes(
            fajr = fajrCal, dhuhr = dhuhrCal, asr = asrCal, maghrib = maghribCal, isha = ishaCal,
            currentPrayer = cur, nextPrayer = next, nextPrayerTimeFormatted = nextFmt, remainingFormatted = remFmt
        )
    }
}
