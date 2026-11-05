package com.karianaquran.reader.data.prayer

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.firestore.SetOptions
import com.karianaquran.reader.QarianaApp
import com.karianaquran.reader.ui.components.toBn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * দৈনিক ৫ ওয়াক্ত নামাজের রেকর্ড মডেল
 */
data class DayPrayerRecord(
    val date: String, // "yyyy-MM-dd" e.g. "2026-10-29"
    val fajr: Boolean = false,
    val dhuhr: Boolean = false,
    val asr: Boolean = false,
    val maghrib: Boolean = false,
    val isha: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
) {
    val completedCount: Int
        get() = (if (fajr) 1 else 0) + (if (dhuhr) 1 else 0) + (if (asr) 1 else 0) + (if (maghrib) 1 else 0) + (if (isha) 1 else 0)

    val completionPercent: Float
        get() = (completedCount.toFloat() / 5f).coerceIn(0f, 1f)

    val isAllCompleted: Boolean
        get() = completedCount == 5

    fun isPrayerCompleted(key: String): Boolean = when (key.lowercase()) {
        "fajr" -> fajr
        "dhuhr" -> dhuhr
        "asr" -> asr
        "maghrib" -> maghrib
        "isha" -> isha
        else -> false
    }

    fun copyWithToggle(key: String): DayPrayerRecord = when (key.lowercase()) {
        "fajr" -> copy(fajr = !fajr, updatedAt = System.currentTimeMillis(), isSynced = false)
        "dhuhr" -> copy(dhuhr = !dhuhr, updatedAt = System.currentTimeMillis(), isSynced = false)
        "asr" -> copy(asr = !asr, updatedAt = System.currentTimeMillis(), isSynced = false)
        "maghrib" -> copy(maghrib = !maghrib, updatedAt = System.currentTimeMillis(), isSynced = false)
        "isha" -> copy(isha = !isha, updatedAt = System.currentTimeMillis(), isSynced = false)
        else -> this
    }
}

enum class TrackerPeriod {
    DAY, WEEK, MONTH, YEAR
}

data class TrackerStats(
    val totalRequired: Int,
    val totalCompleted: Int,
    val completionPercent: Float,
    val allPrayedDaysCount: Int,
    val streakDays: Int
)

/**
 * অফলাইন-ফার্স্ট নামাজ ট্র্যাকার রিপোজিটরি
 * - লোকাল মেমোরি ও ডিস্কে (JSON) তাৎক্ষণিক সেভ (নেট ছাড়াও ১০০% কাজ করে)
 * - নেট পেলেই ফায়ারবেস ক্লাউড ফায়ারস্টোরে স্বয়ংক্রিয় ব্যাকআপ ও সিঙ্ক
 * - ওয়াক্ত শুরু হওয়ার আগে আগাম টিক দেওয়া ব্লক করার লজিক
 */
object PrayerTrackerRepository {

    private const val FILE_NAME = "qariana_prayer_records.json"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val _recordsFlow = MutableStateFlow<Map<String, DayPrayerRecord>>(emptyMap())
    val recordsFlow: StateFlow<Map<String, DayPrayerRecord>> = _recordsFlow.asStateFlow()

    private var isInitialized = false

    fun getTodayDateString(): String = dateFormat.format(Date())

