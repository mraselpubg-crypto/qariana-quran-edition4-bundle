package com.karianaquran.reader.data.prayer

import java.util.Calendar
import java.util.Date
import kotlin.math.*

data class PrayerTime(
    val nameBn: String,
    val nameEn: String,
    val nameAr: String,
    val hour: Int,
    val minute: Int,
    val timeFormattedBn: String,
    val rakatBn: String,
)

data class NextPrayerInfo(
    val currentPrayerNameBn: String,
    val nextPrayerNameBn: String,
    val nextPrayerTimeBn: String,
    val remainingHours: Int,
    val remainingMinutes: Int,
    val remainingSeconds: Int,
    val totalRemainingSeconds: Long,
    val progressPercent: Float,
    val isAzanActive: Boolean,
    val schedule: List<PrayerTime>,
    val currentPrayerNameEn: String = "",
    val nextPrayerNameEn: String = "",
    val nextPrayerTimeEn: String = "",
)

object PrayerCalculator {

    const val DEFAULT_LATITUDE = 23.8103
    const val DEFAULT_LONGITUDE = 90.4125
    const val DEFAULT_TIMEZONE = 6.0

    private fun fixAngle(a: Double): Double {
        val res = a - 360.0 * floor(a / 360.0)
        return if (res < 0) res + 360.0 else res
    }

    private fun fixHour(a: Double): Double {
        val res = a - 24.0 * floor(a / 24.0)
        return if (res < 0) res + 24.0 else res
    }

    private fun dtr(d: Double): Double = Math.toRadians(d)
    private fun rtd(r: Double): Double = Math.toDegrees(r)

