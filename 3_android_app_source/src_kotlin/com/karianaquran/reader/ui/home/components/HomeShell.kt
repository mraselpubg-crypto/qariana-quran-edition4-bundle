package com.karianaquran.reader.ui.home.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.R
import com.karianaquran.reader.data.model.Publication
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.Screen
import com.karianaquran.reader.ui.components.RealBookCover
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.home.AccentMint
import com.karianaquran.reader.ui.home.MutedText
import com.karianaquran.reader.ui.home.PrayerGold
import com.karianaquran.reader.ui.home.PrimaryDarkEmerald
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * হোম স্ক্রিনের নেভিগেশন শেল কম্পোনেন্ট:
 * - HomeNavigationDrawer + DrawerMenuItem
 * - DarUlMujtabaBottomBar + ModernBottomBarTab
 * - LanguageSelectDialog + AboutUsDialog
 * HomeScreen.kt থেকে ভাগ করা হয়েছে — behavior অপরিবর্তিত।
 */

// -------------------------------------------------------------------------------------------------
// ৭. নেভিগেশন ড্রয়ার (HomeNavigationDrawer)
// -------------------------------------------------------------------------------------------------
@Composable
internal fun HomeNavigationDrawer(
    vm: AppViewModel,
    isDark: Boolean,
    onClose: () -> Unit,
    onOpenQuranEdition: () -> Unit,
    onOpenQibla: () -> Unit,
    onOpenNames: () -> Unit,
    onOpenPillars: () -> Unit,
    onOpenRamadan: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isEnglish = readerSettings.appLanguage == "en"

    ModalDrawerSheet(
        modifier = Modifier.width(310.dp),
        drawerContainerColor = if (isDark) Color(0xFF0E1A14) else Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0B5D45), Color(0xFF064E3B))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, PrayerGold)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.kariana_logo),
                                contentDescription = "কারিয়ানা লোগো",
                                modifier = Modifier.size(46.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isEnglish) "Qariana Quran" else "ক্বারীয়ানা কুরআন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Qariana Quran & Muslim Lifestyle",
                        fontSize = 11.sp,
                        color = AccentMint
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            DrawerMenuItem(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                title = if (isEnglish) "The Holy Quran" else "আল-কুরআনুল কারীম",
                subtitle = if (isEnglish) "30 Paras • 3 Reading Editions" else "৩০ পারা • ৩টি পাঠ সংস্করণ",
                onClick = { onClose(); onOpenQuranEdition() }
            )
            DrawerMenuItem(
                icon = Icons.Default.AccessTime,
                title = if (isEnglish) "Prayer Timings" else "নামাজের সময়সূচি",
                subtitle = if (isEnglish) "5 Daily Waqt & Prohibited Times" else "৫ ওয়াক্ত ও নিষিদ্ধ সময়",
                onClick = { onClose(); vm.navigate(Screen.Namaz) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Explore,
                title = if (isEnglish) "Qibla Compass" else "কিবলা কম্পাস",
                subtitle = if (isEnglish) "Accurate Kaaba Direction" else "সঠিক কাবার দিকনির্দেশনা",
                onClick = onOpenQibla
            )
            DrawerMenuItem(
                icon = Icons.Default.RadioButtonChecked,
                title = if (isEnglish) "Digital Tasbih" else "ডিজিটাল তাসবীহ",
                subtitle = if (isEnglish) "Daily Zikr & Counter" else "দৈনিক যিকির ও গণনা",
                onClick = { onClose(); vm.navigate(Screen.Tasbih) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Mosque,
                title = if (isEnglish) "Duas & Supplications" else "দোয়া ও মোনাজাত",
                subtitle = if (isEnglish) "Daily Masnoon Duas" else "প্রতিদিনের মাসনূন দোয়া",
                onClick = { onClose(); vm.navigate(Screen.Dua) }
            )
            DrawerMenuItem(
                icon = Icons.Default.AutoStories,
                title = if (isEnglish) "Hadith Shareef" else "হাদিস শরীফ",
                subtitle = if (isEnglish) "Sahih Hadith Collection" else "সহীহ হাদিস সংকলন",
                onClick = { onClose(); vm.navigate(Screen.Hadith) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Star,
                title = if (isEnglish) "99 Names of Allah" else "আল্লাহর ৯৯ নাম",
                subtitle = if (isEnglish) "Asmaul Husna & Meaning" else "আসমাউল হুসনা ও অর্থ",
                onClick = onOpenNames
            )
            DrawerMenuItem(
                icon = Icons.Default.CheckCircle,
                title = if (isEnglish) "Pillars of Islam & Kalimah" else "ইসলামের স্তম্ভ ও কালিমা",
                subtitle = if (isEnglish) "Faith & Core Teachings" else "ঈমান ও মৌলিক শিক্ষা",
                onClick = onOpenPillars
            )
            DrawerMenuItem(
                icon = Icons.Default.Schedule,
                title = if (isEnglish) "Ramadan & Iftar Schedule" else "রমজান ও সেহরি-ইফতার",
                subtitle = if (isEnglish) "Complete Ramadan Calendar" else "রমজানের পূর্ণ ক্যালেন্ডার",
                onClick = onOpenRamadan
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = if (isDark) Color(0xFF1F2F26) else Color(0xFFF1F5F9)
            )

            DrawerMenuItem(
                icon = Icons.Default.WorkspacePremium,
                title = if (isEnglish) "Unlock Premium" else "প্রিমিয়াম আনলক করুন",
                subtitle = if (isEnglish) "Full Offline License" else "সম্পূর্ণ অফলাইন লাইসেন্স",
                badge = "VIP",
                onClick = { onClose(); vm.navigate(Screen.Pay) }
            )
            DrawerMenuItem(
                icon = Icons.Default.Language,
                title = if (isEnglish) "App Language" else "ভাষা নির্বাচন (Language)",
                subtitle = if (isEnglish) "English / বাংলা" else "বাংলা / English",
                onClick = onOpenLanguage
            )
            DrawerMenuItem(
                icon = Icons.Default.Settings,
                title = if (isEnglish) "Settings" else "সেটিংস",
                subtitle = if (isEnglish) "Fonts, Theme & Display" else "ফন্ট, থিম ও ডিসপ্লে",
                onClick = onOpenSettings
            )
            DrawerMenuItem(
                icon = Icons.Default.Info,
                title = if (isEnglish) "About Us" else "আমাদের সম্পর্কে",
                subtitle = if (isEnglish) "Qariana Publications" else "ক্বারীয়ানা পাবলিকেশনস",
                onClick = onOpenAbout
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryDarkEmerald,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    fontFamily = fonts.kalpurush,
                    fontSize = 10.5.sp,
                    color = MutedText
                )
            }
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D))
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// ১৪. ভাষা নির্বাচন ডায়ালগ (LanguageSelectDialog)
// -------------------------------------------------------------------------------------------------
@Composable
internal fun LanguageSelectDialog(
    currentLang: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val fonts = LocalAppFonts.current
    val isEnglish = currentLang == "en"
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEnglish) "Select App Language" else "অ্যাপের ভাষা নির্বাচন করুন",
                fontFamily = fonts.kalpurush,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkEmerald
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Pair("bn", "বাংলা (Bengali)"),
                    Pair("en", "English")
                ).forEach { (code, name) ->
                    val isSelected = currentLang == code
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFFE8F5E9) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF0F6B56) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(code) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF0F6B56) else Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0F6B56) else Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isEnglish) "OK" else "ঠিক আছে",
                    fontFamily = fonts.kalpurush,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkEmerald
                )
            }
        }
    )
}

