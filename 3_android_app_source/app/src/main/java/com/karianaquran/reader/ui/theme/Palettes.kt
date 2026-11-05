package com.karianaquran.reader.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.karianaquran.reader.data.ReaderSettings

/** রিডারের রঙের প্যালেট (৬টি প্রিসেট + কাস্টম ওভাররাইড) */
data class ReaderPalette(
    val id: String,
    val nameBn: String,
    val bg: Color,
    val arabic: Color,
    val bangla: Color,
    val accent: Color,
    val isDark: Boolean,
) {
    /** টপ/বটম বারের রঙ — ব্যাকগ্রাউন্ডের সাথে সামান্য মিশ্রণ */
    val bar: Color get() = lerp(bg, arabic, 0.09f)
    val divider: Color get() = arabic.copy(alpha = 0.18f)
}

object Palettes {
    val Dark = ReaderPalette("dark", "ডার্ক", Color(0xFF0F172A), Color(0xFFF8FAFC), Color(0xFF94A3B8), Color(0xFFF59E0B), true)
    val Light = ReaderPalette("light", "লাইট", Color(0xFFFFFFFF), Color(0xFF0F172A), Color(0xFF334155), Color(0xFF064E3B), false)
    val Sepia = ReaderPalette("sepia", "সেপিয়া", Color(0xFFF4ECD8), Color(0xFF2D1E12), Color(0xFF5B4636), Color(0xFF9A6B2F), false)
    val Emerald = ReaderPalette("emerald", "এমেরাল্ড", Color(0xFF064E3B), Color(0xFFFEF3C7), Color(0xFFA7F3D0), Color(0xFFD4AF37), true)
    val Midnight = ReaderPalette("midnight", "মিডনাইট", Color(0xFF00050D), Color(0xFFF1F5F9), Color(0xFF94A3B8), Color(0xFF38BDF8), true)
    val Cream = ReaderPalette("cream", "ক্রিম", Color(0xFFFFFDF5), Color(0xFF1F2937), Color(0xFF4B5563), Color(0xFF064E3B), false)

    val all = listOf(Dark, Light, Sepia, Emerald, Midnight, Cream)
    val presets: List<ReaderPalette> get() = all

    fun byId(id: String): ReaderPalette = all.firstOrNull { it.id == id } ?: Light

    fun next(id: String): ReaderPalette {
        val i = all.indexOfFirst { it.id == id }.coerceAtLeast(0)
        return all[(i + 1) % all.size]
    }

    fun resolve(id: String, customBg: Int?, customArabic: Int?, customBangla: Int?): ReaderPalette {
        return resolve(ReaderSettings(themeId = id, customBg = customBg, customArabic = customArabic, customBangla = customBangla))
    }

    /** প্রিসেটের উপর ইউজারের কাস্টম রং প্রয়োগ */
    fun resolve(s: ReaderSettings): ReaderPalette {
        val base = byId(s.themeId)
        val bg = s.customBg?.let { Color(it) } ?: base.bg
        val ar = s.customArabic?.let { Color(it) } ?: base.arabic
        val bn = s.customBangla?.let { Color(it) } ?: base.bangla
        // কাস্টম ব্যাকগ্রাউন্ড হলে isDark ল্যুমিন্যান্স থেকে নির্ধারণ
        val dark = if (s.customBg != null) bg.luminanceApprox() < 0.5f else base.isDark
        return base.copy(bg = bg, arabic = ar, bangla = bn, isDark = dark)
    }
}

typealias ThemePalette = ReaderPalette

private fun Color.luminanceApprox(): Float = 0.2126f * red + 0.7152f * green + 0.0722f * blue