    private fun getSunPosition(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(dtr(g)) + 0.020 * sin(dtr(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val decl = rtd(asin(sin(dtr(e)) * sin(dtr(l))))
        var ra = rtd(atan2(cos(dtr(e)) * sin(dtr(l)), cos(dtr(l)))) / 15.0
        ra = fixHour(ra)
        val eqt = q / 15.0 - ra
        return Pair(decl, eqt)
    }

    private fun getJulianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    fun calculate(
        calendar: Calendar,
        latitude: Double = DEFAULT_LATITUDE,
        longitude: Double = DEFAULT_LONGITUDE,
        timezone: Double = DEFAULT_TIMEZONE,
        isHanafi: Boolean = true
    ): List<PrayerTime> {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val jd = getJulianDate(year, month, day)
        val (decl, eqt) = getSunPosition(jd)

        val dhuhr = fixHour(12.0 + timezone - longitude / 15.0 - eqt)

        fun sunAngleTime(angle: Double, ccw: Boolean): Double {
            val n = sin(dtr(angle)) - sin(dtr(latitude)) * sin(dtr(decl))
            val d = cos(dtr(latitude)) * cos(dtr(decl))
            val ratio = (n / d).coerceIn(-1.0, 1.0)
            val t = rtd(acos(ratio)) / 15.0
            return fixHour(dhuhr + (if (ccw) -t else t))
        }

        val fajrHours = sunAngleTime(-18.0, ccw = true)
        val sunriseHours = sunAngleTime(-0.833, ccw = true)
        val sunsetHours = sunAngleTime(-0.833, ccw = false)

        val asrFactor = if (isHanafi) 2.0 else 1.0
        val asrAlt = rtd(atan(1.0 / (asrFactor + tan(dtr(abs(latitude - decl))))))
        val asrHours = sunAngleTime(asrAlt, ccw = false)
        val maghribHours = sunsetHours
        val ishaHours = sunAngleTime(-18.0, ccw = false)

        fun toHourMin(hVal: Double): Pair<Int, Int> {
            val totalMins = (hVal * 60.0).roundToInt()
            val h = (totalMins / 60) % 24
            val m = totalMins % 60
            return Pair(h, m)
        }

        fun toBnTime(h: Int, m: Int): String {
            val displayH = if (h == 0) 12 else if (h > 12) h - 12 else h
            val period = if (h < 12) "ভোর" else if (h == 12) "দুপুর" else if (h < 16) "দুপুর" else if (h < 18) "বিকাল" else if (h < 20) "সন্ধ্যা" else "রাত"
            val hBn = displayH.toString().toBnDigits()
            val mBn = String.format("%02d", m).toBnDigits()
            return "$hBn:$mBn $period"
        }

        val (fH, fM) = toHourMin(fajrHours)
        val (sH, sM) = toHourMin(sunriseHours)
        val (dH, dM) = toHourMin(dhuhr)
        val (aH, aM) = toHourMin(asrHours)
        val (mH, mM) = toHourMin(maghribHours)
        val (iH, iM) = toHourMin(ishaHours)

        return listOf(
            PrayerTime("ফজর", "Fajr", "الفجر", fH, fM, toBnTime(fH, fM), "৪ রাকাত (২ সুন্নাত, ২ ফরজ)"),
            PrayerTime("সূর্যোদয়", "Sunrise", "الشروق", sH, sM, toBnTime(sH, sM), "নিষিদ্ধ সময়"),
            PrayerTime("যোহর", "Dhuhr", "الظهر", dH, dM, toBnTime(dH, dM), "১২ রাকাত (৪ সুন্নাত, ৪ ফরজ, ২ সুন্নাত, ২ নফল)"),
            PrayerTime("আসর", "Asr", "العصر", aH, aM, toBnTime(aH, aM), "৪ রাকাত (৪ ফরজ)"),
            PrayerTime("মাগরিব", "Maghrib", "المغرب", mH, mM, toBnTime(mH, mM), "৭ রাকাত (৩ ফরজ, ২ সুন্নাত, ২ নফল)"),
            PrayerTime("এশা", "Isha", "العشاء", iH, iM, toBnTime(iH, iM), "১৭ রাকাত (৪ সুন্নাত, ৪ ফরজ, ২ সুন্নাত, ২ নফল, ৩ বিতর, ২ নফল)")
        )
    }

    fun getNextPrayer(
        now: Date = Date(),
        latitude: Double = DEFAULT_LATITUDE,
        longitude: Double = DEFAULT_LONGITUDE,
        timezone: Double = DEFAULT_TIMEZONE,
        isHanafi: Boolean = true
    ): NextPrayerInfo {
        val cal = Calendar.getInstance().apply { time = now }
        val schedule = calculate(cal, latitude, longitude, timezone, isHanafi)

        val currentSecOfDay = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND)

        // নামাজগুলোর সেকেন্ডে সময়
        val times = schedule.map { it.hour * 3600 + it.minute * 60 }
        val fajrSec = times[0]
        val sunriseSec = times[1]
        val dhuhrSec = times[2]
        val asrSec = times[3]
        val maghribSec = times[4]
        val ishaSec = times[5]

        var currentPrayer = "এশা"
        var nextPrayer = schedule[0] // ফজর
        var prevPrayerSec = ishaSec
        var nextPrayerSec = fajrSec + 86400 // পরের দিন

        when {
            currentSecOfDay < fajrSec -> {
                currentPrayer = "তাহাজ্জুদ / সাহরি"
                nextPrayer = schedule[0] // ফজর
                prevPrayerSec = ishaSec - 86400
                nextPrayerSec = fajrSec
            }
            currentSecOfDay < sunriseSec -> {
                currentPrayer = "ফজর"
                nextPrayer = schedule[1] // সূর্যোদয়
                prevPrayerSec = fajrSec
                nextPrayerSec = sunriseSec
            }
            currentSecOfDay < dhuhrSec -> {
                currentPrayer = "ইশরাক / চাশত"
                nextPrayer = schedule[2] // যোহর
                prevPrayerSec = sunriseSec
                nextPrayerSec = dhuhrSec
            }
            currentSecOfDay < asrSec -> {
                currentPrayer = "যোহর"
                nextPrayer = schedule[3] // আসর
                prevPrayerSec = dhuhrSec
                nextPrayerSec = asrSec
            }
            currentSecOfDay < maghribSec -> {
                currentPrayer = "আসর"
                nextPrayer = schedule[4] // মাগরিব
                prevPrayerSec = asrSec
                nextPrayerSec = maghribSec
            }
            currentSecOfDay < ishaSec -> {
                currentPrayer = "মাগরিব"
                nextPrayer = schedule[5] // এশা
                prevPrayerSec = maghribSec
                nextPrayerSec = ishaSec
            }
            else -> {
                currentPrayer = "এশা"
                // আগামীকালের ফজর
                val tomCal = Calendar.getInstance().apply {
                    time = now
                    add(Calendar.DAY_OF_YEAR, 1)
                }
                val tomSchedule = calculate(tomCal, latitude, longitude, timezone, isHanafi)
                nextPrayer = tomSchedule[0]
                prevPrayerSec = ishaSec
                nextPrayerSec = tomSchedule[0].hour * 3600 + tomSchedule[0].minute * 60 + 86400
            }
        }

        val totalInterval = (nextPrayerSec - prevPrayerSec).coerceAtLeast(1)
        val elapsed = (currentSecOfDay - prevPrayerSec).coerceAtLeast(0)
        val progress = (elapsed.toFloat() / totalInterval.toFloat()).coerceIn(0f, 1f)

        val remainingSec = (nextPrayerSec - currentSecOfDay).coerceAtLeast(0)
        val remH = (remainingSec / 3600).toInt()
        val remM = ((remainingSec % 3600) / 60).toInt()
        val remS = (remainingSec % 60).toInt()

        // ওয়াক্ত হওয়ার প্রথম ৫ মিনিট আযান একটিভ ধরা হয়
        val isAzanActive = (currentSecOfDay - prevPrayerSec) in 0..300 && currentPrayer in listOf("ফজর", "যোহর", "আসর", "মাগরিব", "এশা")

        val currentPrayerEn = when (currentPrayer) {
            "ফজর" -> "Fajr"
            "যোহর" -> "Dhuhr"
            "আসর" -> "Asr"
            "মাগরিব" -> "Maghrib"
            "এশা" -> "Isha"
            "সূর্যোদয়" -> "Sunrise"
            else -> currentPrayer
        }
        val nextTime12 = if (nextPrayer.hour > 12) nextPrayer.hour - 12 else if (nextPrayer.hour == 0) 12 else nextPrayer.hour
        val amPm = if (nextPrayer.hour >= 12) "PM" else "AM"
        val nextPrayerTimeEn = String.format("%02d:%02d %s", nextTime12, nextPrayer.minute, amPm)

        return NextPrayerInfo(
            currentPrayerNameBn = currentPrayer,
            nextPrayerNameBn = nextPrayer.nameBn,
            nextPrayerTimeBn = String.format("%02d:%02d", nextTime12, nextPrayer.minute).toBnDigits(),
            remainingHours = remH,
            remainingMinutes = remM,
            remainingSeconds = remS,
            totalRemainingSeconds = remainingSec.toLong(),
            progressPercent = progress,
            isAzanActive = isAzanActive,
            schedule = schedule,
            currentPrayerNameEn = currentPrayerEn,
            nextPrayerNameEn = nextPrayer.nameEn.ifEmpty { currentPrayerEn },
            nextPrayerTimeEn = nextPrayerTimeEn
        )
    }

    private fun String.toBnDigits(): String {
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = java.lang.StringBuilder()
        for (c in this) {
            if (c in '0'..'9') {
                sb.append(bnDigits[c - '0'])
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }
}
