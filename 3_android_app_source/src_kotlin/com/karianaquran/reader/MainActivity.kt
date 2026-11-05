package com.karianaquran.reader

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.Screen
import com.karianaquran.reader.ui.components.SecureDialog
import com.karianaquran.reader.ui.home.HomeScreen
import com.karianaquran.reader.ui.index.SurahIndexScreen
import com.karianaquran.reader.ui.library.LibraryScreen
import com.karianaquran.reader.ui.login.LoginScreen
import com.karianaquran.reader.ui.notifications.NotificationScreen
import com.karianaquran.reader.ui.overview.QuranOverviewScreen
import com.karianaquran.reader.ui.pay.PayScreen
import com.karianaquran.reader.ui.profile.ProfileScreen
import com.karianaquran.reader.ui.reader.ReaderScreen
import com.karianaquran.reader.ui.worship.NamazScreen
import com.karianaquran.reader.ui.worship.NamazTrackerScreen
import com.karianaquran.reader.ui.worship.TasbihScreen
import com.karianaquran.reader.ui.worship.DuaScreen
import com.karianaquran.reader.ui.worship.HadithScreen
import com.karianaquran.reader.ui.worship.QiblaScreen
import com.karianaquran.reader.ui.worship.NamesScreen
import com.karianaquran.reader.ui.worship.PillarsScreen
import com.karianaquran.reader.ui.worship.RamadanScreen
import com.karianaquran.reader.ui.settings.SettingsScreen
import com.karianaquran.reader.ui.home.MoreFeaturesScreen
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts
import com.karianaquran.reader.ui.theme.QarianaTheme

