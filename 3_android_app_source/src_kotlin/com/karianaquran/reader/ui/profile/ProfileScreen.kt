package com.karianaquran.reader.ui.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.data.prayer.PrayerTrackerRepository
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.Screen
import com.karianaquran.reader.ui.components.ModernDialogType
import com.karianaquran.reader.ui.components.ModernNoticeDialog
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.components.toLang
import com.karianaquran.reader.ui.home.components.DarUlMujtabaBottomBar
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * ইউজার প্রোফাইল হেল্পার
 */
object UserProfileHelper {
    private const val PREFS = "qariana_user_profile"
    private const val KEY_NAME = "user_full_name"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_GENDER = "user_gender"
    private const val KEY_AVATAR_ID = "user_avatar_id"
    private const val KEY_AVATAR_EMOJI = "user_avatar_emoji"

    fun getProfileName(context: Context, defaultMobile: String?): String {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val name = sp.getString(KEY_NAME, null)
        return if (!name.isNullOrBlank()) name else (defaultMobile ?: "ক্বারীয়ানা পাঠক")
    }

    fun saveProfile(context: Context, name: String, email: String) {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        sp.edit().putString(KEY_NAME, name).putString(KEY_EMAIL, email).apply()
    }

    fun getAvatarIcon(avatarId: Int): ImageVector {
        return when (avatarId % 5) {
            1 -> Icons.Default.Person
            2 -> Icons.AutoMirrored.Filled.MenuBook
            3 -> Icons.Default.Star
            4 -> Icons.Default.Verified
            else -> Icons.Default.Security
        }
    }

    fun getSavedAvatarId(context: Context): Int {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return sp.getInt(KEY_AVATAR_ID, 1)
    }

    fun getSavedGender(context: Context): String {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return sp.getString(KEY_GENDER, "male") ?: "male"
    }

    fun getSavedAvatarEmoji(context: Context): String {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return sp.getString(KEY_AVATAR_EMOJI, "👤") ?: "👤"
    }

    fun saveAvatar(context: Context, gender: String, avatarId: Int, emoji: String) {
        val sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        sp.edit()
            .putString(KEY_GENDER, gender)
            .putInt(KEY_AVATAR_ID, avatarId)
            .putString(KEY_AVATAR_EMOJI, emoji)
            .apply()
    }
}

