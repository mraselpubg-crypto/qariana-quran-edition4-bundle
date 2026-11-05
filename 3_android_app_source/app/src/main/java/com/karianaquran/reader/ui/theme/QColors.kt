package com.karianaquran.reader.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * কেন্দ্রীয় ব্র্যান্ড টোকেন — পুরো অ্যাপে একমাত্র রঙের উৎস।
 *
 * ⚠️ নিয়ম: নতুন কোনো স্ক্রিনে আর কখনো হার্ডকোড `Color(0xFF...)` লেখা যাবে না।
 * এখান থেকে টোকেন নিতে হবে (বা থিম-সচেতন `LocalQarianaColors.current`)।
 *
 * backward-compat: পুরনো `Brand` অবজেক্ট Deprecated-alias হিসেবে থেকে যাবে
 * (AppTheme.kt), ধীরে ধীরে migrate করা হবে।
 */
object QColors {
    // ---- Brand Primary (নির্বাচিত একক সবুজ — হোমে ব্যবহৃত 0xFF0F6F52) ----
    val Primary = Color(0xFF0F6F52)
    val PrimaryDeep = Color(0xFF064E3B)
    val PrimarySoft = Color(0xFF10B981)
    val OnPrimary = Color(0xFFFFFFFF)

    // ---- Surface / Background ----
    val BgLight = Color(0xFFF6FFF9)
    val SurfaceLight = Color(0xFFFFFFFF)
    val BgDark = Color(0xFF0B131E)
    val SurfaceDark = Color(0xFF131F2A)

    // ---- Accent ----
    val Gold = Color(0xFFD4AF37)
    val GoldSoft = Color(0xFFFDE68A)
    val Mint = Color(0xFFA7F3D0)

    // ---- Text ----
    val TextPrimaryLight = Color(0xFF111827)
    val TextSecondaryLight = Color(0xFF6B7280)
    val TextPrimaryDark = Color(0xFFF1F5F9)
    val TextSecondaryDark = Color(0xFF94A3B8)

    // ---- Border ----
    val BorderLight = Color(0xFFE2E8F0)
    val BorderDark = Color(0xFF1E2E3D)

    // ---- Semantic (status) ----
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFD97706)
    val Error = Color(0xFFEF4444)
    val Info = Color(0xFF0284C7)
}

/**
 * থিম-সচেতন সেমান্টিক রঙের সেট।
 * কম্পোনেন্ট এই অবজেক্ট পাবে `LocalQarianaColors.current` থেকে,
 * ফলে লাইট/ডার্ক আলাদা করে `isDark` বুলিয়ান সব জায়গায় থ্রেড করতে হবে না।
 */
data class QarianaColors(
    val primary: Color,
    val primaryDeep: Color,
    val primarySoft: Color,
    val onPrimary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val gold: Color,
    val goldSoft: Color,
    val mint: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val isDark: Boolean,
)

val QarianaLightColors = QarianaColors(
    primary = QColors.Primary,
    primaryDeep = QColors.PrimaryDeep,
    primarySoft = QColors.PrimarySoft,
    onPrimary = QColors.OnPrimary,
    background = QColors.BgLight,
    surface = QColors.SurfaceLight,
    surfaceVariant = Color(0xFFF3F4F6),
    gold = QColors.Gold,
    goldSoft = QColors.GoldSoft,
    mint = QColors.Mint,
    textPrimary = QColors.TextPrimaryLight,
    textSecondary = QColors.TextSecondaryLight,
    border = QColors.BorderLight,
    success = QColors.Success,
    warning = QColors.Warning,
    error = QColors.Error,
    isDark = false,
)

val QarianaDarkColors = QarianaColors(
    primary = QColors.PrimarySoft,
    primaryDeep = QColors.Primary,
    primarySoft = QColors.PrimarySoft,
    onPrimary = QColors.BgDark,
    background = QColors.BgDark,
    surface = QColors.SurfaceDark,
    surfaceVariant = Color(0xFF1B2836),
    gold = QColors.Gold,
    goldSoft = QColors.GoldSoft,
    mint = QColors.Mint,
    textPrimary = QColors.TextPrimaryDark,
    textSecondary = QColors.TextSecondaryDark,
    border = QColors.BorderDark,
    success = QColors.Success,
    warning = QColors.Warning,
    error = Color(0xFFFF8A80),
    isDark = true,
)

/** সেমান্টিক রঙের CompositionLocal — স্ক্রিন-লেভেলে provide করা হবে। */
val LocalQarianaColors = staticCompositionLocalOf { QarianaLightColors }
