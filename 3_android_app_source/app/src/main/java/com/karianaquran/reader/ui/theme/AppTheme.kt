package com.karianaquran.reader.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

/** আরবি = Qariana ফন্ট; বাংলা ও সংখ্যা (০-৯) = কালপুরুষ/কাস্টম; ইংরেজি = ডিফল্ট/কাস্টম */
data class AppFonts(
    val arabic: FontFamily, 
    val bangla: FontFamily,
    val english: FontFamily = FontFamily.Default,
    val kalpurushFont: FontFamily? = null
) {
    val qariana: FontFamily get() = arabic
    val kalpurush: FontFamily get() = kalpurushFont ?: bangla
}

val LocalAppFonts = staticCompositionLocalOf { AppFonts(FontFamily.Default, FontFamily.Default) }

/**
 * নগদ ইসলামিক (Nagad Islamic) ও ক্বারীয়ানা কুরআন ব্র্যান্ড কালার প্যালেট
 */
object Brand {
    // নগদ ইসলামিক সিগনেচার গ্রীন ও মিন্ট প্যালেট
    val NagadGreen = Color(0xFF0B7A46)         // মূল ইসলামিক সবুজ বাটন ও অ্যাকসেন্ট
    val NagadGreenDark = Color(0xFF06542F)     // ডিপ সবুজ
    val MintBackground = Color(0xFFF1F9F3)     // সফট মিন্ট ব্যাকগ্রাউন্ড
    val MintSurface = Color(0xFFE8F5E9)        // সফট মিন্ট আইকন কন্টেইনার
    val MintBorder = Color(0xFFD1E7DD)         // সূক্ষ্ম মিন্ট বর্ডার
    
    // টেক্সট ও সারফেস
    val CharcoalText = Color(0xFF111827)       // স্পষ্ট ডার্ক চারকোল শিরোনাম
    val SlateSubtext = Color(0xFF4B5563)       // সাবটেক্সট
    val CardBorder = Color(0xFFE5E7EB)         // কার্ড বর্ডার
    val IslamicGold = Color(0xFFD4AF37)        // গোল্ডেন ক্যালিগ্রাফি ও ব্যাজ
    val PistachioRow = Color(0xFFD0E8C4)       // কুরআনের পস্তা সবুজ আয়াত ব্যান্ড
    
    // ওলেড ডার্ক থিম
    val DarkBackground = Color(0xFF080F0B)     // ডিপ ইসলামিক ডার্ক
    val DarkSurface = Color(0xFF0E1A14)        // ডার্ক কার্ড সারফেস
    val DarkSurfaceElevated = Color(0xFF14241C)
    val DarkCardBorder = Color(0xFF1D3527)
    val DarkText = Color(0xFFF3F4F6)
    val DarkSubtext = Color(0xFF9CA3AF)
    
    // পূর্ববর্তী কম্প্যাটিবিলিটি
    val Emerald = NagadGreen
    val EmeraldDark = NagadGreenDark
    val EmeraldLight = Color(0xFF10B981)
    val Gold = IslamicGold
    val Obsidian = DarkBackground
    val ObsidianSurface = DarkSurface
    val Parchment = Color(0xFFFDFBF7)

    // ---- নতুন অ্যাবস্ট্রাক্ট অ্যালিয়াস (সিজন-নামিং মুক্ত) ----
    // নতুন কোডে Brand.NagadGreen নয়, বরং Brand.Primary ব্যবহার করুন।
    val Primary = QColors.Primary
    val PrimaryDeep = QColors.PrimaryDeep
    val PrimarySoft = QColors.PrimarySoft
}

private fun typographyWith(f: FontFamily): Typography {
    val d = Typography()
    return d.copy(
        displayLarge = d.displayLarge.copy(fontFamily = f),
        displayMedium = d.displayMedium.copy(fontFamily = f),
        displaySmall = d.displaySmall.copy(fontFamily = f),
        headlineLarge = d.headlineLarge.copy(fontFamily = f),
        headlineMedium = d.headlineMedium.copy(fontFamily = f),
        headlineSmall = d.headlineSmall.copy(fontFamily = f),
        titleLarge = d.titleLarge.copy(fontFamily = f),
        titleMedium = d.titleMedium.copy(fontFamily = f),
        titleSmall = d.titleSmall.copy(fontFamily = f),
        bodyLarge = d.bodyLarge.copy(fontFamily = f),
        bodyMedium = d.bodyMedium.copy(fontFamily = f),
        bodySmall = d.bodySmall.copy(fontFamily = f),
        labelLarge = d.labelLarge.copy(fontFamily = f),
        labelMedium = d.labelMedium.copy(fontFamily = f),
        labelSmall = d.labelSmall.copy(fontFamily = f),
    )
}

// নগদ ইসলামিক লাইট থিম (ডিফল্ট লাইট মোড)
private val LightColorScheme = lightColorScheme(
    primary = Brand.NagadGreen,
    onPrimary = Color.White,
    primaryContainer = Brand.MintSurface,
    onPrimaryContainer = Brand.NagadGreenDark,
    secondary = Brand.NagadGreen,
    onSecondary = Color.White,
    background = Color(0xFFF9FAF8),
    onBackground = Brand.CharcoalText,
    surface = Color.White,
    onSurface = Brand.CharcoalText,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Brand.SlateSubtext,
    outline = Brand.CardBorder,
    error = Color(0xFFD32F2F),
)

// নগদ ইসলামিক ওলেড ডার্ক থিম
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color(0xFF042F1A),
    primaryContainer = Brand.DarkSurfaceElevated,
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = Brand.NagadGreen,
    onSecondary = Color.White,
    background = Brand.DarkBackground,
    onBackground = Brand.DarkText,
    surface = Brand.DarkSurface,
    onSurface = Brand.DarkText,
    surfaceVariant = Brand.DarkSurfaceElevated,
    onSurfaceVariant = Brand.DarkSubtext,
    outline = Brand.DarkCardBorder,
    error = Color(0xFFFF8A80),
)

@Composable
fun QarianaTheme(
    fonts: AppFonts, 
    darkTheme: Boolean = false, 
    isEnglish: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val primaryFont = if (isEnglish) fonts.english else fonts.bangla
    val currentStrings = if (isEnglish) com.karianaquran.reader.ui.l10n.EnStrings else com.karianaquran.reader.ui.l10n.BnStrings
    val qarianaColors = if (darkTheme) QarianaDarkColors else QarianaLightColors
    CompositionLocalProvider(
        LocalAppFonts provides fonts,
        LocalQarianaColors provides qarianaColors,
        com.karianaquran.reader.ui.l10n.LocalAppStrings provides currentStrings
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typographyWith(primaryFont),
            content = content,
        )
    }
}