/**
 * MindBloom আন্তর্জাতিক ডিজাইনের আদলে ক্বারীয়ানা কুরআন সেটিংস ও প্রোফাইল স্ক্রিন
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val isEnglish = readerSettings.appLanguage == "en"

    val session = vm.session
    val isLoggedIn = session != null
    val userData by vm.userData.collectAsState()
    val isCheckingUpdate = vm.isCheckingUpdate
    var showLicenseSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        PrayerTrackerRepository.init(context)
    }
    val todayRecord = remember(recordsMapState(context)) {
        PrayerTrackerRepository.getTodayRecord()
    }

    // ডায়ালগ স্টেটস
    var showBanglaFontDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showHelplineDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val profileName = remember(session) {
        UserProfileHelper.getProfileName(context, session?.mobile)
    }

    val bgPage = if (isDark) Color(0xFF091410) else Color(0xFFFBFBFD)
    val cardBg = if (isDark) Color(0xFF101D17) else Color.White
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.09f) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgPage,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        bottomBar = {
            DarUlMujtabaBottomBar(
                activeTab = 3,
                isDark = isDark,
                onTabSelect = { tabIdx ->
                    when (tabIdx) {
                        0 -> vm.navigate(Screen.Home)
                        1 -> vm.openLibrary()
                        2 -> vm.openIndex()
                        3 -> { /* বর্তমান প্রোফাইল স্ক্রিন */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // =========================================================================
            // ১. শীর্ষ হেডার: ব্যাক বাটন, স্ক্রিন টাইটেল এবং নোটিফিকেশন বাটন
            // =========================================================================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(onClick = { vm.navigate(Screen.Home) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = if (isEnglish) "Settings" else "প্রোফাইল ও সেটিংস",
                        fontFamily = fonts.kalpurush,
                        fontSize = 17.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    Surface(
                        shape = CircleShape,
                        color = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(onClick = { vm.navigate(Screen.Notifications) }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = textPrimary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // ২. ইউজার প্রোফাইল হিরো সেকশন (অবতার + এডিট পেন্সিল ব্যাজ + নাম + আইডি)
            // =========================================================================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // অবতারের ওপর এডিট ব্যাজ
                    Box(contentAlignment = Alignment.BottomEnd) {
                        val avatarId = remember(session) { UserProfileHelper.getSavedAvatarId(context) }
                        Surface(
                            modifier = Modifier.size(86.dp),
                            shape = CircleShape,
                            color = if (isDark) Color(0xFF162B21) else Color(0xFFE2F3EC),
                            border = BorderStroke(2.5.dp, Color(0xFF0F6B56))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isLoggedIn) UserProfileHelper.getAvatarIcon(avatarId) else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF0F6B56),
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }

                        // ছোট এডিট পেন্সিল ব্যাজ
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF091712),
                            border = BorderStroke(2.dp, Color.White),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { vm.navigate(Screen.EditProfile) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = profileName,
                        fontFamily = fonts.kalpurush,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = if (session != null) {
                            if (isEnglish) "ID: ${session.licenseKey}" else "আইডি: ${session.licenseKey}"
                        } else {
                            if (isEnglish) "Guest Reader • Tap to Login" else "অতিথি পাঠক • লগইন করুন"
                        },
                        fontFamily = fonts.kalpurush,
                        fontSize = 12.5.sp,
                        color = textSecondary
                    )

                    Spacer(Modifier.height(8.dp))

                    // মেম্বারশিপ স্ট্যাটাস ব্যাজ
                    val isVip = vm.isVipPhone(session?.mobile ?: "")
                    val isUnlocked = vm.isUnlocked
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isVip) Color(0xFFFEF3C7) else (if (isUnlocked) Color(0xFFD1FAE5) else Color(0xFFF1F5F9)),
                        border = BorderStroke(1.dp, if (isVip) Color(0xFFF59E0B) else (if (isUnlocked) Color(0xFF10B981) else Color(0xFFCBD5E1)))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isVip) Icons.Default.Star else Icons.Default.Verified,
                                contentDescription = null,
                                tint = if (isVip) Color(0xFFB45309) else (if (isUnlocked) Color(0xFF059669) else Color(0xFF64748B)),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isVip) {
                                    if (isEnglish) "Lifetime VIP Diamond Member" else "আজীবন ভিআইপি ডায়মন্ড সদস্য"
                                } else if (isUnlocked) {
                                    if (isEnglish) "Premium Licensed Member" else "ক্বারীয়ানা লাইসেন্সপ্রাপ্ত সদস্য"
                                } else {
                                    if (isEnglish) "Free Preview Member (10 Pages)" else "ফ্রি প্রিভিউ সদস্য (১০ পৃষ্ঠা)"
                                },
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVip) Color(0xFF92400E) else (if (isUnlocked) Color(0xFF065F46) else Color(0xFF475569))
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // ৩. সাবস্ক্রিপশন শোকেস কার্ড (MindBloom "Subscription: Upgrade to Plus")
            // =========================================================================
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(
                        text = if (isEnglish) "Subscription" else "মেম্বারশিপ সাবস্ক্রিপশন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = cardBg,
                        border = BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF10B981).copy(alpha = 0.35f) else Color(0xFF93C5FD).copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0F6B56).copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFF0F6B56),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isEnglish) "Upgrade to Plus" else "ক্বারীয়ানা প্রিমিয়াম",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = if (isEnglish) "Unlock all 30 Paras & offline vault" else "সম্পূর্ণ ৩০ পারা ও অফলাইন সুবিধা আনলক করুন",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.5.sp,
                                        color = textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(Modifier.width(8.dp))

                            // স্লিক ব্ল্যাক পিল বাটন "See Plan" / "প্যাকেজ দেখুন"
                            Button(
                                onClick = { vm.navigate(Screen.MembershipPackages) },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDark) Color(0xFF0F6B56) else Color(0xFF091712)
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = if (isEnglish) "See Plan" else "প্যাকেজ দেখুন",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                }
            }

            // =========================================================================
            // ৪. অ্যাপের রূপ ও থিম পিল রো (MindBloom "Appearance" 4-Pill Segment)
            // =========================================================================
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(
                        text = if (isEnglish) "Appearance" else "অ্যাপের রূপ ও থিম",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppearancePillItem(
                            title = if (isEnglish) "System" else "সিস্টেম",
                            icon = Icons.Default.Tune,
                            isSelected = readerSettings.themeId == "system" || readerSettings.themeId == "cream",
                            isDark = isDark,
                            fonts = fonts,
                            onClick = { vm.updateSettings { it.copy(themeId = "system") } }
                        )
                        AppearancePillItem(
                            title = if (isEnglish) "For You" else "ফর ইউ",
                            icon = Icons.Default.AutoAwesome,
                            isSelected = readerSettings.themeId == "emerald",
                            isDark = isDark,
                            fonts = fonts,
                            onClick = { vm.updateSettings { it.copy(themeId = "emerald") } }
                        )
                        AppearancePillItem(
                            title = if (isEnglish) "Dark" else "ডার্ক",
                            icon = Icons.Default.DarkMode,
                            isSelected = readerSettings.themeId == "dark",
                            isDark = isDark,
                            fonts = fonts,
                            onClick = { vm.updateSettings { it.copy(themeId = "dark") } }
                        )
                        AppearancePillItem(
                            title = if (isEnglish) "Light" else "লাইট",
                            icon = Icons.Default.LightMode,
                            isSelected = readerSettings.themeId == "light",
                            isDark = isDark,
                            fonts = fonts,
                            onClick = { vm.updateSettings { it.copy(themeId = "light") } }
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                }
            }

            // =========================================================================
            // ৫. সেটিংস ও প্রয়োজনীয় তথ্য (MindBloom "Data & Information")
            // =========================================================================
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(
                        text = if (isEnglish) "Data & Information" else "সেটিংস ও প্রয়োজনীয় তথ্য",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = cardBg,
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            ProfileMenuRow(
                                icon = Icons.Default.Security,
                                title = if (isEnglish) "Digital License & Device Lock" else "ডিজিটাল লাইসেন্স ও ডিভাইস লক",
                                subtitle = if (session != null) (if (isEnglish) "Device: ${session.deviceId.take(14)}..." else "ডিভাইস: ${session.deviceId.take(14)}...") else (if (isEnglish) "Login to view license" else "লাইসেন্স দেখতে লগইন করুন"),
                                onClick = {
                                    if (session == null) vm.navigate(Screen.Login)
                                    else showLicenseSheet = true
                                },
                                fonts = fonts
                            )
                            HorizontalDivider(modifier = Modifier.padding(start = 54.dp), color = cardBorder)
                            ProfileMenuRow(
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                title = if (isEnglish) "Saved Ayahs & Bookmarks" else "সংরক্ষিত আয়াত ও বুকমার্কস",
                                subtitle = if (isEnglish) "${userData.bookmarks.size} Bookmarked Ayahs" else "${userData.bookmarks.size.toBn()}টি আয়াত বুকমার্ক করা আছে",
                                onClick = { vm.openIndex() },
                                fonts = fonts
                            )
                            HorizontalDivider(modifier = Modifier.padding(start = 54.dp), color = cardBorder)
                            ProfileMenuRow(
                                icon = Icons.Default.Verified,
                                title = if (isEnglish) "Reading Progress & History" else "পড়ার অগ্রগতি ও ইতিহাস",
                                subtitle = if (isEnglish) "Resume from Page ${userData.lastPage}" else "সর্বশেষ পঠিত পৃষ্ঠা ${userData.lastPage.toBn()} থেকে শুরু করুন",
                                onClick = { vm.openReader(userData.lastPage.coerceIn(1, 610)) },
                                fonts = fonts
                            )
                            HorizontalDivider(modifier = Modifier.padding(start = 54.dp), color = cardBorder)
                            ProfileMenuRow(
                                icon = Icons.Default.FontDownload,
                                title = if (isEnglish) "Bangla Font & Style" else "বাংলা ফন্ট ও আকার নির্ধারণ",
                                subtitle = "${readerSettings.banglaFont.replaceFirstChar { it.uppercase() }}${if (readerSettings.fontBold) " (Bold)" else ""}",
                                onClick = { showBanglaFontDialog = true },
                                fonts = fonts
                            )
                            HorizontalDivider(modifier = Modifier.padding(start = 54.dp), color = cardBorder)
                            ProfileMenuRow(
                                icon = Icons.Default.Notifications,
                                title = if (isEnglish) "Notifications & Azan Alerts" else "নোটিফিকেশন ও আযান অ্যালার্ট",
                                subtitle = if (isEnglish) "Daily 5 Waqt Studio Azan Alerts" else "৫ ওয়াক্তের সময়সূচি ও আযান",
                                onClick = { vm.navigate(Screen.Namaz) },
                                fonts = fonts
                            )
                            HorizontalDivider(modifier = Modifier.padding(start = 54.dp), color = cardBorder)
                            ProfileMenuRow(
                                icon = Icons.Default.SupportAgent,
                                title = if (isEnglish) "Customer Support & Helpline" else "গ্রাহক সহায়তা ও হেল্পলাইন",
                                subtitle = "WhatsApp: 01712-345678",
                                onClick = { showHelplineDialog = true },
                                fonts = fonts
                            )
                            HorizontalDivider(modifier = Modifier.padding(start = 54.dp), color = cardBorder)
                            ProfileMenuRow(
                                icon = Icons.Default.SystemUpdate,
                                title = if (isEnglish) "Check for Updates" else "নতুন আপডেট পরীক্ষা করুন",
                                subtitle = if (isCheckingUpdate) (if (isEnglish) "Checking..." else "যাচাই করা হচ্ছে...") else (if (isEnglish) "Version 1.3.8" else "সংস্করণ ১.৩.৮"),
                                onClick = { vm.checkAppUpdate(manual = true) },
                                fonts = fonts
                            )
                            HorizontalDivider(modifier = Modifier.padding(start = 54.dp), color = cardBorder)
                            ProfileMenuRow(
                                icon = Icons.Default.Info,
                                title = if (isEnglish) "About Qariana Publications" else "ক্বারীয়ানা প্রকাশনী পরিচিতি",
                                subtitle = if (isEnglish) "Dar-ul-Mujtaba Research & Publication" else "দারুল মুজতবা কুরআন গবেষণা ও প্রকাশনা",
                                onClick = { showAboutDialog = true },
                                fonts = fonts
                            )
                        }
                    }

                    // লগআউট বাটন (লগইন থাকলে লাল অ্যাকসেন্ট কার্ড)
                    if (isLoggedIn) {
                        Spacer(Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isDark) Color(0xFF2A1515) else Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth().clickable { showLogoutConfirmDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (isEnglish) "Log Out Account" else "অ্যাকাউন্ট থেকে লগআউট করুন",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // ডায়ালগসমূহ (ModernNoticeDialog প্রয়োগ)
    // =========================================================================

    // লগআউট কনফার্মেশন ডায়ালগ
    if (showLogoutConfirmDialog) {
        ModernNoticeDialog(
            title = if (isEnglish) "Log Out Account?" else "অ্যাকাউন্ট থেকে লগআউট?",
            message = if (isEnglish)
                "Are you sure you want to log out? Your offline downloaded pages will remain safe on this device."
            else
                "আপনি কি নিশ্চিত যে অ্যাকাউন্ট থেকে লগআউট করতে চান? আপনার অফলাইন সংরক্ষিত ডেটা সুরক্ষিত থাকবে।",
            type = ModernDialogType.WARNING,
            confirmText = if (isEnglish) "Yes, Log Out" else "হ্যাঁ, লগআউট করুন",
            dismissText = if (isEnglish) "Cancel" else "বাতিল",
            isDark = isDark,
            onConfirm = {
                showLogoutConfirmDialog = false
                vm.logout()
            },
            onDismissRequest = { showLogoutConfirmDialog = false }
        )
    }

    // হেল্পলাইন ডায়ালগ
    if (showHelplineDialog) {
        ModernNoticeDialog(
            title = if (isEnglish) "Customer Support" else "গ্রাহক সহায়তা ও হেল্পলাইন",
            message = if (isEnglish)
                "For any license or Quran reader support, connect directly via WhatsApp: +8801712-345678 or visit karianaquran.com"
            else
                "যেকোনো লাইসেন্স বা অ্যাপ সংক্রান্ত সহায়তায় সরাসরি আমাদের হোয়াটসঅ্যাপ হেল্পলাইনে যোগাযোগ করুন: ০১৭১২-৩৪৫৬৭৮।",
            type = ModernDialogType.INFO,
            confirmText = if (isEnglish) "Close" else "ঠিক আছে",
            isDark = isDark,
            onConfirm = { showHelplineDialog = false },
            onDismissRequest = { showHelplineDialog = false }
        )
    }

    // প্রকাশনী পরিচিতি ডায়ালগ
    if (showAboutDialog) {
        ModernNoticeDialog(
            title = if (isEnglish) "About Qariana" else "ক্বারীয়ানা প্রকাশনী ও দারুল মুজতবা",
            message = if (isEnglish)
                "Qariana Quran is dedicated to presenting the Holy Quran with high-fidelity InDesign vector typography, pronunciation, word-by-word meanings and color-coded Tajweed rules."
            else
                "ক্বারীয়ানা কুরআন দারুল মুজতবা গবেষণা ও প্রকাশনীর একটি অনন্য আন্তর্জাতিক ইসলামিক উদ্যোগ। সঠিক উচ্চারণ, শব্দে শব্দে অর্থ ও রঙিন তাজবীদ নিয়মের সমন্বয়ে ইনডিজাইন প্রিন্ট সংস্করণের অনবদ্য রূপ ফুটিয়ে তোলা হয়েছে এই ডিজিটাল অ্যাপ্লিকেশনে।",
            type = ModernDialogType.INFO,
            confirmText = if (isEnglish) "Close" else "ঠিক আছে",
            isDark = isDark,
            onConfirm = { showAboutDialog = false },
            onDismissRequest = { showAboutDialog = false }
        )
    }

    // বাংলা ফন্ট ডায়ালগ
    if (showBanglaFontDialog) {
        BanglaFontSelectionDialog(
            currentFont = readerSettings.banglaFont,
            currentBold = readerSettings.fontBold,
            onDismiss = { showBanglaFontDialog = false },
            onApply = { newFont, isBold ->
                vm.updateSettings { it.copy(banglaFont = newFont, fontBold = isBold) }
                showBanglaFontDialog = false
                Toast.makeText(context, "বাংলা ফন্ট সফলভাবে সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // ডিজিটাল লাইসেন্স বটমশিট
    if (showLicenseSheet && session != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val isVip = vm.isVipPhone(session.mobile)

        ModalBottomSheet(
            onDismissRequest = { showLicenseSheet = false },
            sheetState = sheetState,
            containerColor = if (isDark) Color(0xFF13221A) else Color(0xFFFBF9F5),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0F6B56),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Security, null, tint = Color(0xFFFFDF79), modifier = Modifier.size(22.dp))
                            Text(
                                text = "ডিজিটাল লাইসেন্স ও ডিভাইস লক",
                                fontFamily = fonts.kalpurush,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        IconButton(onClick = { showLicenseSheet = false }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "লাইসেন্স কী: ${session.licenseKey}",
                        fontFamily = fonts.kalpurush,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "নিবন্ধিত মোবাইল: ${session.mobile}",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        color = textSecondary
                    )
                    Text(
                        text = "ডিভাইস ফিঙ্গারপ্রিন্ট: ${session.deviceId}",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.5.sp,
                        color = textSecondary
                    )

                    Button(
                        onClick = { showLicenseSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("বন্ধ করুন", color = Color.White, fontFamily = fonts.kalpurush)
                    }
                }
            }
        }
    }
}

/**
 * অ্যাপেয়ারেন্স ৪-পিল রো আইটেম
 */
@Composable
private fun AppearancePillItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    isDark: Boolean,
    fonts: com.karianaquran.reader.ui.theme.AppFonts,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = CircleShape,
            color = if (isSelected) {
                if (isDark) Color(0xFF0F6B56) else Color(0xFF091712)
            } else {
                if (isDark) Color(0xFF13221A) else Color(0xFFF1F5F9)
            },
            border = BorderStroke(
                1.dp,
                if (isSelected) Color(0xFF10B981) else (if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFE2E8F0))
            ),
            modifier = Modifier.size(54.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) Color.White else (if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = title,
            fontFamily = fonts.kalpurush,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) (if (isDark) Color.White else Color(0xFF0F172A)) else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
        )
    }
}