    /**
     * অ্যাপ বা স্ক্রিন লোডের সময় লোকাল ডিস্ক থেকে ডেটা লোড
     */
    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true
        scope.launch {
            loadLocalRecords(context)
            syncPendingRecords(context)
        }
    }

    private fun loadLocalRecords(context: Context) {
        runCatching {
            val file = File(context.filesDir, FILE_NAME)
            if (!file.exists()) {
                _recordsFlow.value = emptyMap()
                return
            }
            val jsonStr = file.readText(Charsets.UTF_8)
            val jsonArr = JSONArray(jsonStr)
            val map = mutableMapOf<String, DayPrayerRecord>()
            for (i in 0 until jsonArr.length()) {
                val obj = jsonArr.getJSONObject(i)
                val date = obj.getString("date")
                val record = DayPrayerRecord(
                    date = date,
                    fajr = obj.optBoolean("fajr", false),
                    dhuhr = obj.optBoolean("dhuhr", false),
                    asr = obj.optBoolean("asr", false),
                    maghrib = obj.optBoolean("maghrib", false),
                    isha = obj.optBoolean("isha", false),
                    updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
                    isSynced = obj.optBoolean("isSynced", false)
                )
                map[date] = record
            }
            _recordsFlow.value = map
        }
    }

    private fun saveLocalRecords(context: Context, map: Map<String, DayPrayerRecord>) {
        runCatching {
            val file = File(context.filesDir, FILE_NAME)
            val jsonArr = JSONArray()
            map.values.forEach { record ->
                val obj = JSONObject().apply {
                    put("date", record.date)
                    put("fajr", record.fajr)
                    put("dhuhr", record.dhuhr)
                    put("asr", record.asr)
                    put("maghrib", record.maghrib)
                    put("isha", record.isha)
                    put("updatedAt", record.updatedAt)
                    put("isSynced", record.isSynced)
                }
                jsonArr.put(obj)
            }
            file.writeText(jsonArr.toString(), Charsets.UTF_8)
        }
    }

    /**
     * নির্দিষ্ট তারিখের রেকর্ড সংগ্রহ (না থাকলে শূন্য রেকর্ড প্রদান)
     */
    fun getRecordForDate(date: String): DayPrayerRecord {
        return _recordsFlow.value[date] ?: DayPrayerRecord(date = date)
    }

    fun getTodayRecord(): DayPrayerRecord {
        return getRecordForDate(getTodayDateString())
    }

    /**
     * ওয়াক্ত শুরু হয়েছে কিনা তা যাচাই (ভবিষ্যতের ওয়াক্ত আগাম টিক দেওয়া নিষিদ্ধ)
     */
    fun isPrayerWaqtStarted(
        prayerKey: String,
        targetDate: String,
        now: Calendar = Calendar.getInstance()
    ): Boolean {
        val todayStr = getTodayDateString()
        // অতীত দিনের ক্ষেত্রে ওয়াক্ত অতিক্রান্ত, তাই কাজা বা পড়া হিসেবে দাগানো বৈধ
        if (targetDate < todayStr) return true
        // ভবিষ্যৎ দিনের জন্য কোনো ওয়াক্ত এখনও আসেনি
        if (targetDate > todayStr) return false

        // আজকের দিনের জন্য ডাইনামিক সময় ক্যালকুলেশন
        val schedule = PrayerCalculator.calculate(now)
        val currentTotalMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        val fajr = schedule.getOrNull(0) ?: return true
        val dhuhr = schedule.getOrNull(2) ?: return true
        val asr = schedule.getOrNull(3) ?: return true
        val maghrib = schedule.getOrNull(4) ?: return true
        val isha = schedule.getOrNull(5) ?: return true

        return when (prayerKey.lowercase()) {
            "fajr" -> currentTotalMinutes >= (fajr.hour * 60 + fajr.minute)
            "dhuhr" -> currentTotalMinutes >= (dhuhr.hour * 60 + dhuhr.minute)
            "asr" -> currentTotalMinutes >= (asr.hour * 60 + asr.minute)
            "maghrib" -> currentTotalMinutes >= (maghrib.hour * 60 + maghrib.minute)
            "isha" -> currentTotalMinutes >= (isha.hour * 60 + isha.minute)
            else -> false
        }
    }

    /**
     * ওয়াক্তের শুরুর সময় ফরম্যাট করে পাওয়া (ইউজারকে টোস্টে দেখানোর জন্য)
     */
    fun getPrayerStartTimeString(prayerKey: String, isEnglish: Boolean, now: Calendar = Calendar.getInstance()): String {
        val schedule = PrayerCalculator.calculate(now)
        val pTime = when (prayerKey.lowercase()) {
            "fajr" -> schedule.getOrNull(0)
            "dhuhr" -> schedule.getOrNull(2)
            "asr" -> schedule.getOrNull(3)
            "maghrib" -> schedule.getOrNull(4)
            "isha" -> schedule.getOrNull(5)
            else -> null
        } ?: return ""

        val h = pTime.hour
        val m = pTime.minute
        val displayH = if (h == 0) 12 else if (h > 12) h - 12 else h
        val mStr = String.format("%02d", m)
        val period = if (h < 12) "AM" else "PM"
        val raw = "$displayH:$mStr $period"
        return if (isEnglish) raw else raw.toBn()
    }

    /**
     * কোনো ওয়াক্তের টিক টগল করা (শুরু হলে টগল হবে, নেট থাকলে সিঙ্ক হবে)
     * রিটার্ন: Pair(সফল_হয়েছে_কিনা, কারণ/টোস্ট_মেসেজ)
     */
    fun togglePrayer(
        context: Context,
        prayerKey: String,
        targetDate: String = getTodayDateString(),
        isEnglish: Boolean = false
    ): Pair<Boolean, String> {
        val now = Calendar.getInstance()
        if (!isPrayerWaqtStarted(prayerKey, targetDate, now)) {
            val startTime = getPrayerStartTimeString(prayerKey, isEnglish, now)
            val msg = if (isEnglish) {
                "${prayerKey.replaceFirstChar { it.uppercase() }} waqt has not started yet (starts at $startTime)"
            } else {
                val nameBn = when (prayerKey.lowercase()) {
                    "fajr" -> "ফজর"
                    "dhuhr" -> "যোহর"
                    "asr" -> "আসর"
                    "maghrib" -> "মাগরিব"
                    "isha" -> "এশা"
                    else -> prayerKey
                }
                "$nameBnের ওয়াক্ত এখনও শুরু হয়নি ($startTime থেকে শুরু হবে)"
            }
            return Pair(false, msg)
        }

        val currentMap = _recordsFlow.value.toMutableMap()
        val oldRecord = currentMap[targetDate] ?: DayPrayerRecord(date = targetDate)
        val newRecord = oldRecord.copyWithToggle(prayerKey)
        currentMap[targetDate] = newRecord
        _recordsFlow.value = currentMap

        // লোকাল ডিস্কে অবিলম্বে সেভ
        saveLocalRecords(context, currentMap)

        // ব্যাকগ্রাউন্ডে ক্লাউডে সিঙ্ক চেষ্টা
        scope.launch {
            syncRecordToCloud(context, newRecord)
        }

        return Pair(true, "")
    }

    /**
     * ক্লাউড সিঙ্ক: নেটওয়ার্ক থাকলে Firebase Firestore-এ সংরক্ষণ
     */
    private suspend fun syncRecordToCloud(context: Context, record: DayPrayerRecord) {
        if (!isNetworkAvailable(context)) return

        val app = context.applicationContext as? QarianaApp ?: return
        val firebase = app.container.firebase
        if (!firebase.enabled) return

        val db = firebase.db ?: return
        val uid = firebase.ensureSignedIn() ?: "guest_${app.container.deviceId}"

        runCatching {
            val docRef = db.collection("users")
                .document(uid)
                .collection("prayer_records")
                .document(record.date)

            val data = hashMapOf(
                "date" to record.date,
                "fajr" to record.fajr,
                "dhuhr" to record.dhuhr,
                "asr" to record.asr,
                "maghrib" to record.maghrib,
                "isha" to record.isha,
                "completedCount" to record.completedCount,
                "updatedAt" to record.updatedAt
            )

            docRef.set(data, SetOptions.merge())

            // সিঙ্ক সফল হলে লোকাল ফ্ল্যাগ আপডেট
            val currentMap = _recordsFlow.value.toMutableMap()
            currentMap[record.date]?.let {
                currentMap[record.date] = it.copy(isSynced = true)
                _recordsFlow.value = currentMap
                saveLocalRecords(context, currentMap)
            }
        }.onFailure {
            firebase.record(it)
        }
    }

    /**
     * জমে থাকা আন-সিঙ্কড ডেটা ক্লাউডে সিঙ্ক
     */
    fun syncPendingRecords(context: Context) {
        if (!isNetworkAvailable(context)) return
        scope.launch {
            val pending = _recordsFlow.value.values.filter { !it.isSynced }
            pending.forEach { record ->
                syncRecordToCloud(context, record)
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * পরিসংখ্যান গণনা (Day / Week / Month / Year)
     */
    fun getStats(period: TrackerPeriod): TrackerStats {
        val map = _recordsFlow.value
        val cal = Calendar.getInstance()
        val todayStr = getTodayDateString()

        when (period) {
            TrackerPeriod.DAY -> {
                val rec = map[todayStr] ?: DayPrayerRecord(date = todayStr)
                return TrackerStats(
                    totalRequired = 5,
                    totalCompleted = rec.completedCount,
                    completionPercent = rec.completionPercent,
                    allPrayedDaysCount = if (rec.isAllCompleted) 1 else 0,
                    streakDays = if (rec.completedCount > 0) 1 else 0
                )
            }
            TrackerPeriod.WEEK -> {
                var completed = 0
                var allPrayedDays = 0
                for (i in 0 until 7) {
                    val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
                    val dStr = dateFormat.format(c.time)
                    val r = map[dStr] ?: DayPrayerRecord(date = dStr)
                    completed += r.completedCount
                    if (r.isAllCompleted) allPrayedDays++
                }
                return TrackerStats(
                    totalRequired = 35,
                    totalCompleted = completed,
                    completionPercent = (completed.toFloat() / 35f).coerceIn(0f, 1f),
                    allPrayedDaysCount = allPrayedDays,
                    streakDays = calculateCurrentStreak(map)
                )
            }
            TrackerPeriod.MONTH -> {
                val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val currentDay = cal.get(Calendar.DAY_OF_MONTH)
                var completed = 0
                var allPrayedDays = 0
                val yearMonth = SimpleDateFormat("yyyy-MM", Locale.US).format(cal.time)

                for (day in 1..currentDay) {
                    val dStr = String.format("%s-%02d", yearMonth, day)
                    val r = map[dStr] ?: DayPrayerRecord(date = dStr)
                    completed += r.completedCount
                    if (r.isAllCompleted) allPrayedDays++
                }
                val totalReq = currentDay * 5
                return TrackerStats(
                    totalRequired = totalReq.coerceAtLeast(5),
                    totalCompleted = completed,
                    completionPercent = (completed.toFloat() / totalReq.toFloat()).coerceIn(0f, 1f),
                    allPrayedDaysCount = allPrayedDays,
                    streakDays = calculateCurrentStreak(map)
                )
            }
            TrackerPeriod.YEAR -> {
                val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
                val yearPrefix = cal.get(Calendar.YEAR).toString()
                var completed = 0
                var allPrayedDays = 0

                map.filter { it.key.startsWith(yearPrefix) }.values.forEach { r ->
                    completed += r.completedCount
                    if (r.isAllCompleted) allPrayedDays++
                }
                val totalReq = dayOfYear * 5
                return TrackerStats(
                    totalRequired = totalReq.coerceAtLeast(5),
                    totalCompleted = completed,
                    completionPercent = (completed.toFloat() / totalReq.toFloat()).coerceIn(0f, 1f),
                    allPrayedDaysCount = allPrayedDays,
                    streakDays = calculateCurrentStreak(map)
                )
            }
        }
    }

    private fun calculateCurrentStreak(map: Map<String, DayPrayerRecord>): Int {
        var streak = 0
        val cal = Calendar.getInstance()
        while (true) {
            val dStr = dateFormat.format(cal.time)
            val r = map[dStr]
            if (r != null && r.completedCount > 0) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }
}

