package com.karianaquran.reader.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("qariana_prefs")

enum class ReadingMode { CLASSIC, PAGE, AYAH, HAFEZI }

data class ReaderSettings(
    val themeId: String = "light",
    val customBg: Int? = null,
    val customArabic: Int? = null,
    val customBangla: Int? = null,
    val readingMode: ReadingMode = ReadingMode.CLASSIC,
    val showMeaning: Boolean = true,
    val cropMargins: Boolean = true,
    val fontScale: Float = 1f,
    val arabicScale: Float = 1f,
    val banglaScale: Float = 1f,
    val appLanguage: String = "bn",
    val arabicFont: String = "qariana",
    val banglaFont: String = "hindsiliguri",
    val showRamadanCountdown: Boolean = false,
    val azanSoundEnabled: Boolean = true,
    val keepScreenOn: Boolean = true,
    val fontBold: Boolean = true,
    val englishFont: String = "default",
    val hasSeenOnboarding: Boolean = false,
)

data class UserData(
    val lastPage: Int = 1,
    val bookmarks: Set<Int> = emptySet(),
    val wordMarks: Map<String, String> = emptyMap(),
    val unlockedBooks: Set<String> = emptySet(),
)

class SettingsStore(private val ctx: Context) {
    private object K {
        val theme = stringPreferencesKey("theme")
        val cBg = intPreferencesKey("c_bg")
        val cAr = intPreferencesKey("c_ar")
        val cBn = intPreferencesKey("c_bn")
        val mode = stringPreferencesKey("mode")
        val meaning = booleanPreferencesKey("meaning")
        val crop = booleanPreferencesKey("crop_margins")
        val scale = floatPreferencesKey("scale")
        val arScale = floatPreferencesKey("ar_scale")
        val bnScale = floatPreferencesKey("bn_scale")
        val lang = stringPreferencesKey("app_lang")
        val arFont = stringPreferencesKey("ar_font")
        val bnFont = stringPreferencesKey("bn_font")
        val showRamadan = booleanPreferencesKey("show_ramadan")
        val azanSound = booleanPreferencesKey("azan_sound")
        val keepScreenOn = booleanPreferencesKey("keep_screen_on")
        val fontBold = booleanPreferencesKey("font_bold")
        val enFont = stringPreferencesKey("en_font")
        val last = intPreferencesKey("last_page")
        val bookmarks = stringPreferencesKey("bookmarks")
        val marks = stringPreferencesKey("word_marks")
        val unlockedBooks = stringPreferencesKey("unlocked_books")
        val onboardingSeen = booleanPreferencesKey("onboarding_seen_v2")
        val v133ClassicFirstMigrated = booleanPreferencesKey("v133_classic_first_migrated")
    }

    val settings: Flow<ReaderSettings> = ctx.dataStore.data.map { p ->
        ReaderSettings(
            themeId = p[K.theme] ?: "light",
            customBg = p[K.cBg],
            customArabic = p[K.cAr],
            customBangla = p[K.cBn],
            readingMode = runCatching { ReadingMode.valueOf(p[K.mode] ?: "CLASSIC") }.getOrDefault(ReadingMode.CLASSIC),
            showMeaning = p[K.meaning] ?: true,
            cropMargins = p[K.crop] ?: true,
            fontScale = (p[K.scale] ?: 1f).coerceIn(0.8f, 1.5f),
            arabicScale = (p[K.arScale] ?: 1f).coerceIn(0.8f, 1.5f),
            banglaScale = (p[K.bnScale] ?: 1f).coerceIn(0.8f, 1.5f),
            appLanguage = p[K.lang] ?: "bn",
            arabicFont = p[K.arFont] ?: "qariana",
            banglaFont = p[K.bnFont] ?: "hindsiliguri",
            showRamadanCountdown = p[K.showRamadan] ?: false,
            azanSoundEnabled = p[K.azanSound] ?: true,
            keepScreenOn = p[K.keepScreenOn] ?: true,
            fontBold = p[K.fontBold] ?: true,
            englishFont = p[K.enFont] ?: "default",
            hasSeenOnboarding = p[K.onboardingSeen] ?: false,
        )
    }

    val userData: Flow<UserData> = ctx.dataStore.data.map { p ->
        UserData(
            lastPage = p[K.last] ?: 1,
            bookmarks = decodeBookmarks(p[K.bookmarks]),
            wordMarks = decodeMarks(p[K.marks]),
            unlockedBooks = decodeUnlockedBooks(p[K.unlockedBooks]),
        )
    }