// -------------------------------------------------------------------------------------------------
// ১৫. আমাদের সম্পর্কে ডায়ালগ (AboutUsDialog)
// -------------------------------------------------------------------------------------------------
@Composable
internal fun AboutUsDialog(
    isDark: Boolean,
    onDismiss: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.aboutus),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ক্বারীয়ানা কুরআন ও প্রকাশনা",
                    fontFamily = fonts.kalpurush,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "ক্বারীয়ানা কুরআন—উচ্চারণ, শব্দার্থ ও সহজ তাজবীদ সংবলিত পূর্ণাঙ্গ ৩০ পারা কুরআন শরীফ এবং ইসলামিক জীবনযাত্রার বিশ্বস্ত ডিজিটাল অ্যাপ।",
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "সংস্করণ: ৩.৩৩ (আন্তর্জাতিক স্ট্যান্ডার্ড)",
                    fontFamily = fonts.kalpurush,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryDarkEmerald
                )
                Text(
                    text = "কপিরাইট © ২০২৬ ক্বারীয়ানা পাবলিকেশনস। সর্বস্বত্ব সংরক্ষিত।",
                    fontFamily = fonts.kalpurush,
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "বন্ধ করুন", fontFamily = fonts.kalpurush, color = PrimaryDarkEmerald)
            }
        }
    )
}