@Composable
private fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    fonts: com.karianaquran.reader.ui.theme.AppFonts
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F6B56).copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF0F6B56),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = fonts.kalpurush,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontFamily = fonts.kalpurush,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun recordsMapState(context: Context): Int {
    val map by PrayerTrackerRepository.recordsFlow.collectAsState()
    return map.hashCode()
}

@Composable
private fun BanglaFontSelectionDialog(
    currentFont: String,
    currentBold: Boolean,
    onDismiss: () -> Unit,
    onApply: (font: String, isBold: Boolean) -> Unit
) {
    var selectedFont by remember { mutableStateOf(currentFont) }
    var isBold by remember { mutableStateOf(currentBold) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("বাংলা ফন্ট ও আকার নির্ধারণ", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F6B56)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("পছন্দের বাংলা ফন্ট নির্বাচন করুন:", fontSize = 12.sp, color = Color(0xFF64748B))
                FontRadioItem("কালপুরুষ (Kalpurush)", "ঐতিহ্যবাহী স্ট্যান্ডার্ড বাংলা ফন্ট (ডিফল্ট)", selectedFont == "kalpurush") { selectedFont = "kalpurush" }
                FontRadioItem("হিন্দ শিলিগুড়ি (Hind Siliguri)", "আধুনিক ও পরিচ্ছন্ন ডিজিটাল ফন্ট", selectedFont == "hind_siliguri") { selectedFont = "hind_siliguri" }
                FontRadioItem("ফিরাস্যান্স (Fira Sans)", "বোল্ড ও কম্প্যাক্ট পাঠযোগ্য ফন্ট", selectedFont == "fira_sans") { selectedFont = "fira_sans" }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { isBold = !isBold }.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("বোল্ড (Bold) টেক্সট মোড", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("টেক্সটের ঘনত্ব বৃদ্ধি করে স্পষ্ট পড়া", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    Switch(
                        checked = isBold,
                        onCheckedChange = { isBold = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0F6B56))
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(selectedFont, isBold) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
            ) {
                Text("প্রয়োগ ও সংরক্ষণ", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = Color(0xFF64748B)) }
        }
    )
}

@Composable
private fun FontRadioItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFFE8F5E9) else Color.Transparent,
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF0F6B56) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0F6B56))
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color(0xFF0F6B56) else Color(0xFF1E293B)
                )
                Text(text = subtitle, fontSize = 10.5.sp, color = Color(0xFF64748B))
            }
        }
    }
}
