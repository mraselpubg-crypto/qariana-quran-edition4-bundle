package com.karianaquran.reader.ui.home

import android.content.Intent
import android.net.Uri

import androidx.compose.ui.graphics.graphicsLayer

import com.karianaquran.reader.data.prayer.DayPrayerRecord
import com.karianaquran.reader.data.prayer.PrayerTrackerRepository
import androidx.compose.material.icons.filled.Lock

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.karianaquran.reader.data.location.District
import com.karianaquran.reader.data.location.LocationHelper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import com.karianaquran.reader.data.model.Publication
import com.karianaquran.reader.data.model.Publications
import com.karianaquran.reader.ui.components.RealBookCover
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.R
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.Screen
import com.karianaquran.reader.ui.components.QuranMetadata
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.home.components.AboutUsDialog
import com.karianaquran.reader.ui.home.components.DarUlMujtabaBottomBar
import com.karianaquran.reader.ui.home.components.HomeFeatureGrid
import com.karianaquran.reader.ui.home.components.HomeLastReadCard
import com.karianaquran.reader.ui.home.components.HomeNavigationDrawer
import com.karianaquran.reader.ui.home.components.HomePrayerTrackerCard
import com.karianaquran.reader.ui.home.components.HomeRamadanCountdownCard
import com.karianaquran.reader.ui.home.components.HomeTopBar
import com.karianaquran.reader.ui.home.components.HomeUpcomingPublicationsSection
import com.karianaquran.reader.ui.home.components.LanguageSelectDialog
import com.karianaquran.reader.ui.home.components.UpcomingBookDetailDialog
import com.karianaquran.reader.ui.home.components.triggerHaptic
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts
import com.karianaquran.reader.ui.theme.Palettes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// -------------------------------------------------------------
// কালার কনস্ট্যান্টস → ui/home/HomeTokens.kt-এ সরানো হয়েছে (split)
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val userData by vm.userData.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ডায়ালগ স্টেট
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var upcomingDetailBook by remember { mutableStateOf<Publication?>(null) }

    // আযান প্লেয়ার স্টেট
    val isAzanPlaying by com.karianaquran.reader.service.AzanPlayer.isPlaying.collectAsState()

    // লোকেশন ও জেলা নির্বাচন স্টেট
    var currentDistrict by remember {
        mutableStateOf(LocationHelper.getSelectedDistrict(context))
    }
    var showDistrictSheet by remember { mutableStateOf(false) }
    var showLocationPermissionDialog by remember {
        mutableStateOf(false)
    }
    var showOnboardingDialog by remember { mutableStateOf(false) }

    val notifPrefs = remember(context) { context.getSharedPreferences("qariana_notif_prefs", android.content.Context.MODE_PRIVATE) }
    var showNotificationDialog by remember {
        mutableStateOf(
            !com.karianaquran.reader.service.AudioNotificationManager.areNotificationsEnabled(context) &&
            !notifPrefs.getBoolean("has_prompted_notification", false)
        )
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (!readerSettings.hasSeenOnboarding) {
            kotlinx.coroutines.delay(2800)
            showOnboardingDialog = true
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        LocationHelper.setPermissionRequested(context)
        showLocationPermissionDialog = false
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            LocationHelper.detectDeviceLocation(context) { detected ->
                currentDistrict = detected
            }
        }
    }


    // রিয়েল-টাইম টাইমার স্টেট (প্রতি সেকেন্ডে কাউন্টডাউন আপডেট)
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000)
        }
    }

    // প্রতি মিনিটে একবার তারিখ ও হিজরী ক্যালেন্ডার মেমোইজেশন (CPU ও মেমোরি চরম অপটিমাইজড)
    val currentMinuteBucket = currentTimeMillis / 60000L
    val isEnglish = readerSettings.appLanguage == "en"
    val nowForDate = remember(currentMinuteBucket) { Date(currentTimeMillis) }
    val gregorianDateStr = remember(currentMinuteBucket, isEnglish) {
        if (isEnglish) {
            java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.ENGLISH).format(nowForDate)
        } else {
            val monthsBn = arrayOf("জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন", "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর")
            val cal = Calendar.getInstance().apply { time = nowForDate }
            val day = cal.get(Calendar.DAY_OF_MONTH).toBn()
            val month = monthsBn[cal.get(Calendar.MONTH)]
            val year = cal.get(Calendar.YEAR).toBn()
            "$day $month $year"
        }
    }
    val hijriDateStr = remember(currentMinuteBucket, isEnglish) {
        calculateDynamicHijriDate(nowForDate, isEnglish)
    }

    // সঠিক প্রেয়ার ও রিমেইনিং কাউন্টডাউন হিসাব (বর্তমান জেলার স্থানাঙ্ক অনুযায়ী)
    val nextPrayerInfo = remember(currentTimeMillis, currentDistrict) {
        com.karianaquran.reader.data.prayer.PrayerCalculator.getNextPrayer(
            now = Date(currentTimeMillis),
            latitude = currentDistrict.lat,
            longitude = currentDistrict.lon
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            com.karianaquran.reader.service.AzanPlayer.playAzan(
                context,
                nextPrayerInfo.currentPrayerNameBn,
                nextPrayerInfo.currentPrayerNameBn == "ফজর",
                nextPrayerInfo.nextPrayerTimeBn
            )
        }
    }

    // রমজান কাউন্টডাউন হিসাব
    val ramadanCountdown = remember(currentTimeMillis) {
        calculateRamadanCountdown(Date(currentTimeMillis))
    }

    // নামাজ ট্র্যাকার স্টেট (লোকাল রিপোজিটরি ও ক্লাউড সিঙ্ক থেকে)
    androidx.compose.runtime.LaunchedEffect(Unit) {
        PrayerTrackerRepository.init(context)
    }
    val prayerRecordsMap by PrayerTrackerRepository.recordsFlow.collectAsState()
    val todayDateStr = remember { PrayerTrackerRepository.getTodayDateString() }
    val todayRecord = prayerRecordsMap[todayDateStr] ?: DayPrayerRecord(date = todayDateStr)
    val completedCount = todayRecord.completedCount

    // সর্বশেষ পঠিত সূরা তথ্য (মোড সচেতন: ক্লাসিক মোডে ১১/১২ অফসেট অনুযায়ী সঠিক সূরা দেখাবে)
    val lastPage = userData.lastPage.coerceIn(1, 610)
    val currentSurah = remember(lastPage, readerSettings.readingMode) {
        QuranMetadata.surahForPage(lastPage, readerSettings.readingMode)
    }

    // নেভিগেশন ড্রয়ার র‍্যাপার
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeNavigationDrawer(
                vm = vm,
                isDark = isDark,
                onClose = { scope.launch { drawerState.close() } },
                onOpenQuranEdition = {
                    scope.launch { drawerState.close() }
                    vm.openQuranOverview(lastPage)
                },
                onOpenQibla = {
                    scope.launch { drawerState.close() }
                    vm.navigate(Screen.Qibla)
                },
                onOpenNames = {
                    scope.launch { drawerState.close() }
                    vm.navigate(Screen.Names)
                },
                onOpenPillars = {
                    scope.launch { drawerState.close() }
                    vm.navigate(Screen.Pillars)
                },
                onOpenRamadan = {
                    scope.launch { drawerState.close() }
                    vm.navigate(Screen.Ramadan)
                },
                onOpenSettings = {
                    scope.launch { drawerState.close() }
                    vm.navigate(Screen.Settings)
                },
                onOpenLanguage = {
                    scope.launch { drawerState.close() }
                    showLanguageDialog = true
                },
                onOpenAbout = {
                    scope.launch { drawerState.close() }
                    showAboutDialog = true
                }
            )
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = if (isDark) ScreenBgDark else ScreenBgLight,
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            bottomBar = {
                DarUlMujtabaBottomBar(activeTab = 0, isDark = isDark, isEnglish = isEnglish, onTabSelect = { tabIdx ->
                        when (tabIdx) {
                            0 -> { /* ইতোমধ্যে হোম স্ক্রিনে */ }
                            1 -> vm.openLibrary()
                            2 -> vm.openQuranOverview()
                            3 -> vm.openProfile()
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
            ) {
                // ১. পরিচ্ছন্ন টপ অ্যাপ বার (মেনু, ইংরেজি ও হিজরি তারিখ, সেটিংস ও অ্যানিমেটেড ক্রাউন বাটন)
                HomeTopBar(
                    isDark = isDark,
                    isUnlocked = vm.isUnlocked,
                    gregorianDate = gregorianDateStr,
                    hijriDate = hijriDateStr,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSearchClick = { vm.openQuranOverview() },
                    onCrownClick = {
                        if (vm.isUnlocked) vm.navigate(Screen.Profile) else vm.navigate(Screen.Pay)
                    },
                    onSettingsClick = { vm.navigate(Screen.Settings) }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // ২. মূল হিরো প্রেয়ার কার্ড (লোকেশন পিল, আযান বাটন, ডোম আর্চ এবং পরবর্তী ওয়াক্ত স্ট্রিপ)
                HomePrayerHeroCard(
                    isDark = isDark,
                    nextPrayerInfo = nextPrayerInfo,
                    locationName = if (isEnglish) "${currentDistrict.nameEn}, Bangladesh" else "${currentDistrict.nameBn}, বাংলাদেশ",
                    isAzanPlaying = isAzanPlaying,
                    onLocationClick = { showDistrictSheet = true },
                    onToggleAzan = {
                        if (isAzanPlaying) {
                            com.karianaquran.reader.service.AzanPlayer.stopAzan()
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                androidx.core.content.ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                            ) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            com.karianaquran.reader.service.AzanPlayer.playAzan(
                                context,
                                nextPrayerInfo.currentPrayerNameBn,
                                nextPrayerInfo.currentPrayerNameBn == "ফজর",
                                nextPrayerInfo.nextPrayerTimeBn
                            )
                        }
                    },
                    onCardClick = { vm.navigate(Screen.Namaz) },
                    isEnglish = isEnglish
                )

                // ৩. রমজান কাউন্টডাউন সিন কার্ড (শুধুমাত্র সেটিংসে অন করা থাকলে অথবা রমজান মাসে দৃশ্যমান)
                if (readerSettings.showRamadanCountdown) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HomeRamadanCountdownCard(
                        isDark = isDark,
                        days = ramadanCountdown.days,
                        hours = ramadanCountdown.hours,
                        minutes = ramadanCountdown.minutes,
                        seconds = ramadanCountdown.seconds,
                        onViewAllClick = { vm.navigate(Screen.Ramadan) },
                        isEnglish = isEnglish
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // ৪. মূল ৯-ফিচার গ্রিড (লাইন ১: পূর্ণাঙ্গ আল-কুরআন ব্যানার বাটন; লাইন ২ ও ৩: ৮টি মূল ইসলামিক ফিচার)
                HomeFeatureGrid(
                    isDark = isDark,
                    isEnglish = isEnglish,
                    onQuranClick = { vm.openQuranOverview(lastPage) },
                    onPrayerClick = { vm.navigate(Screen.Namaz) },
                    onNamesClick = { vm.navigate(Screen.Names) },
                    onTasbihClick = { vm.navigate(Screen.Tasbih) },
                    onQiblaClick = { vm.navigate(Screen.Qibla) },
                    onDuasClick = { vm.navigate(Screen.Dua) },
                    onPillarsClick = { vm.navigate(Screen.Pillars) },
                    onRamadanClick = { vm.navigate(Screen.Ramadan) },
                    onMoreClick = { vm.navigate(Screen.MoreFeatures) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ৭. দৈনিক নামাজ ট্র্যাকার কার্ড (Prayer Tracker Scene Card with Progress)
                HomePrayerTrackerCard(
                    isDark = isDark,
                    todayRecord = todayRecord,
                    todayDateStr = todayDateStr,
                    completedCount = completedCount,
                    onToggle = { prayerKey ->
                        triggerHaptic(context)
                        val res = PrayerTrackerRepository.togglePrayer(context, prayerKey, todayDateStr, isEnglish)
                        if (!res.first && res.second.isNotEmpty()) {
                            android.widget.Toast.makeText(context, res.second, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    onViewHistoryClick = { vm.navigate(Screen.NamazTracker) },
                    isEnglish = isEnglish
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ৮. আমাদের প্রকাশিত ও আসন্ন কিতাবসমূহ (হরিজন্টাল ক্যারোসেল)
                HomeUpcomingPublicationsSection(
                    isDark = isDark,
                    onBookClick = { book -> upcomingDetailBook = book },
                    onViewAllClick = { vm.openLibrary() },
                    isEnglish = isEnglish
                )

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showLanguageDialog) {
        LanguageSelectDialog(
            currentLang = readerSettings.appLanguage,
            onDismiss = { showLanguageDialog = false },
            onSelect = { lang ->
                vm.updateSettings { it.copy(appLanguage = lang) }
                showLanguageDialog = false
            }
        )
    }

    if (showAboutDialog) {
        AboutUsDialog(
            isDark = isDark,
            onDismiss = { showAboutDialog = false }
        )
    }

    upcomingDetailBook?.let { book ->
        UpcomingBookDetailDialog(
            book = book,
            isDark = isDark,
            onDismiss = { upcomingDetailBook = null }
        )
    }

    // জেলা নির্বাচন ও জিপিএস অটো-ডিটেকশন মডাল বটমশীট
    if (showDistrictSheet) {
        DistrictSelectSheet(
            isDark = isDark,
            currentDistrict = currentDistrict,
            onDistrictSelected = { district ->
                currentDistrict = district
                LocationHelper.saveSelectedDistrict(context, district)
            },
            onDismiss = { showDistrictSheet = false }
        )
    }

    // প্রথমবার অ্যাপ চালুর ২.৮ সেকেন্ড পর আধুনিক অনবোর্ডিং ও পারমিশন ডায়ালগ
    if (showOnboardingDialog) {
        com.karianaquran.reader.ui.components.OnboardingWelcomeDialog(
            onAllow = {
                vm.updateSettings { it.copy(hasSeenOnboarding = true) }
                showOnboardingDialog = false
                LocationHelper.setPermissionRequested(context)
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    (context as? com.karianaquran.reader.MainActivity)?.requestNotificationPermission()
                }
            },
            onSkip = {
                vm.updateSettings { it.copy(hasSeenOnboarding = true) }
                LocationHelper.setPermissionRequested(context)
                showOnboardingDialog = false
            }
        )
    }

    // লোকেশন পারমিশন রিকোয়েস্ট ডায়ালগ (ম্যানুয়াল ক্লিক বা প্রয়োজন অনুযায়ী)
    if (showLocationPermissionDialog) {
        AlertDialog(
            onDismissRequest = {
                LocationHelper.setPermissionRequested(context)
                showLocationPermissionDialog = false
            },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF059669).copy(alpha = 0.15f),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "লোকেশন",
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = "সঠিক নামাজের ওয়াক্ত ও কিবলা",
                    fontFamily = fonts.kalpurush,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    color = if (isDark) Color.White else Color(0xFF064E3B)
                )
            },
            text = {
                Text(
                    text = "আপনার এলাকার সঠিক নামাজের সময়সূচি, সেহরি-ইফতারের সময় এবং আবহাওয়ার তথ্য পাওয়ার জন্য আপনার অবস্থানের অনুমতি প্রয়োজন।",
                    fontFamily = fonts.kalpurush,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF374151)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "অনুমতি দিন",
                        fontFamily = fonts.kalpurush,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            LocationHelper.setPermissionRequested(context)
                            showLocationPermissionDialog = false
                            showDistrictSheet = true
                        }
                    ) {
                        Text(
                            text = "জেলা নির্বাচন",
                            fontFamily = fonts.kalpurush,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }
                    TextButton(
                        onClick = {
                            LocationHelper.setPermissionRequested(context)
                            showLocationPermissionDialog = false
                        }
                    ) {
                        Text(
                            text = "পরে করব",
                            fontFamily = fonts.kalpurush,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            },
            containerColor = if (isDark) Color(0xFF0B1F17) else Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showNotificationDialog) {
        com.karianaquran.reader.ui.components.NotificationPermissionDialog(
            onDismiss = {
                notifPrefs.edit().putBoolean("has_prompted_notification", true).apply()
                showNotificationDialog = false
            },
            onGranted = {
                notifPrefs.edit().putBoolean("has_prompted_notification", true).apply()
                showNotificationDialog = false
            }
        )
    }
}

// -------------------------------------------------------------------------------------------------
// ১. টপ অ্যাপ বার (HomeTopBar)
// -------------------------------------------------------------------------------------------------



// -------------------------------------------------------------------------------------------------
// ৩. রমজান কাউন্টডাউন সিন কার্ড (HomeRamadanCountdownCard)
// -------------------------------------------------------------------------------------------------


// -------------------------------------------------------------------------------------------------
// ৬. সর্বশেষ পঠিত কার্ড (HomeLastReadCard)
// -------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------
// ৭. দৈনিক নামাজ ট্র্যাকার কার্ড (HomePrayerTrackerCard)
// -------------------------------------------------------------------------------------------------


// -------------------------------------------------------------------------------------------------
// ৮. আমাদের প্রকাশিত ও আসন্ন কিতাবসমূহ (HomeUpcomingPublicationsSection)
// -------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------
// ৯. শীঘ্রই আসছে বিস্তারিত মডাল ডায়ালগ (UpcomingBookDetailDialog)
// -------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------
// ৭. নেভিগেশন ড্রয়ার (HomeNavigationDrawer)
// -------------------------------------------------------------------------------------------------


// -------------------------------------------------------------------------------------------------
// ১৪. ভাষা নির্বাচন ডায়ালগ (LanguageSelectDialog)
// -------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------
// ১৫. আমাদের সম্পর্কে ডায়ালগ (AboutUsDialog)
// -------------------------------------------------------------------------------------------------

// =========================================================================
// দার-উল-মুজতাবা মেটেরিয়াল বটম নেভিগেশন বার (অন্যান্য স্ক্রিনের জন্য রক্ষিত)
// =========================================================================


// -------------------------------------------------------------------------------------------------
// হেল্পার ডেটা ও ক্যালকুলেশন লজিক
// -------------------------------------------------------------------------------------------------
private data class NextPrayerResult(
    val nameBn: String,
    val timeStr: String,
    val remainingFormatted: String,
    val progress: Float
)

private data class RamadanCountdownResult(
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int
)

private fun calculateDynamicHijriDate(date: Date, isEnglish: Boolean = false): String {
    val cal = Calendar.getInstance().apply { time = date }
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val hijriDay = ((day + 12) % 30) + 1
    return if (isEnglish) {
        "$hijriDay Dhu al-Qadah 1447 AH"
    } else {
        "${hijriDay.toBn()} জিলকদ ১৪৪৭ হিজরি"
    }
}

private fun getTodayPrayerSchedule(date: Date): List<Triple<String, Int, Int>> {
    return listOf(
        Triple("ফজর", 4, 52),
        Triple("যোহর", 11, 48),
        Triple("আসর", 15, 56),
        Triple("মাগরিব", 17, 27),
        Triple("এশা", 18, 44)
    )
}

private fun calculateNextPrayer(now: Date, schedule: List<Triple<String, Int, Int>>): NextPrayerResult {
    val cal = Calendar.getInstance().apply { time = now }
    val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    val nowSeconds = cal.get(Calendar.SECOND)

    for ((name, h, m) in schedule) {
        val targetMinutes = h * 60 + m
        if (targetMinutes > nowMinutes) {
            val diffSec = (targetMinutes - nowMinutes) * 60 - nowSeconds
            val rh = diffSec / 3600
            val rm = (diffSec % 3600) / 60
            val rs = diffSec % 60
            val timeStr = formatPrayerTime(h, m).toBn()
            val remStr = String.format("%02d:%02d:%02d", rh, rm, rs).toBn()
            val progress = 1f - (diffSec.toFloat() / (4 * 3600f)).coerceIn(0f, 1f)
            return NextPrayerResult(name, timeStr, remStr, progress)
        }
    }

    val (name, h, m) = schedule.first()
    val targetMinutes = (24 * 60) + (h * 60 + m)
    val diffSec = (targetMinutes - nowMinutes) * 60 - nowSeconds
    val rh = diffSec / 3600
    val rm = (diffSec % 3600) / 60
    val rs = diffSec % 60
    val timeStr = formatPrayerTime(h, m).toBn()
    val remStr = String.format("%02d:%02d:%02d", rh, rm, rs).toBn()
    return NextPrayerResult(name, timeStr, remStr, 0.25f)
}

private fun formatPrayerTime(hour: Int, minute: Int): String {
    val isPm = hour >= 12
    val h12 = if (hour % 12 == 0) 12 else hour % 12
    val amPm = if (isPm) "মি." else "মি."
    return String.format("%02d:%02d %s", h12, minute, amPm)
}

private fun calculateRamadanCountdown(now: Date): RamadanCountdownResult {
    val cal = Calendar.getInstance().apply { time = now }
    val currentSec = cal.timeInMillis / 1000
    val targetCal = Calendar.getInstance().apply {
        set(Calendar.MONTH, Calendar.FEBRUARY)
        set(Calendar.DAY_OF_MONTH, 17)
        if (before(cal)) add(Calendar.YEAR, 1)
    }
    val diffSec = (targetCal.timeInMillis / 1000 - currentSec).coerceAtLeast(0)
    val d = (diffSec / 86400).toInt()
    val h = ((diffSec % 86400) / 3600).toInt()
    val m = ((diffSec % 3600) / 60).toInt()
    val s = (diffSec % 60).toInt()
    return RamadanCountdownResult(d, h, m, s)
}