class MainActivity : ComponentActivity() {
    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 2001)
            }
        }
    }


    private val vm: AppViewModel by viewModels()

    companion object {
        /**
         * [ANTI-PIRACY & SCREENSHOT PROTECTION]
         * ব্যবহারকারীর টেস্ট বিল্ডের নির্দেশনা অনুযায়ী সব পেজ থেকে স্ক্রিনশট প্রটেকশন অফ রাখা হয়েছে।
         */
        val ENFORCE_SCREENSHOT_SECURITY: Boolean = false
    }

    // Android 14+ (API 34+) স্ক্রিন ক্যাপচার ডিটেকশন কলব্যাক
    private val screenCaptureCallback by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Activity.ScreenCaptureCallback {
                runOnUiThread {
                    if (ENFORCE_SCREENSHOT_SECURITY) {
                        vm.onScreenCaptureDetected()
                    }
                }
            }
        } else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        if (intent?.getStringExtra("navigate_to") == "namaz") {
            vm.navigate(Screen.Namaz)
        }

        // নামাজের ওয়াক্ত ও আজানের নোটিফিকেশন সচল রাখতে সিস্টেম পারমিশন রিকোয়েস্ট



        setContent {
            val isReading = vm.current is Screen.Reader
            val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
            val readerSettings by vm.settings.collectAsState(initial = com.karianaquran.reader.data.ReaderSettings())
            val isDarkTheme = readerSettings.themeId == "dark"

            androidx.compose.runtime.DisposableEffect(isDarkTheme, vm.current, isReading) {
                // স্ট্যাটাস বার টেক্সট ও আইকন কালার: লাইট মোডে ডার্ক আইকন (স্পষ্ট পড়ার জন্য), ডার্ক মোডে সাদা আইকন
                val isLightBars = !isDarkTheme && vm.current !is Screen.Pay
                val style = if (isLightBars) {
                    androidx.activity.SystemBarStyle.light(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    )
                } else {
                    androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)

                insetsController.isAppearanceLightStatusBars = isLightBars
                insetsController.isAppearanceLightNavigationBars = isLightBars

                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT

                if (isReading && ENFORCE_SCREENSHOT_SECURITY) {
                    // শুধুমাত্র রিডার স্ক্রিনে স্ক্রিনশট ও স্ক্রিন রেকর্ডিং ১০০% ব্লক (বইয়ের পাইরেসি রোধ)
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    // অন্য সকল স্ক্রিনে (হোম, ওভারভিউ, সেটিংস ইত্যাদি) স্ক্রিনশট ও রেকর্ডিং উন্মুক্ত
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
                onDispose {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            var showSplash by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }
            androidx.compose.runtime.LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2200)
                showSplash = false
            }

            val isEnglish = readerSettings.appLanguage == "en"
            val currentFonts = androidx.compose.runtime.remember(
                readerSettings.banglaFont,
                readerSettings.fontBold,
                readerSettings.englishFont,
                readerSettings.appLanguage
            ) {
                vm.getDynamicFonts(
                    banglaFont = readerSettings.banglaFont,
                    fontBold = readerSettings.fontBold,
                    englishFont = readerSettings.englishFont,
                    appLanguage = readerSettings.appLanguage
                )
            }

            QarianaTheme(fonts = currentFonts, darkTheme = isDarkTheme, isEnglish = isEnglish) {
                androidx.compose.animation.Crossfade(
                    targetState = showSplash,
                    animationSpec = androidx.compose.animation.core.tween(500),
                    label = "splash_crossfade"
                ) { isSplash ->
                    if (isSplash) {
                        com.karianaquran.reader.ui.components.QarianaSplashScreen()
                    } else {
                        MainContent(vm = vm, onExit = { finish() })
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            runCatching {
                screenCaptureCallback?.let {
                    registerScreenCaptureCallback(mainExecutor, it)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            runCatching {
                screenCaptureCallback?.let {
                    unregisterScreenCaptureCallback(it)
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    vm: AppViewModel,
    onExit: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"

    // ব্যাক বাটন হ্যান্ডলিং: স্ট্যাকে পেজ থাকলে পেছনে ফেরে, না থাকলে অ্যাপ ক্লোজ
    BackHandler(enabled = true) {
        if (!vm.back()) {
            onExit()
        }
    }

    // সিকিউরিটি গার্ড এখন স্কোপড (Scoped Security) — সম্পূর্ণ অ্যাপ ক্র্যাশ বা লক না করে
    // সাধারণ স্ক্রিন (Home, Catalog, Settings) সবসময় খোলা থাকে,
    // শুধুমাত্র কপিরাইটযুক্ত সংরক্ষিত কুরআন রিডারে ডিক্রিপশন উইথহোল্ড ও গাইডেন্স দেখায়।

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // স্ক্রিন ট্রানজিশন
        AnimatedContent(
            targetState = vm.current,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition"
        ) { targetScreen ->
            when (targetScreen) {
                Screen.Home -> HomeScreen(vm = vm)
                Screen.Library -> LibraryScreen(vm = vm)
                is Screen.SurahIndex -> SurahIndexScreen(vm = vm, initialTab = targetScreen.initialTab)
                is Screen.EditionDetail -> com.karianaquran.reader.ui.overview.EditionDetailScreen(initialMode = targetScreen.initialMode, vm = vm)
                Screen.MembershipPackages -> com.karianaquran.reader.ui.pay.MembershipPackagesScreen(vm = vm)
                Screen.Profile -> ProfileScreen(vm = vm)
                Screen.Notifications -> NotificationScreen(vm = vm)
                is Screen.QuranOverview -> SurahIndexScreen(vm = vm, initialTab = if (vm.isUnlocked) 1 else 0)
                is Screen.Reader -> ReaderScreen(startPage = targetScreen.startPage, vm = vm)
                Screen.Login -> LoginScreen(vm = vm)
                Screen.Pay -> PayScreen(vm = vm)
                Screen.Namaz -> NamazScreen(vm = vm)
                Screen.Tasbih -> TasbihScreen(vm = vm)
                Screen.Dua -> DuaScreen(vm = vm)
                Screen.Hadith -> HadithScreen(vm = vm)
                Screen.Qibla -> QiblaScreen(vm = vm)
                Screen.Names -> NamesScreen(vm = vm)
                Screen.Pillars -> PillarsScreen(vm = vm)
                Screen.Ramadan -> RamadanScreen(vm = vm)
                Screen.Settings -> SettingsScreen(vm = vm)
                Screen.MoreFeatures -> MoreFeaturesScreen(vm = vm)
                Screen.NamazTracker -> NamazTrackerScreen(vm = vm)
                Screen.EditProfile -> com.karianaquran.reader.ui.profile.EditProfileScreen(vm = vm)
                is Screen.InAppBrowser -> com.karianaquran.reader.ui.browser.InAppBrowserScreen(url = targetScreen.url, title = targetScreen.title, vm = vm)
            }
        }

        // গ্লোবাল ইন-অ্যাপ আপডেট ডায়ালগ (সার্ভার থেকে নতুন ভার্সন পাওয়া গেলে তাৎক্ষণিক পপআপ ও বাধ্যতামূলক আপডেট)
        val updateState = vm.updateState
        if (updateState is com.karianaquran.reader.data.remote.UpdateCheckResult.UpdateAvailable) {
            val info = updateState.info
            val context = androidx.compose.ui.platform.LocalContext.current
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    // নতুন আপডেট এলে আপডেট করা সম্পূর্ণ বাধ্যতামূলক — ডায়ালগ বন্ধ করা যাবে না
                    if (!info.isMandatory) vm.dismissUpdateDialog()
                },
                properties = androidx.compose.ui.window.DialogProperties(
                    dismissOnBackPress = !info.isMandatory,
                    dismissOnClickOutside = !info.isMandatory
                ),
                containerColor = if (isDark) Color(0xFF13221A) else Color.White,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (info.isMandatory) "বাধ্যতামূলক আপডেট প্রয়োজন!" else "ক্বারীয়ানা কুরআন আপডেট উপলব্ধ!",
                            fontFamily = fonts.kalpurush,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = if (isDark) Color(0xFFFFDF79) else Color(0xFF0F6B56)
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "নতুন সংস্করণ: v${info.latestVersionName} (${info.fileSizeMb} MB)",
                            fontFamily = fonts.kalpurush,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                        Text(
                            text = info.releaseNotesBn,
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                            lineHeight = 17.sp
                        )
                        if (info.isMandatory) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE11D48).copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE11D48).copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "অ্যাপের ধারাবাহিক কার্যকারিতা ও নিরাপত্তা বজায় রাখতে এই সংস্করণটি ইনস্টল করা বাধ্যতামূলক। আপডেট সম্পন্ন না করা পর্যন্ত অ্যাপটি ব্যবহার করা যাবে না।",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 11.5.sp,
                                    color = if (isDark) Color(0xFFFCA5A5) else Color(0xFF991B1B),
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(info.downloadUrl)).apply {
                                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SystemUpdate, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("এখনই ইন-অ্যাপ আপডেট করুন", fontFamily = fonts.kalpurush, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // প্লে-স্টোর আপডেট অপশন (প্লে-স্টোর রিলিজ না হওয়া পর্যন্ত হাইড/ডিজেবল)
                        if (info.isPlayStoreAvailable) {
                            androidx.compose.material3.OutlinedButton(
                                onClick = {
                                    val playStoreIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=${context.packageName}")).apply {
                                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    try {
                                        context.startActivity(playStoreIntent)
                                    } catch (e: Exception) {
                                        context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(40.dp)
                            ) {
                                Text("Google Play Store থেকে আপডেট করুন", fontFamily = fonts.kalpurush, fontSize = 12.sp, color = Color(0xFF0F6B56))
                            }
                        }
                    }
                },
                dismissButton = {
                    if (!info.isMandatory) {
                        TextButton(onClick = { vm.dismissUpdateDialog() }) {
                            Text("পরে", fontFamily = fonts.kalpurush, color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                        }
                    }
                }
            )
        }

        // স্ক্রিন ক্যাপচার সতর্কবার্তা ডায়ালগ
        if (vm.captureWarning) {
            SecureDialog(onDismiss = { vm.dismissCaptureWarning() }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Brand.Obsidian,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Brand.Gold),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = "স্ক্রিনশট বা রেকর্ডিং নিষিদ্ধ!",
                            fontFamily = fonts.kalpurush,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brand.Gold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "ক্বারীয়ানা কুরআনের সকল ডিজিটাল কন্টেন্ট সুরক্ষিত। স্ক্রিনশট বা স্ক্রিন রেকর্ডিং নেওয়া আইনত দণ্ডনীয় এবং অ্যাপটি স্বয়ংক্রিয়ভাবে ব্ল্যাক স্ক্রিন প্রদর্শন করে।",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(Modifier.height(18.dp))

                        Button(
                            onClick = { vm.dismissCaptureWarning() },
                            colors = ButtonDefaults.buttonColors(containerColor = Brand.EmeraldDark),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "বুঝেছি",
                                fontFamily = fonts.kalpurush,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // সিস্টেম নোটিশ ডায়ালগ (আধুনিক আন্তর্জাতিক স্ট্যান্ডার্ড কার্ড ডায়ালগ)
        vm.notice?.let { noticeMsg ->
            val dialogType = when {
                noticeMsg.contains("সফল") || noticeMsg.contains("আনলক") || noticeMsg.contains("success", ignoreCase = true) ->
                    com.karianaquran.reader.ui.components.ModernDialogType.SUCCESS
                noticeMsg.contains("ভুল") || noticeMsg.contains("ব্যর্থ") || noticeMsg.contains("ত্রুটি") || noticeMsg.contains("error", ignoreCase = true) ->
                    com.karianaquran.reader.ui.components.ModernDialogType.ERROR
                noticeMsg.contains("সতর্ক") || noticeMsg.contains("বাতিল") ->
                    com.karianaquran.reader.ui.components.ModernDialogType.WARNING
                else ->
                    com.karianaquran.reader.ui.components.ModernDialogType.INFO
            }

            com.karianaquran.reader.ui.components.ModernNoticeDialog(
                title = if (dialogType == com.karianaquran.reader.ui.components.ModernDialogType.SUCCESS) "অভিনন্দন!" else "বিজ্ঞপ্তি",
                message = noticeMsg,
                type = dialogType,
                confirmText = "ঠিক আছে",
                isDark = isDark,
                onDismissRequest = { vm.notice = null },
                onConfirm = { vm.notice = null }
            )
        }
    }
}