// =========================================================================
// দার-উল-মুজতাবা মেটেরিয়াল বটম নেভিগেশন বার (অন্যান্য স্ক্রিনের জন্য রক্ষিত)
// =========================================================================
@Composable
fun DarUlMujtabaBottomBar(
    activeTab: Int,
    isDark: Boolean,
    onTabSelect: (Int) -> Unit,
    isEnglish: Boolean = false,
) {
    val fonts = LocalAppFonts.current
    val activeColor = Color(0xFF0F6B56)
    val inactiveColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val barBg = if (isDark) Color(0xFF13221A) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = barBg,
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDark) Color(0xFF1F352A) else Color(0xFFE2E8F0)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModernBottomBarTab(
                    title = if (isEnglish) "Home" else "হোম",
                    icon = Icons.Default.Home,
                    isActive = activeTab == 0,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onTabSelect(0) }
                )
                ModernBottomBarTab(
                    title = if (isEnglish) "Library" else "বইসমূহ",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    isActive = activeTab == 1,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onTabSelect(1) }
                )
                ModernBottomBarTab(
                    title = if (isEnglish) "Al-Quran" else "আল-কুরআন",
                    icon = Icons.Default.AutoStories,
                    isActive = activeTab == 2,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onTabSelect(2) }
                )
                ModernBottomBarTab(
                    title = if (isEnglish) "Profile" else "প্রোফাইল",
                    icon = Icons.Default.Person,
                    isActive = activeTab == 3,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onTabSelect(3) }
                )
            }
        }
    }
}

@Composable
private fun ModernBottomBarTab(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit,
) {
    val fonts = LocalAppFonts.current

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isActive) activeColor else Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) Color.White else inactiveColor,
                modifier = Modifier.size(20.dp)
            )

            if (isActive) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = title,
                    fontFamily = fonts.kalpurush,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/** হোম স্ক্রিনের হ্যাপটিক ফিডব্যাক (split-এ factory ফাংশন)। */
internal fun triggerHaptic(context: Context) {
    try {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(35)
        }
    } catch (_: Exception) {}
}

// -------------------------------------------------------------------------------------------------
// ৯. শীঘ্রই আসছে বিস্তারিত মডাল ডায়ালগ (UpcomingBookDetailDialog)
// -------------------------------------------------------------------------------------------------
@Composable
internal fun UpcomingBookDetailDialog(
    book: Publication,
    isDark: Boolean,
    onDismiss: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    val context = LocalContext.current

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = if (isDark) Color(0xFF13221A) else Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4AF37)),
            shadowElevation = 10.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RealBookCover(
                    publication = book,
                    height = 140.dp
                )

                Spacer(Modifier.height(12.dp))

                if (book.discountPercent.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE11D48),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = book.discountPercent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = book.titleBn,
                    fontFamily = fonts.kalpurush,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                if (book.titleAr.isNotEmpty()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = book.titleAr,
                        fontFamily = fonts.arabic,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4AF37),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (book.paperType.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDark) Color(0xFF1E3A2F) else Color(0xFFE8F5E9)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F6B56),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = book.paperType,
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 11.sp,
                                    color = if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F6B56),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    if (book.totalPages > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDark) Color(0xFF1E3A2F) else Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "${book.totalPages.toBn()} পৃষ্ঠা",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F6B56),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (book.originalPriceBn.isNotEmpty()) {
                        Text(
                            text = book.originalPriceBn,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    }
                    Text(
                        text = book.priceBn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color(0xFFFDE68A) else Color(0xFF0F6B56)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = book.subtitleBn,
                    fontFamily = fonts.kalpurush,
                    fontSize = 12.sp,
                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(book.orderUrl))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("ওয়েবসাইট থেকে অর্ডার করুন", fontFamily = fonts.kalpurush, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(38.dp)
                    ) {
                        Text("বন্ধ করুন", fontSize = 12.5.sp, color = if (isDark) Color.White else Color(0xFF334155))
                    }
                }
            }
        }
    }
}