    suspend fun update(block: (ReaderSettings) -> ReaderSettings) {
        ctx.dataStore.edit { p ->
            val cur = ReaderSettings(
                themeId = p[K.theme] ?: "light",
                customBg = p[K.cBg],
                customArabic = p[K.cAr],
                customBangla = p[K.cBn],
            readingMode = runCatching { ReadingMode.valueOf(p[K.mode] ?: "CLASSIC") }.getOrDefault(ReadingMode.CLASSIC),
                showMeaning = p[K.meaning] ?: true,
                cropMargins = p[K.crop] ?: true,
                fontScale = p[K.scale] ?: 1f,
                arabicScale = p[K.arScale] ?: 1f,
                banglaScale = p[K.bnScale] ?: 1f,
            appLanguage = p[K.lang] ?: "bn",
                arabicFont = p[K.arFont] ?: "qariana",
            banglaFont = p[K.bnFont] ?: "hindsiliguri",
                showRamadanCountdown = p[K.showRamadan] ?: false,
                azanSoundEnabled = p[K.azanSound] ?: true,
                keepScreenOn = p[K.keepScreenOn] ?: true,
            fontBold = p[K.fontBold] ?: true,
                englishFont = p[K.enFont] ?: "default",
            hasSeenOnboarding = p[K.onboardingSeen] ?: false,
            )
            val n = block(cur)
            p[K.theme] = n.themeId
            n.customBg?.let { p[K.cBg] = it } ?: p.remove(K.cBg)
            n.customArabic?.let { p[K.cAr] = it } ?: p.remove(K.cAr)
            n.customBangla?.let { p[K.cBn] = it } ?: p.remove(K.cBn)
            p[K.mode] = n.readingMode.name
            p[K.meaning] = n.showMeaning
            p[K.crop] = n.cropMargins
            p[K.scale] = n.fontScale
            p[K.arScale] = n.arabicScale
            p[K.bnScale] = n.banglaScale
            p[K.lang] = n.appLanguage
            p[K.arFont] = n.arabicFont
            p[K.bnFont] = n.banglaFont
            p[K.showRamadan] = n.showRamadanCountdown
            p[K.onboardingSeen] = n.hasSeenOnboarding
            p[K.azanSound] = n.azanSoundEnabled
            p[K.keepScreenOn] = n.keepScreenOn
            p[K.fontBold] = n.fontBold
            p[K.enFont] = n.englishFont
        }
    }

    suspend fun setLastPage(page: Int) = ctx.dataStore.edit { it[K.last] = page }

    /**
     * v1.3.3 আপগ্রেডে একবারের জন্য জনপ্রিয় সংস্করণ (CLASSIC) ও বাংলা অনুবাদ দৃশ্যমান ডিফল্ট নিশ্চিত করা
     */
    suspend fun applyV133DefaultsOnce() {
        ctx.dataStore.edit { p ->
            if (p[K.v133ClassicFirstMigrated] != true) {
                p[K.mode] = ReadingMode.CLASSIC.name
                p[K.meaning] = true
                p[K.v133ClassicFirstMigrated] = true
            }
        }
    }

    suspend fun setBookmarks(set: Set<Int>) = ctx.dataStore.edit { it[K.bookmarks] = encodeBookmarks(set) }

    suspend fun setWordMarks(map: Map<String, String>) = ctx.dataStore.edit { it[K.marks] = encodeMarks(map) }

    suspend fun applyRemote(lastPage: Int?, bookmarks: Set<Int>?, marks: Map<String, String>?) {
        ctx.dataStore.edit { p ->
            lastPage?.let { p[K.last] = it }
            bookmarks?.let { p[K.bookmarks] = encodeBookmarks(it) }
            marks?.let { p[K.marks] = encodeMarks(it) }
        }
    }

    suspend fun setUnlockedBooks(books: Set<String>) = ctx.dataStore.edit { it[K.unlockedBooks] = encodeUnlockedBooks(books) }
    suspend fun unlockBook(bookId: String) = ctx.dataStore.edit { p ->
        val current = decodeUnlockedBooks(p[K.unlockedBooks])
        p[K.unlockedBooks] = encodeUnlockedBooks(current + bookId)
    }

    suspend fun clearUserData() = ctx.dataStore.edit {
        it.remove(K.last); it.remove(K.bookmarks); it.remove(K.marks); it.remove(K.unlockedBooks)
    }

    companion object {
        fun encodeBookmarks(s: Set<Int>): String = JSONArray(s.sorted()).toString()
        fun decodeBookmarks(raw: String?): Set<Int> = runCatching {
            val a = JSONArray(raw ?: return emptySet())
            (0 until a.length()).map { a.getInt(it) }.toSet()
        }.getOrDefault(emptySet())

        fun encodeMarks(m: Map<String, String>): String = JSONObject(m).toString()
        fun decodeMarks(raw: String?): Map<String, String> = runCatching {
            val o = JSONObject(raw ?: return emptyMap())
            o.keys().asSequence().associateWith { o.getString(it) }
        }.getOrDefault(emptyMap())

        fun encodeUnlockedBooks(s: Set<String>): String = JSONArray(s.toList()).toString()
        fun decodeUnlockedBooks(raw: String?): Set<String> = runCatching {
            val a = JSONArray(raw ?: return emptySet())
            (0 until a.length()).map { a.getString(it) }.toSet()
        }.getOrDefault(emptySet())
    }
}
