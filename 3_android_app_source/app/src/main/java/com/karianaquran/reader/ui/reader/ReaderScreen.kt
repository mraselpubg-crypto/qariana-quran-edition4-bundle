package com.karianaquran.reader.ui.reader



import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.material3.TextButton
import com.karianaquran.reader.security.SecurityGuard

import androidx.compose.animation.AnimatedVisibility

import androidx.compose.animation.core.LinearEasing

import androidx.compose.animation.core.RepeatMode

import androidx.compose.animation.core.animateFloat

import androidx.compose.animation.core.infiniteRepeatable

import androidx.compose.animation.core.rememberInfiniteTransition

import androidx.compose.animation.core.tween

import androidx.compose.animation.fadeIn

import androidx.compose.animation.fadeOut

import androidx.compose.animation.slideInVertically

import androidx.compose.animation.slideOutVertically

import androidx.compose.foundation.background

import androidx.compose.ui.geometry.Offset

import androidx.compose.foundation.border

import androidx.compose.foundation.clickable

import androidx.compose.foundation.gestures.detectTapGestures

import androidx.compose.foundation.gestures.detectTransformGestures

import androidx.compose.foundation.interaction.MutableInteractionSource

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.ExperimentalLayoutApi

import androidx.compose.foundation.layout.FlowRow

import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxHeight

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.navigationBarsPadding

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.size

import androidx.compose.foundation.layout.statusBarsPadding

import androidx.compose.foundation.layout.width

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.foundation.pager.HorizontalPager

import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.automirrored.filled.ArrowForward

import androidx.compose.material.icons.automirrored.filled.MenuBook

import androidx.compose.material.icons.automirrored.filled.VolumeUp

import androidx.compose.material.icons.filled.ArrowDropDown

import androidx.compose.material.icons.filled.Bookmark

import androidx.compose.material.icons.filled.BookmarkBorder

import androidx.compose.material.icons.filled.ContentCopy

import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Crop

import androidx.compose.material.icons.filled.CropFree

import androidx.compose.material.icons.filled.Fullscreen

import androidx.compose.material.icons.filled.FullscreenExit

import androidx.compose.material.icons.filled.Lock

import androidx.compose.material.icons.filled.Palette

import androidx.compose.material.icons.filled.Refresh

import androidx.compose.material.icons.filled.Search

import androidx.compose.material.icons.filled.Settings

import androidx.compose.material.icons.filled.Share

import android.app.Activity

import android.view.WindowManager

import androidx.compose.material.icons.filled.GraphicEq

import androidx.compose.material.icons.filled.KeyboardArrowDown

import androidx.compose.material.icons.filled.KeyboardArrowUp

import androidx.compose.material.icons.filled.Pause

import androidx.compose.material.icons.filled.PlayArrow

import androidx.compose.material.icons.filled.SubtitlesOff

import androidx.compose.material.icons.filled.Translate

import androidx.compose.material3.Slider

import androidx.compose.material3.SliderDefaults

import com.karianaquran.reader.service.QuranAudioItem

import com.karianaquran.reader.service.QuranAudioPlayer

import com.karianaquran.reader.service.QuranAudioState

import androidx.compose.material3.Button

import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Card

import androidx.compose.material3.CardDefaults

import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.OutlinedButton

import androidx.compose.material3.ModalBottomSheet

import androidx.compose.material3.rememberModalBottomSheetState

import androidx.compose.material3.Surface

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.runtime.DisposableEffect

import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableFloatStateOf

import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.produceState

import androidx.compose.runtime.remember

import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.platform.LocalClipboardManager

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.AnnotatedString

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import androidx.core.view.WindowCompat

import androidx.core.view.WindowInsetsCompat

import androidx.core.view.WindowInsetsControllerCompat

import com.karianaquran.reader.data.PageResult

import com.karianaquran.reader.data.ReadingMode

import com.karianaquran.reader.data.model.Publications

import com.karianaquran.reader.data.model.QuranPage

import com.karianaquran.reader.data.model.QuranRow

import com.karianaquran.reader.data.model.Verse

import com.karianaquran.reader.data.model.WordMarkType

import com.karianaquran.reader.data.model.WordRef

import com.karianaquran.reader.ui.AppViewModel

import com.karianaquran.reader.ui.Screen

import com.karianaquran.reader.ui.components.JuzMeta

import com.karianaquran.reader.ui.components.QuranMetadata

import com.karianaquran.reader.ui.components.SurahMeta

import com.karianaquran.reader.ui.components.toBn

import com.karianaquran.reader.ui.theme.Brand

import com.karianaquran.reader.ui.theme.LocalAppFonts

import com.karianaquran.reader.ui.theme.Palettes

import com.karianaquran.reader.ui.theme.ReaderPalette

import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun ReaderScreen(

    startPage: Int,

    vm: AppViewModel,

    modifier: Modifier = Modifier,

) {

    val fonts = LocalAppFonts.current

    val settings by vm.settings.collectAsState()

    val userData by vm.userData.collectAsState()

    val scope = rememberCoroutineScope()



    val isUnlocked = vm.isUnlocked

    val isClassic = settings.readingMode == ReadingMode.CLASSIC

    val classicOffset = if (isClassic) (Publications.CLASSIC_START_PAGE - 1) else 0

    val totalPages = Publications.totalPagesForMode(settings.readingMode)

    val freeLimit = vm.freePagesLimit(settings.readingMode)



    // গেস্টদের জন্য পেজ কাউন্ট কঠোরভাবে ফ্রি প্রিভিউ সীমার মধ্যে লক করা (মুসহাফে ১০ পৃষ্ঠা, ক্লাসিকে ২০ পৃষ্ঠা)

    // আনলকড ব্যবহারকারীদের জন্য মোট ৯৫৩ পৃষ্ঠা, এবং ফ্রি ব্যবহারকারীদের জন্য ১-১০ পৃষ্ঠা উন্মুক্ত + ১১তম পেজে পেওয়াল
    val modePageCount = if (isUnlocked) {
        totalPages
    } else {
        (freeLimit + 1)
    }



    val maxAllowedPage = if (isUnlocked) totalPages else freeLimit

    val effectiveStartPage = if (!isUnlocked && startPage > maxAllowedPage) {

        maxAllowedPage

    } else if (isClassic && startPage < Publications.CLASSIC_START_PAGE) {

        Publications.CLASSIC_START_PAGE

    } else {

        startPage

    }

    val initialPageIdx = (effectiveStartPage - 1 - classicOffset).coerceIn(0, (modePageCount - 1).coerceAtLeast(0))

    val pagerState = androidx.compose.runtime.key(settings.readingMode) {
        rememberPagerState(initialPage = initialPageIdx, pageCount = { modePageCount })
    }

    androidx.compose.runtime.LaunchedEffect(settings.readingMode, startPage) {
        val targetIdx = (effectiveStartPage - 1 - classicOffset).coerceIn(0, (modePageCount - 1).coerceAtLeast(0))
        if (pagerState.currentPage != targetIdx) {
            pagerState.scrollToPage(targetIdx)
        }
    }



    // ফুল-স্ক্রিন ইমার্সিভ মোড স্টেট

    var isImmersive by remember { mutableStateOf(false) }



    // ডায়ালগ ও শিট খোলার স্টেটসমূহ

    var showGoToPageDialog by remember { mutableStateOf(false) }

    var showSettingsSheet by remember { mutableStateOf(false) }
    var showDownloadSheet by remember { mutableStateOf(false) }

    var showSurahIndexSheet by remember { mutableStateOf(false) }

    var showEditionSheet by remember { mutableStateOf(false) }

    var showPaywallSheet by remember { mutableStateOf(false) }

    var selectedWordInfo by remember { mutableStateOf<SelectedWordInfo?>(null) }



    val context = LocalContext.current
    var tamperState by remember { mutableStateOf(SecurityGuard.checkReaderAccess(context)) }

    when (tamperState) {
        SecurityGuard.TamperState.RootedDevice -> {
            ReaderRootSecurityNotice(
                onBack = { vm.back() }
            )
            return
        }
        SecurityGuard.TamperState.SecurityRiskOrDevEnvironment -> {
            ReaderSecurityGuidanceNotice(
                onOpenDevSettings = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    runCatching {
                        context.startActivity(intent)
                    }.onFailure {
                        runCatching {
                            context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }
                    }
                },
                onRetry = {
                    tamperState = SecurityGuard.checkReaderAccess(context)
                },
                onBack = { vm.back() }
            )
            return
        }
        SecurityGuard.TamperState.Clean -> {
            // ডিভাইস সুরক্ষিত — স্বাভাবিক রিডার রেন্ডার হবে
        }
    }

    val audioState by QuranAudioPlayer.state.collectAsState()



    // ১. তিলাওয়াতের সময় স্ক্রিন অন রাখা (Keep Screen Awake)

    DisposableEffect(settings.keepScreenOn) {

        val activity = context as? Activity

        if (settings.keepScreenOn) {

            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        }

        onDispose {

            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        }

    }



    // ১.৫. ট্রু ওএস-লেভেল ফুলস্ক্রিন (স্ট্যাটাস বার ও নেভিগেশন বার হাইড/শো)

    LaunchedEffect(isImmersive) {

        val activity = context as? Activity ?: return@LaunchedEffect

        val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)

        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        if (isImmersive) {

            controller.hide(WindowInsetsCompat.Type.systemBars())

        } else {

            controller.show(WindowInsetsCompat.Type.systemBars())

        }

    }



    // ২. রিডার বন্ধ হলে অডিও তিলাওয়াত বন্ধ করা ও সিস্টেম বার ফিরিয়ে আনা

    DisposableEffect(Unit) {

        val activity = context as? Activity

        onDispose {

            QuranAudioPlayer.stop()

            activity?.let {

                WindowCompat.getInsetsController(it.window, it.window.decorView)

                    .show(WindowInsetsCompat.Type.systemBars())

            }

        }

    }



    val activePalette = remember(settings.themeId, settings.customBg, settings.customArabic, settings.customBangla) {

        Palettes.resolve(settings)

    }



    val currentPageNum = pagerState.currentPage + 1 + classicOffset

    val displayPageNum = if (isClassic) (pagerState.currentPage + 1) else currentPageNum

    val displayTotalPages = totalPages // ৯৫৩ পৃষ্ঠা
    val displayFreeLimit = freeLimit   // ১০ পৃষ্ঠা ফ্রি প্রিভিউ

    val minPageForMode = if (isClassic) Publications.CLASSIC_START_PAGE else 1

    val isBookmarked = userData.bookmarks.contains(currentPageNum)

    val currentSurah = remember(currentPageNum, settings.readingMode) {

        QuranMetadata.surahForPage(currentPageNum, settings.readingMode)

    }

    val currentJuz = remember(currentPageNum, settings.readingMode) {

        QuranMetadata.juzForPage(currentPageNum, settings.readingMode)

    }



    val switchReadingMode: (ReadingMode) -> Unit = { nextMode ->
        val oldMode = settings.readingMode
        if (nextMode != oldMode) {
            val mappedPage = when {
                oldMode == ReadingMode.CLASSIC && nextMode != ReadingMode.CLASSIC -> {
                    QuranMetadata.toDigitalPage(currentPageNum, ReadingMode.CLASSIC)
                }
                oldMode != ReadingMode.CLASSIC && nextMode == ReadingMode.CLASSIC -> {
                    QuranMetadata.targetPageForMode(currentPageNum, ReadingMode.CLASSIC)
                }
                else -> currentPageNum
            }
            vm.updateSettings { it.copy(readingMode = nextMode) }
            vm.openReader(mappedPage)
        }
    }




    // পেজ পরিবর্তন হলে সেটল ইভেন্ট পাঠানো

    LaunchedEffect(pagerState.currentPage, settings.readingMode) {

        vm.onPageSettled(currentPageNum)

    }



    Column(

        modifier = modifier

            .fillMaxSize()

            .background(activePalette.bg)

    ) {

        // ১. টপ নেভিগেশন বার (statusBarsPadding সহ, যাতে নচ বা স্ট্যাটাস বারে না কাটে)

        AnimatedVisibility(

            visible = !isImmersive,

            enter = slideInVertically { -it } + fadeIn(),

            exit = slideOutVertically { -it } + fadeOut(),

        ) {

            ReaderTopBar(

                pageNum = displayPageNum,

                surah = currentSurah,

                juz = currentJuz,

                isBookmarked = isBookmarked,

                showMeaning = settings.showMeaning,

                cropMargins = settings.cropMargins,

                readingMode = settings.readingMode,

                palette = activePalette,

                audioPlaying = audioState.isPlaying,

                onBack = { vm.back() },

                onOpenIndex = { showSurahIndexSheet = true },

                onToggleBookmark = { vm.toggleBookmark(currentPageNum) },

                onToggleMeaning = { vm.toggleMeaning() },

                onToggleCropMargins = { vm.updateSettings { it.copy(cropMargins = !it.cropMargins) } },

                onOpenEditions = { showEditionSheet = true },

                onToggleAudio = {
                    if (audioState.isPlaying) {
                        QuranAudioPlayer.togglePlayPause()
                    } else {
                        scope.launch {
                            when (settings.readingMode) {
                                ReadingMode.CLASSIC -> {
                                    val items = com.karianaquran.reader.data.model.ClassicVerseMapping.getVersesForClassicPage(currentPageNum)
                                    if (items.isNotEmpty()) {
                                        QuranAudioPlayer.playVersesQueue(items, 0)
                                    }
                                }
                                ReadingMode.PAGE, ReadingMode.AYAH, ReadingMode.HAFEZI -> {
                                    val res = vm.loadPage(currentPageNum, settings.readingMode)
                                    if (res is PageResult.Ready && res.page.verses.isNotEmpty()) {
                                        val items = res.page.verses.map { v ->
                                            val (s, a) = QuranAudioPlayer.parseSurahAndAyah(v.ayahRef, v.ayah, currentSurah.number)
                                            QuranAudioItem(s, a, currentSurah.nameBn, QuranAudioPlayer.currentReciter.value)
                                        }
                                        QuranAudioPlayer.playVersesQueue(items, 0)
                                    } else {
                                        val fallbackItems = com.karianaquran.reader.data.model.ClassicVerseMapping.getVersesForClassicPage(currentPageNum)
                                        if (fallbackItems.isNotEmpty()) {
                                            QuranAudioPlayer.playVersesQueue(fallbackItems, 0)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },

                onCycleTheme = { vm.cycleTheme() },

                onToggleFullscreen = { isImmersive = true },

                onOpenDownloadCenter = { showDownloadSheet = true },

                onOpenSettings = { showSettingsSheet = true }

            )

        }



        // বর্তমান পৃষ্ঠার পাশাপাশি থাকা পাতাগুলো ব্যাকগ্রাউন্ডে আগেই রেডি রাখা

        LaunchedEffect(pagerState.currentPage) {

            vm.prefetchAdjacent(pagerState.currentPage + 1)

        }



        // ২. মূল পেজার কনটেন্ট (weight 1f — টপ ও বটম বারের মাঝে নিখুঁত ফিট)

        Box(

            modifier = Modifier

                .weight(1f)

                .fillMaxWidth()

        ) {

            HorizontalPager(

                state = pagerState,

                modifier = Modifier.fillMaxSize(),

                userScrollEnabled = true,

                beyondViewportPageCount = 1,

                key = { pageIdx -> "${settings.readingMode}_$pageIdx" }

            ) { pageIdx ->

                val pageNum = pageIdx + 1 + classicOffset

                val isAllowed = vm.canRead(pageNum, settings.readingMode)



                if (!isAllowed) {

                    // পেওয়াল আনলক প্রম্পট

                    PaywallPagePrompt(

                        pageNum = pageNum,

                        readingMode = settings.readingMode,

                        onLogin = { vm.navigate(Screen.Login) },

                        onPay = { vm.navigate(Screen.Pay) }

                    )

                } else if (settings.readingMode == ReadingMode.CLASSIC) {
                    ClassicPdfReader(
                        pageNumber = pageNum,
                        themeId = settings.themeId,
                        cropMargins = settings.cropMargins,
                        showMeaning = settings.showMeaning,
                        customBg = settings.customBg,
                        customArabic = settings.customArabic,
                        customBangla = settings.customBangla,
                        isImmersive = isImmersive,
                        onToggleImmersive = { isImmersive = !isImmersive }
                    )
                } else {

                    // পেজ লোড স্টেট (ক্যাশড পেজ থাকলে ০ মিলিসেকেন্ডে ইনস্ট্যান্ট রেন্ডার)

                    val pageState by produceState<PageResult?>(

                        initialValue = vm.peekCachedPage(pageNum, settings.readingMode),

                        key1 = pageNum,

                        key2 = settings.readingMode

                    ) {

                        value = vm.loadPage(pageNum, settings.readingMode)

                    }



                    Box(modifier = Modifier.fillMaxSize()) {

                        val itemSurah = remember(pageNum, settings.readingMode) {

                            QuranMetadata.surahForPage(pageNum, settings.readingMode)

                        }

                        val itemJuz = remember(pageNum, settings.readingMode) {

                            QuranMetadata.juzForPage(pageNum, settings.readingMode)

                        }

                        val itemDisplayNum = if (isClassic) (pageIdx + 1) else pageNum



                        when (val res = pageState) {

                            null -> {

                                IslamicPageSkeletonLoader(

                                    pageNum = itemDisplayNum,

                                    surah = itemSurah,

                                    juz = itemJuz,

                                    palette = activePalette,

                                    readingMode = settings.readingMode

                                )

                            }

                            is PageResult.Ready -> {

                            when (settings.readingMode) {

                                ReadingMode.PAGE, ReadingMode.HAFEZI -> {

                                    MushafWebView(

                                        page = res.page,

                                        themeId = settings.themeId,

                                        showMeaning = settings.showMeaning,

                                        cropMargins = settings.cropMargins,

                                        isClassic = false,
                                        pageZoom = (settings.fontScale * 100).toInt().coerceIn(100, 350),

                                        arabicScale = settings.arabicScale,

                                        banglaScale = settings.banglaScale,

                                        customBg = settings.customBg?.let { "#%06X".format(0xFFFFFF and it) },

                                        customArabic = settings.customArabic?.let { "#%06X".format(0xFFFFFF and it) },

                                        customBangla = settings.customBangla?.let { "#%06X".format(0xFFFFFF and it) },

                                        wordMarks = userData.wordMarks,

                                        activeBookmark = userData.wordMarks.entries.firstOrNull { it.value == "bookmarked" }?.key,

                                        shimmerContent = {

                                            IslamicPageSkeletonLoader(

                                                pageNum = itemDisplayNum,

                                                surah = itemSurah,

                                                juz = itemJuz,

                                                palette = activePalette,

                                                readingMode = ReadingMode.PAGE

                                            )

                                        },

                                        onWordClicked = { wordId, arabic, translit, meaning, ayahRef, surahName ->

                                            val ref = WordRef.parse(wordId.removePrefix("p").replace("_r", "_").replace("_w", "_"))

                                                ?: WordRef(pageNum, 0, 0)

                                            val (sNum, aNum) = QuranAudioPlayer.parseSurahAndAyah(ayahRef, fallbackAyah = 1, fallbackSurah = currentSurah.number)

                                            val matchingVerse = res.page.verses.firstOrNull { it.ayah == aNum || it.ayahRef == ayahRef }
                                            val wIdxInAyah = if (matchingVerse != null && matchingVerse.words.isNotEmpty()) {
                                                val cleanArabic = arabic.trim().replace("\u0652", "").replace("\u064E", "").replace("\u064F", "").replace("\u0650", "").replace("\u0651", "")
                                                val found = matchingVerse.words.indexOfFirst { w ->
                                                    val cleanW = w.trim().replace("\u0652", "").replace("\u064E", "").replace("\u064F", "").replace("\u0650", "").replace("\u0651", "")
                                                    cleanW == cleanArabic || w.trim() == arabic.trim()
                                                }
                                                if (found >= 0) found + 1 else ((wordId.split("_w").lastOrNull()?.toIntOrNull() ?: 0) + 1)
                                            } else {
                                                (wordId.split("_w").lastOrNull()?.toIntOrNull() ?: 0) + 1
                                            }

                                            selectedWordInfo = SelectedWordInfo(

                                                ref = ref,

                                                arabic = arabic,

                                                translit = translit,

                                                meaning = meaning,

                                                ayahRef = ayahRef,

                                                surahName = surahName,

                                                nextWord = null,

                                                surahNumber = sNum,

                                                ayahNumber = aNum,

                                                wordIndexInAyah = wIdxInAyah

                                            )

                                        },

                                        onWordMarked = { wordId, color ->

                                            vm.setWordMarkColor(wordId, color)

                                        },

                                        onBookmarkSet = { wordId, _, isBm ->

                                            if (isBm) {

                                                vm.setWordMarkColor(wordId, "bookmarked")

                                            } else {

                                                vm.setWordMarkColor(wordId, "clear")

                                            }

                                        },

                                        onSwipeNext = {

                                            if (pagerState.currentPage < modePageCount - 1) {

                                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }

                                            }

                                        },

                                        onSwipePrev = {

                                            if (pagerState.currentPage > 0) {

                                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }

                                            }

                                        },

                                        onToggleImmersive = { isImmersive = !isImmersive }

                                    )

                                }


                                ReadingMode.AYAH -> {
                                    // ডিজিটাল সংস্করণ: নিরবচ্ছিন্ন সূরা/আয়াত প্রবাহ ও ব্যাচভিত্তিক নেভিগেশন
                                    AyahTafsirView(
                                        page = res.page,
                                        palette = activePalette,
                                        arabicScale = settings.arabicScale,
                                        banglaScale = settings.banglaScale,
                                        wordMarks = userData.wordMarks,
                                        audioState = audioState,
                                        pageSurahNumber = currentSurah.number,
                                        onWordClick = { rowIdx, wordIdx, arabic, translit, meaning, ayahRef, surahName ->
                                            val (sNum, aNum) = QuranAudioPlayer.parseSurahAndAyah(ayahRef, fallbackAyah = 1, fallbackSurah = currentSurah.number)
                                            selectedWordInfo = SelectedWordInfo(
                                                ref = WordRef(pageNum, rowIdx, wordIdx),
                                                arabic = arabic,
                                                translit = translit,
                                                meaning = meaning,
                                                ayahRef = ayahRef,
                                                surahName = surahName,
                                                nextWord = null,
                                                surahNumber = sNum,
                                                ayahNumber = aNum,
                                                wordIndexInAyah = wordIdx + 1
                                            )
                                        },
                                        onToggleImmersive = { isImmersive = !isImmersive },
                                        hasNextBatch = pagerState.currentPage < modePageCount - 1,
                                        hasPrevBatch = pagerState.currentPage > 0,
                                        onNextBatch = {
                                            if (pagerState.currentPage < modePageCount - 1) {
                                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                            }
                                        },
                                        onPrevBatch = {
                                            if (pagerState.currentPage > 0) {
                                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                                            }
                                        }
                                    )
                                }
                                ReadingMode.CLASSIC -> Unit
                            }
                        }

                        is PageResult.Failed -> {
                            PaywallPagePrompt(
                                pageNum = itemDisplayNum,
                                readingMode = settings.readingMode,
                                onLogin = { vm.navigate(Screen.Login) },
                                onPay = { vm.navigate(Screen.Pay) }
                            )
                        }

                    }

                    }

                }

            }



            // ফুলস্ক্রিন (Immersive) মোডে সূক্ষ্ম ফ্লোটিং এক্সিট পিল বাটন

            androidx.compose.animation.AnimatedVisibility(

                visible = isImmersive,

                enter = fadeIn(),

                exit = fadeOut(),

                modifier = Modifier

                    .align(Alignment.TopEnd)

                    .statusBarsPadding()

                    .padding(top = 8.dp, end = 10.dp)

            ) {

                Surface(

                    onClick = { isImmersive = false },

                    shape = RoundedCornerShape(20.dp),

                    color = Brand.Obsidian.copy(alpha = 0.72f),

                    border = androidx.compose.foundation.BorderStroke(0.8.dp, Brand.Gold.copy(alpha = 0.55f)),

                    shadowElevation = 4.dp

                ) {

                    Row(

                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector = Icons.Default.FullscreenExit,

                            contentDescription = "ফুলস্ক্রিন বন্ধ করুন",

                            tint = Brand.Gold,

                            modifier = Modifier.size(16.dp)

                        )

                        Spacer(Modifier.width(4.dp))

                        Text(

                            text = "পৃষ্ঠা ${currentPageNum.toBn()}",

                            fontFamily = fonts.kalpurush,

                            fontSize = 11.sp,

                            fontWeight = FontWeight.Bold,

                            color = Color.White

                        )

                    }

                }

            }

        }



        // ২.৫. ফ্ল্যাটিং অডিও প্লেয়ার বার (চলমান থাকলে)

        QuranAudioBar(

            state = audioState,

            modifier = Modifier.fillMaxWidth()

        )



        // ৩. ডাউন নেভিগেশন বার (Bottom Bar — navigationBarsPadding সহ)

        AnimatedVisibility(

            visible = !isImmersive,

            enter = slideInVertically { it } + fadeIn(),

            exit = slideOutVertically { it } + fadeOut(),

        ) {

            ReaderBottomBar(

                currentPage = displayPageNum,

                minPage = 1,

                totalPages = displayTotalPages,

                palette = activePalette,

                isUnlocked = isUnlocked,

                onPageChange = { targetDisplayPage ->

                    if (!isUnlocked && targetDisplayPage > displayFreeLimit) {

                        showPaywallSheet = true

                    } else {

                        val targetIdx = (targetDisplayPage - 1).coerceIn(0, modePageCount - 1)

                        scope.launch { pagerState.scrollToPage(targetIdx) }

                    }

                },

                onPrevPage = {

                    if (pagerState.currentPage > 0) {

                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }

                    }

                },

                onNextPage = {

                    if (!isUnlocked && displayPageNum >= displayFreeLimit) {

                        showPaywallSheet = true

                    } else if (pagerState.currentPage < modePageCount - 1) {

                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }

                    }

                },

                onOpenGoToPage = { showGoToPageDialog = true }

            )

        }

    }



    // নির্দিষ্ট পৃষ্ঠায় যান ডায়ালগ

    if (showGoToPageDialog) {

        GoToPageDialog(

            currentPage = displayPageNum,

            totalPages = displayTotalPages,

            readingMode = settings.readingMode,

            onNavigatePage = { targetDisplayPage ->

                if (!isUnlocked && targetDisplayPage > displayFreeLimit) {

                    showPaywallSheet = true

                    android.widget.Toast.makeText(context, "পৃষ্ঠা ${targetDisplayPage.toBn()} প্রিমিয়াম সংস্করণ। ফ্রি প্রিভিউতে ${displayFreeLimit.toBn()} পৃষ্ঠা পর্যন্ত উন্মুক্ত।", android.widget.Toast.LENGTH_SHORT).show()

                } else {

                    val targetIdx = (targetDisplayPage - 1).coerceIn(0, modePageCount - 1)

                    scope.launch { pagerState.scrollToPage(targetIdx) }

                }

            },

            onDismiss = { showGoToPageDialog = false }

        )

    }



    // ইন-রিডার ডাউনলোড সেন্টার ও অফলাইন কুরআন শিট
    if (showDownloadSheet) {
        val isSheetDark = settings.themeId == "dark"
        val isSheetEnglish = settings.appLanguage == "en"
        com.karianaquran.reader.ui.components.OfflineAssetManagerSheet(
            vm = vm,
            isDark = isSheetDark,
            isEnglish = isSheetEnglish,
            onDismiss = { showDownloadSheet = false }
        )
    }

    // ইন-রিডার সেটিংস ও থিম শিট

    if (showSettingsSheet) {

        val offlineState by vm.offlineDownloadState.collectAsState()

        SettingsSheet(

            settings = settings,

            isUnlocked = vm.isUnlocked,

            offlineState = offlineState,

            onStartOfflineDownload = { vm.startOfflineDownload() },

            onCancelOfflineDownload = { vm.cancelOfflineDownload() },

            onUpdateSettings = { vm.updateSettings(it) },

            onChangeReadingMode = { switchReadingMode(it) },

            onDismiss = { showSettingsSheet = false }

        )

    }



    // ইন-রিডার সূরা, পারা ও বুকমার্ক সূচিপত্র শিট

    if (showSurahIndexSheet) {

        SurahIndexSheet(

            currentPage = currentPageNum,

            bookmarks = userData.bookmarks,

            wordMarks = userData.wordMarks,

            readingMode = settings.readingMode,

            isUnlocked = vm.isUnlocked,

            onSelectPage = { targetPage ->

                val targetIdx = (targetPage - 1 - classicOffset).coerceIn(0, modePageCount - 1)

                scope.launch { pagerState.scrollToPage(targetIdx) }

            },

            onDismiss = { showSurahIndexSheet = false },

            onNavigateToPay = { vm.navigate(Screen.Pay) },

            onNavigateToLogin = { vm.navigate(Screen.Login) }

        )

    }



    // ইন-রিডার সংস্করণ পরিবর্তন শিট

    if (showEditionSheet) {

        com.karianaquran.reader.ui.overview.QuranEditionBottomSheet(

            onDismiss = { showEditionSheet = false },

            vm = vm,

            startPage = currentPageNum,

            onSelectEdition = { nextMode ->

                switchReadingMode(nextMode)

            }

        )

    }





    // ওয়ার্ড ডিটেইল শিট

    selectedWordInfo?.let { info ->

        val currentMarkType = WordMarkType.from(userData.wordMarks[info.ref.id])

        WordDetailSheet(

            info = info,

            currentMark = currentMarkType,

            onSetMark = { type ->

                vm.setWordMark(info.ref.id, type)

            },

            onDismiss = { selectedWordInfo = null }

        )

    }



    if (showPaywallSheet) {

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        val previewMax = if (isClassic) Publications.FREE_PREVIEW_CLASSIC_LAST_PAGE else Publications.FREE_PREVIEW_LAST_PAGE

        ModalBottomSheet(

            onDismissRequest = { showPaywallSheet = false },

            sheetState = sheetState,

            containerColor = if (activePalette.isDark) Color(0xFF0F241C) else Color.White,

            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

        ) {

            Column(

                modifier = Modifier

                    .fillMaxWidth()

                    .padding(horizontal = 24.dp)

                    .padding(bottom = 32.dp),

                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Surface(

                    shape = CircleShape,

                    color = Brand.Gold.copy(alpha = 0.15f),

                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Brand.Gold),

                    modifier = Modifier.size(64.dp)

                ) {

                    Box(contentAlignment = Alignment.Center) {

                        Icon(

                            imageVector = Icons.Default.Lock,

                            contentDescription = null,

                            tint = Brand.Gold,

                            modifier = Modifier.size(32.dp)

                        )

                    }

                }



                Spacer(Modifier.height(14.dp))



                Text(

                    text = "সম্পূর্ণ কুরআন আনলক করুন",

                    fontFamily = fonts.kalpurush,

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold,

                    color = if (activePalette.isDark) Color.White else Brand.EmeraldDark

                )



                Spacer(Modifier.height(6.dp))



                Text(

                    text = "আপনি ফ্রি মোড ১০ পৃষ্ঠার এক্সেস পেয়েছেন। এখন যদি আপনি সম্পূর্ণ কুরআনুল কারিম পড়তে চান সেক্ষেত্রে আমাদের আকর্ষণীয় প্যাকেজসমূহ থেকে ক্রয় করে সম্পূর্ণ ৩০ পারা আনলক করুন।",

                    fontFamily = fonts.kalpurush,

                    fontSize = 13.sp,

                    color = if (activePalette.isDark) Color(0xFF94A3B8) else Color(0xFF64748B),

                    textAlign = TextAlign.Center,

                    lineHeight = 18.sp

                )



                Spacer(Modifier.height(20.dp))



                Button(

                    onClick = {

                        showPaywallSheet = false

                        vm.navigate(Screen.Pay)

                    },

                    shape = RoundedCornerShape(14.dp),

                    colors = ButtonDefaults.buttonColors(containerColor = Brand.EmeraldDark),

                    modifier = Modifier.fillMaxWidth().height(48.dp)

                ) {

                    Text(

                        text = "বিকাশ / নগদ দিয়ে কিনুন (১,০০০ ৳)",

                        fontFamily = fonts.kalpurush,

                        fontSize = 14.5.sp,

                        fontWeight = FontWeight.Bold,

                        color = Color.White

                    )

                }



                Spacer(Modifier.height(10.dp))



                OutlinedButton(

                    onClick = {

                        showPaywallSheet = false

                        vm.navigate(Screen.Login)

                    },

                    shape = RoundedCornerShape(14.dp),

                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Brand.Gold),

                    modifier = Modifier.fillMaxWidth().height(48.dp)

                ) {

                    Text(

                        text = "পূর্বের লাইসেন্স দিয়ে লগইন করুন",

                        fontFamily = fonts.kalpurush,

                        fontSize = 14.sp,

                        fontWeight = FontWeight.Bold,

                        color = if (activePalette.isDark) Brand.Gold else Brand.EmeraldDark

                    )

                }

            }

        }

    }

}



/**

 * স্লিম প্রিমিয়াম টপ নেভিগেশন বার (statusBarsPadding সহ)

 * যাতে কোনো ক্যামেরা নচ বা স্ট্যাটাস বারে আইকন বা টেক্সট না কাটে

 */

@Composable

private fun ReaderTopBar(

    pageNum: Int,

    surah: SurahMeta,

    juz: JuzMeta,

    isBookmarked: Boolean,

    showMeaning: Boolean,

    cropMargins: Boolean,

    readingMode: ReadingMode,

    palette: com.karianaquran.reader.ui.theme.ThemePalette,

    audioPlaying: Boolean,

    onBack: () -> Unit,

    onOpenIndex: () -> Unit,

    onToggleBookmark: () -> Unit,

    onToggleMeaning: () -> Unit,

    onToggleCropMargins: () -> Unit,

    onOpenEditions: () -> Unit,

    onToggleAudio: () -> Unit,

    onCycleTheme: () -> Unit,

    onToggleFullscreen: () -> Unit,

    onOpenDownloadCenter: () -> Unit = {},

    onOpenSettings: () -> Unit,

    modifier: Modifier = Modifier,

) {

    val fonts = LocalAppFonts.current



    Surface(

        modifier = modifier.fillMaxWidth(),

        color = palette.bg.copy(alpha = 0.97f),

        shadowElevation = 4.dp

    ) {

        Column(

            modifier = Modifier

                .fillMaxWidth()

                .statusBarsPadding()

        ) {

            Row(

                modifier = Modifier

                    .fillMaxWidth()

                    .height(52.dp)

                    .padding(horizontal = 6.dp),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                // ১. বাম অংশ: হোম ব্যাক বাটন

                Row(

                    modifier = Modifier.weight(1f, fill = false),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {

                        Icon(

                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,

                            contentDescription = "হোমে ফিরুন",

                            tint = palette.bangla,

                            modifier = Modifier.size(21.dp)

                        )

                    }



                    Spacer(Modifier.width(3.dp))



                    // ২. প্রধান সূচিপত্র বাটন (বড় ও স্পষ্ট টেক্সটসহ)
                    val pillBg = if (palette.isDark) Brand.EmeraldDark.copy(alpha = 0.45f) else Brand.EmeraldDark.copy(alpha = 0.12f)
                    val pillBorder = if (palette.isDark) Brand.Gold.copy(alpha = 0.6f) else Brand.EmeraldDark.copy(alpha = 0.4f)
                    val pillText = if (palette.isDark) Color(0xFFFEF08A) else Brand.EmeraldDark

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = pillBg,
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, pillBorder),
                        modifier = Modifier.clickable(onClick = onOpenIndex)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "সূচিপত্র",
                                tint = pillText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "সূচিপত্র (${surah.nameBn})",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = pillText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = pillText.copy(alpha = 0.85f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // ৩. মধ্য অংশ: ছোট মার্জিত গোল ব্যাজ (Compact Circular Badge) এ পৃষ্ঠা নম্বর
                Surface(
                    shape = CircleShape,
                    color = if (palette.isDark) Color.White.copy(alpha = 0.12f) else Brand.EmeraldDark.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Brand.Gold.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable(onClick = onOpenEditions)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pageNum.toBn(),
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (palette.isDark) Color(0xFFFEF08A) else Brand.EmeraldDark
                        )
                    }
                }



                // ৪. ডান পাশের পরিপাটি ৪টি অপরিহার্য কন্ট্রোল (অডিও + বুকমার্ক + থিম + সেটিংস)

                Row(

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.spacedBy(1.dp)

                ) {

                    // অডিও তিলাওয়াত বাটন

                    IconButton(onClick = onToggleAudio, modifier = Modifier.size(34.dp)) {

                        Icon(

                            imageVector = if (audioPlaying) Icons.Default.GraphicEq else Icons.AutoMirrored.Filled.VolumeUp,

                            contentDescription = if (audioPlaying) "তিলাওয়াত বিরতি" else "তিলাওয়াত শুনুন",

                            tint = if (audioPlaying) Brand.Gold else palette.bangla.copy(alpha = 0.85f),

                            modifier = Modifier.size(19.dp)

                        )

                    }



                    // ৩টি সংস্করণের চাহিদা অনুযায়ী সুনির্দিষ্ট ও প্রাসঙ্গিক কুইক অ্যাকশন বাটন:
                    if (readingMode == ReadingMode.PAGE || readingMode == ReadingMode.CLASSIC || readingMode == ReadingMode.HAFEZI) {
                        // নতুন ও জনপ্রিয় উভয় সংস্করণে: বাংলা অর্থ শো/হাইড টগল বাটন
                        IconButton(onClick = onToggleMeaning, modifier = Modifier.size(34.dp)) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = if (showMeaning) "বাংলা অর্থ বন্ধ করুন" else "বাংলা অর্থ দেখুন",
                                tint = if (showMeaning) Brand.Gold else palette.bangla.copy(alpha = 0.40f),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    } else {
                        // ডিজিটাল সংস্করণে: বুকমার্ক বাটন
                        IconButton(onClick = onToggleBookmark, modifier = Modifier.size(34.dp)) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "বুকমার্ক",
                                tint = if (isBookmarked) Brand.Gold else palette.bangla.copy(alpha = 0.72f),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }



                    // দ্রুত থিম সাইকেল বাটন [🎨]

                    IconButton(onClick = onCycleTheme, modifier = Modifier.size(34.dp)) {

                        Icon(

                            imageVector = Icons.Default.Palette,

                            contentDescription = "থিম পরিবর্তন",

                            tint = palette.bangla.copy(alpha = 0.82f),

                            modifier = Modifier.size(19.dp)

                        )

                    }



                    // ডাউনলোড সেন্টার বাটন [📥]
                    IconButton(onClick = onOpenDownloadCenter, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "ডাউনলোড সেন্টার",
                            tint = palette.bangla.copy(alpha = 0.85f),
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // রিডার সেটিংস বাটন [⚙️] (যার ভেতর সংস্করণ, ক্রপ, জুম ও অন্যান্য সব কন্ট্রোল রয়েছে)

                    IconButton(onClick = onOpenSettings, modifier = Modifier.size(34.dp)) {

                        Icon(

                            imageVector = Icons.Default.Settings,

                            contentDescription = "রিডার সেটিংস",

                            tint = palette.bangla.copy(alpha = 0.9f),

                            modifier = Modifier.size(19.dp)

                        )

                    }

                }

            }

            HorizontalDivider(color = Brand.Gold.copy(alpha = 0.2f), thickness = 0.8.dp)

        }

    }

}



/**

 * স্লিম প্রিমিয়াম ডাউন নেভিগেশন বার (Bottom Bar — navigationBarsPadding সহ)

 * ১..৬১০ পৃষ্ঠা স্লাইডার ও পূর্ববর্তী/পরবর্তী বাটন

 */

@OptIn(ExperimentalMaterial3Api::class)

@Composable

private fun ReaderBottomBar(

    currentPage: Int,

    minPage: Int,

    totalPages: Int,

    palette: com.karianaquran.reader.ui.theme.ThemePalette,

    isUnlocked: Boolean = false,

    onPageChange: (Int) -> Unit,

    onPrevPage: () -> Unit,

    onNextPage: () -> Unit,

    onOpenGoToPage: () -> Unit,

    modifier: Modifier = Modifier,

) {

    val fonts = LocalAppFonts.current

    val safeMin = minPage.coerceIn(1, totalPages)

    var sliderValue by remember(currentPage, safeMin) {

        mutableFloatStateOf(currentPage.coerceIn(safeMin, totalPages).toFloat())

    }



    Surface(

        modifier = modifier.fillMaxWidth(),

        color = palette.bg.copy(alpha = 0.97f),

        shadowElevation = 8.dp

    ) {

        Column(

            modifier = Modifier

                .fillMaxWidth()

                .navigationBarsPadding()

                .padding(horizontal = 14.dp, vertical = 6.dp)

        ) {

            HorizontalDivider(color = Brand.Gold.copy(alpha = 0.2f), thickness = 0.8.dp)

            Spacer(Modifier.height(5.dp))



            // উচ্চ-কন্ট্রাস্ট বাটন ও টেক্সট কালার

            val isDark = palette.isDark

            val btnBg = if (isDark) Brand.EmeraldDark.copy(alpha = 0.45f) else Brand.EmeraldDark

            val btnBorder = if (isDark) Brand.Gold.copy(alpha = 0.45f) else Brand.EmeraldDark

            val btnTextColor = if (isDark) Color(0xFFFEF08A) else Color.White

            val disabledText = if (isDark) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.45f)



            // সারি ১: পৃষ্ঠা নেভিগেশন ও বাটনসমূহ

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                // পূর্ববর্তী পৃষ্ঠা বাটন (<)

                val canPrev = currentPage > safeMin

                Surface(

                    shape = RoundedCornerShape(10.dp),

                    color = if (canPrev) btnBg else btnBg.copy(alpha = 0.35f),

                    border = androidx.compose.foundation.BorderStroke(1.dp, if (canPrev) btnBorder else btnBorder.copy(alpha = 0.3f)),

                    modifier = Modifier.clickable(enabled = canPrev, onClick = onPrevPage)

                ) {

                    Row(

                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,

                            contentDescription = "পূর্ববর্তী পৃষ্ঠা",

                            tint = if (canPrev) btnTextColor else disabledText,

                            modifier = Modifier.size(15.dp)

                        )

                        Spacer(Modifier.width(4.dp))

                        Text(

                            text = "পূর্ববর্তী",

                            fontFamily = fonts.kalpurush,

                            fontSize = 11.5.sp,

                            fontWeight = FontWeight.Bold,

                            color = if (canPrev) btnTextColor else disabledText

                        )

                    }

                }



                // মাঝের পৃষ্ঠা নম্বর পিল (ট্যাপ করলে "নির্দিষ্ট পৃষ্ঠায় যান" ডায়ালগ ওপেন হবে)

                val pillContainer = if (isDark) Brand.Gold.copy(alpha = 0.15f) else Brand.EmeraldDark.copy(alpha = 0.08f)

                val pillBorderColor = if (isDark) Brand.Gold.copy(alpha = 0.45f) else Brand.EmeraldDark.copy(alpha = 0.3f)

                val pillTextColor = if (isDark) Color(0xFFFEF08A) else Brand.EmeraldDark



                Surface(

                    shape = RoundedCornerShape(16.dp),

                    color = pillContainer,

                    border = androidx.compose.foundation.BorderStroke(1.dp, pillBorderColor),

                    modifier = Modifier.clickable(onClick = onOpenGoToPage)

                ) {

                    Row(

                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        val span = (totalPages - safeMin + 1).coerceAtLeast(1)

                        val progressIndex = (currentPage - safeMin + 1).coerceIn(1, span)

                        val pct = ((progressIndex.toFloat() / span.toFloat()) * 100).toInt().coerceIn(0, 100)

                        Text(

                            text = "পৃষ্ঠা ${currentPage.toBn()} / ${totalPages.toBn()} (${pct.toBn()}%)",

                            fontFamily = fonts.kalpurush,

                            fontSize = 12.sp,

                            fontWeight = FontWeight.Bold,

                            color = pillTextColor

                        )

                        Spacer(Modifier.width(5.dp))

                        Icon(

                            imageVector = Icons.Default.Search,

                            contentDescription = "পৃষ্ঠায় যান",

                            tint = pillTextColor,

                            modifier = Modifier.size(14.dp)

                        )

                    }

                }



                // পরবর্তী পৃষ্ঠা বাটন (>)

                val canNext = (!isUnlocked && currentPage >= 10) || currentPage < totalPages

                Surface(

                    shape = RoundedCornerShape(10.dp),

                    color = if (canNext) btnBg else btnBg.copy(alpha = 0.35f),

                    border = androidx.compose.foundation.BorderStroke(1.dp, if (canNext) btnBorder else btnBorder.copy(alpha = 0.3f)),

                    modifier = Modifier.clickable(enabled = canNext, onClick = onNextPage)

                ) {

                    Row(

                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Text(

                            text = "পরবর্তী",

                            fontFamily = fonts.kalpurush,

                            fontSize = 11.5.sp,

                            fontWeight = FontWeight.Bold,

                            color = if (canNext) btnTextColor else disabledText

                        )

                        Spacer(Modifier.width(4.dp))

                        Icon(

                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,

                            contentDescription = "পরবর্তী পৃষ্ঠা",

                            tint = if (canNext) btnTextColor else disabledText,

                            modifier = Modifier.size(15.dp)

                        )

                    }

                }

            }



            // স্লাইডার

            Spacer(Modifier.height(3.dp))



            // কাস্টম স্লিম প্রিমিয়াম স্লাইডার

            val sliderAccent = if (isDark) Color(0xFFFEF08A) else palette.accent

            val rangeSpan = (totalPages - safeMin).coerceAtLeast(1).toFloat()

            val fraction = ((sliderValue - safeMin.toFloat()) / rangeSpan).coerceIn(0f, 1f)

            Slider(

                value = sliderValue,

                onValueChange = { sliderValue = it },

                onValueChangeFinished = { onPageChange(sliderValue.toInt().coerceIn(safeMin, totalPages)) },

                valueRange = safeMin.toFloat()..totalPages.toFloat(),

                steps = 0,

                modifier = Modifier

                    .fillMaxWidth()

                    .height(26.dp),

                thumb = {

                    Box(

                        modifier = Modifier

                            .size(18.dp)

                            .clip(CircleShape)

                            .background(sliderAccent)

                            .border(2.dp, palette.bg, CircleShape)

                    )

                },

                track = {

                    Box(

                        modifier = Modifier

                            .fillMaxWidth()

                            .height(5.dp)

                            .clip(RoundedCornerShape(3.dp))

                            .background(palette.bangla.copy(alpha = 0.2f))

                    ) {

                        Box(

                            modifier = Modifier

                                .fillMaxWidth(fraction)

                                .height(5.dp)

                                .clip(RoundedCornerShape(3.dp))

                                .background(sliderAccent)

                        )

                    }

                }

            )

        }

    }

}



/** পৃষ্ঠা ভিউ (Mushaf Page View) — পিঞ্চ-টু-জুম ও ইন্টারেক্টিভ শব্দসহ */

@Composable

private fun MushafPageView(

    page: QuranPage,

    palette: com.karianaquran.reader.ui.theme.ThemePalette,

    arabicScale: Float,

    banglaScale: Float,

    wordMarks: Map<String, String>,

    onWordClick: (rowIdx: Int, wordIdx: Int, arabic: String, translit: String, meaning: String, ayahRef: String, surahName: String, nextWord: String?) -> Unit,

    onToggleImmersive: () -> Unit,

) {

    val fonts = LocalAppFonts.current

    var zoomScale by remember { mutableFloatStateOf(1f) }

    var panOffsetX by remember { mutableFloatStateOf(0f) }

    var panOffsetY by remember { mutableFloatStateOf(0f) }



    Box(

        modifier = Modifier

            .fillMaxSize()

            .pointerInput(Unit) {

                detectTapGestures(

                    onTap = { onToggleImmersive() },

                    onDoubleTap = {

                        zoomScale = if (zoomScale > 1.5f) 1f else 2.2f

                        panOffsetX = 0f

                        panOffsetY = 0f

                    }

                )

            }

            .pointerInput(Unit) {

                detectTransformGestures { _, pan, zoom, _ ->

                    zoomScale = (zoomScale * zoom).coerceIn(1f, 4f)

                    if (zoomScale > 1f) {

                        panOffsetX += pan.x

                        panOffsetY += pan.y

                    } else {

                        panOffsetX = 0f

                        panOffsetY = 0f

                    }

                }

            }

            .graphicsLayer {

                scaleX = zoomScale

                scaleY = zoomScale

                translationX = panOffsetX

                translationY = panOffsetY

            }

            .padding(horizontal = 8.dp, vertical = 6.dp)

    ) {

        Surface(

            modifier = Modifier

                .fillMaxSize()

                .border(1.5.dp, Brand.Gold.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),

            shape = RoundedCornerShape(12.dp),

            color = palette.bg

        ) {

            Column(

                modifier = Modifier

                    .fillMaxSize()

                    .padding(horizontal = 10.dp, vertical = 8.dp)

            ) {

                // পৃষ্ঠার হেডার (সূরা নাম ও পারা নাম)

                Row(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(bottom = 6.dp),

                    horizontalArrangement = Arrangement.SpaceBetween,

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(

                        text = "পারা ${page.juz.toBn()}",

                        fontFamily = fonts.kalpurush,

                        fontSize = 12.sp,

                        fontWeight = FontWeight.Bold,

                        color = Brand.Gold

                    )

                    Text(

                        text = page.surahNameAr.ifBlank { page.surahNameBn },

                        fontFamily = fonts.qariana,

                        fontSize = 16.sp,

                        fontWeight = FontWeight.Bold,

                        color = Brand.Gold

                    )

                    Text(

                        text = page.surahNameBn,

                        fontFamily = fonts.kalpurush,

                        fontSize = 12.sp,

                        fontWeight = FontWeight.Bold,

                        color = Brand.Gold

                    )

                }



                HorizontalDivider(color = Brand.Gold.copy(alpha = 0.4f), thickness = 1.dp)

                Spacer(Modifier.height(6.dp))



                // পৃষ্ঠার প্রতিটি সারি

                Column(

                    modifier = Modifier

                        .weight(1f)

                        .verticalScroll(rememberScrollState()),

                    verticalArrangement = Arrangement.SpaceEvenly

                ) {

                    page.rows.forEachIndexed { rIdx, row ->

                        MushafRowView(

                            row = row,

                            rowIdx = rIdx,

                            pageNum = page.page,

                            palette = palette,

                            arabicScale = arabicScale,

                            banglaScale = banglaScale,

                            wordMarks = wordMarks,

                            onWordClick = { wIdx, arabic, translit, meaning, nextWord ->

                                onWordClick(rIdx, wIdx, arabic, translit, meaning, row.ayahRef, row.surahName, nextWord)

                            }

                        )

                    }

                }



                Spacer(Modifier.height(6.dp))

                HorizontalDivider(color = Brand.Gold.copy(alpha = 0.4f), thickness = 1.dp)



                // নিচের ফুটার: পৃষ্ঠা নং

                Box(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(top = 4.dp),

                    contentAlignment = Alignment.Center

                ) {

                    Text(

                        text = page.page.toBn(),

                        fontFamily = fonts.kalpurush,

                        fontSize = 13.sp,

                        fontWeight = FontWeight.Bold,

                        color = Brand.Gold

                    )

                }

            }

        }

    }

}



/** মুসহাফের একটি পূর্ণাঙ্গ সারি */

@OptIn(ExperimentalLayoutApi::class)

@Composable

private fun MushafRowView(

    row: QuranRow,

    rowIdx: Int,

    pageNum: Int,

    palette: com.karianaquran.reader.ui.theme.ThemePalette,

    arabicScale: Float,

    banglaScale: Float,

    wordMarks: Map<String, String>,

    onWordClick: (wIdx: Int, arabic: String, translit: String, meaning: String, nextWord: String?) -> Unit,

) {

    val fonts = LocalAppFonts.current

    val words = row.words.ifEmpty { row.arabic.split("\\s+".toRegex()).filter { it.isNotBlank() } }



    Column(

        modifier = Modifier

            .fillMaxWidth()

            .padding(vertical = 2.dp)

    ) {

        // আরবি শব্দসমূহ (ডান থেকে বামে)

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceAround,

            verticalAlignment = Alignment.CenterVertically

        ) {

            words.forEachIndexed { wIdx, wordText ->

                val wordRefId = "${pageNum}_${rowIdx}_$wIdx"

                val markKey = wordMarks[wordRefId]

                val markType = WordMarkType.from(markKey)

                val detail = row.wordDetails.getOrNull(wIdx)

                val translit = detail?.translit ?: row.translit

                val meaning = detail?.meaning ?: row.meaning

                val nextWord = words.getOrNull(wIdx + 1)



                Box(

                    modifier = Modifier

                        .clip(RoundedCornerShape(6.dp))

                        .background(

                            when (markType) {

                                WordMarkType.STUCK -> Color(0xFFEF4444).copy(alpha = 0.2f)

                                WordMarkType.TAJWEED -> Color(0xFFF59E0B).copy(alpha = 0.2f)

                                WordMarkType.REVISION -> Color(0xFF10B981).copy(alpha = 0.2f)

                                null -> Color.Transparent

                            }

                        )

                        .clickable(

                            indication = null,

                            interactionSource = remember { MutableInteractionSource() }

                        ) {

                            onWordClick(wIdx, wordText, translit, meaning, nextWord)

                        }

                        .padding(horizontal = 4.dp, vertical = 2.dp)

                ) {

                    Text(

                        text = wordText,

                        fontFamily = fonts.qariana,

                        fontSize = (22 * arabicScale).sp,

                        color = palette.arabic,

                        textAlign = TextAlign.Center

                    )

                }

            }

        }



        // বাংলা অর্থ বা উচ্চারণ সারি

        if (row.meaning.isNotBlank()) {

            Text(

                text = row.meaning,

                fontFamily = fonts.kalpurush,

                fontSize = (12 * banglaScale).sp,

                color = palette.bangla.copy(alpha = 0.85f),

                textAlign = TextAlign.Center,

                modifier = Modifier

                    .fillMaxWidth()

                    .padding(top = 1.dp)

            )

        }

    }

}



/** ডিজিটাল সংস্করণ ভিউ */

@OptIn(ExperimentalLayoutApi::class)

@Composable

private fun AyahTafsirView(

    page: QuranPage,

    palette: com.karianaquran.reader.ui.theme.ThemePalette,

    arabicScale: Float,

    banglaScale: Float,

    wordMarks: Map<String, String>,

    audioState: QuranAudioState,

    pageSurahNumber: Int,

    onWordClick: (rowIdx: Int, wordIdx: Int, arabic: String, translit: String, meaning: String, ayahRef: String, surahName: String) -> Unit,

    onToggleImmersive: () -> Unit,

    hasNextBatch: Boolean = true,

    hasPrevBatch: Boolean = true,

    onNextBatch: (() -> Unit)? = null,

    onPrevBatch: (() -> Unit)? = null,

) {

    val fonts = LocalAppFonts.current
    val listState = rememberLazyListState()

    // ডিজিটাল সংস্করণে ১ আয়াত = ১ একক পূর্ণাঙ্গ কার্ড: খণ্ডিত আয়াতগুলোকে একটিমাত্র পূর্ণ কার্ডে একীভূত করা
    val consolidatedVerses = remember(page.verses, pageSurahNumber) {
        val list = mutableListOf<Verse>()
        for (v in page.verses) {
            val (s, a) = QuranAudioPlayer.parseSurahAndAyah(v.ayahRef, v.ayah, pageSurahNumber)
            val last = list.lastOrNull()
            if (last != null) {
                val (ls, la) = QuranAudioPlayer.parseSurahAndAyah(last.ayahRef, last.ayah, pageSurahNumber)
                if (ls == s && la == a) {
                    val mergedArabic = (last.arabic.trim() + " " + v.arabic.trim()).trim()
                    val mergedTranslit = (last.transliteration.trim() + " " + v.transliteration.trim()).trim()
                    val mergedTranslation = (last.translation.trim() + " " + v.translation.trim()).trim()
                    val mergedWords = last.words + v.words
                    val mergedTafsir = last.tafsir ?: v.tafsir
                    list[list.lastIndex] = last.copy(
                        arabic = mergedArabic,
                        transliteration = mergedTranslit,
                        translation = mergedTranslation,
                        words = mergedWords,
                        tafsir = mergedTafsir
                    )
                    continue
                }
            }
            list.add(v)
        }
        list
    }

    // তিলাওয়াত চলার সময় সক্রিয় আয়াতটি স্ক্রিনে দৃশ্যমান রাখতে অটো-স্ক্রোল
    LaunchedEffect(audioState.currentItem?.surah, audioState.currentItem?.ayah, audioState.isPlaying) {
        if (audioState.isPlaying && audioState.currentItem != null) {
            val currSurah = audioState.currentItem?.surah
            val currAyah = audioState.currentItem?.ayah
            val targetIdx = consolidatedVerses.indexOfFirst { verse ->
                val (verseSurah, verseAyah) = QuranAudioPlayer.parseSurahAndAyah(verse.ayahRef, verse.ayah, pageSurahNumber)
                verseSurah == currSurah && verseAyah == currAyah
            }
            if (targetIdx >= 0) {
                val scrollIdx = if (consolidatedVerses.isEmpty()) targetIdx + 1 else targetIdx
                listState.animateScrollToItem(scrollIdx)
            }
        }
    }




    LazyColumn(
        state = listState,

        modifier = Modifier

            .fillMaxSize()

            .background(palette.bg)

            .padding(horizontal = 12.dp)

            .padding(top = 6.dp, bottom = 24.dp),

        contentPadding = PaddingValues(vertical = 8.dp),

        verticalArrangement = Arrangement.spacedBy(14.dp)

    ) {

        if (page.verses.isEmpty()) {

            item {

                Box(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(top = 40.dp, bottom = 40.dp),

                    contentAlignment = Alignment.Center

                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(

                            text = "পৃষ্ঠা ${page.page.toBn()}",

                            fontFamily = fonts.kalpurush,

                            fontSize = 18.sp,

                            fontWeight = FontWeight.Bold,

                            color = palette.bangla

                        )

                        Spacer(Modifier.height(8.dp))

                        Text(

                            text = "সূরা: ${page.surahNameBn} • পারা: ${page.juz.toBn()}",

                            fontFamily = fonts.kalpurush,

                            fontSize = 14.sp,

                            color = palette.bangla.copy(alpha = 0.85f)

                        )

                        Spacer(Modifier.height(14.dp))

                        Text(

                            text = "এই পৃষ্ঠার ভেক্টর ডিজাইন দেখতে উপরের টপ বার থেকে 'নতুন সংস্করণ' বা 'জনপ্রিয় সংস্করণ' বেছে নিন।",

                            fontFamily = fonts.kalpurush,

                            fontSize = 13.sp,

                            color = palette.bangla.copy(alpha = 0.7f),

                            textAlign = TextAlign.Center,

                            modifier = Modifier.padding(horizontal = 24.dp)

                        )

                    }

                }

            }

        }



        itemsIndexed(consolidatedVerses) { verseIdx, verse ->

            val (verseSurah, verseAyah) = QuranAudioPlayer.parseSurahAndAyah(verse.ayahRef, verse.ayah, pageSurahNumber)
            val rowSurahName = page.rows.getOrNull(verseIdx)?.surahName?.takeIf { it.isNotBlank() } ?: page.surahNameBn

            val isThisPlaying = audioState.isPlaying &&

                audioState.currentItem?.surah == verseSurah &&

                audioState.currentItem?.ayah == verseAyah



            var isTafsirExpanded by remember { mutableStateOf(false) }



            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(14.dp),

                colors = CardDefaults.cardColors(

                    containerColor = if (isThisPlaying) (if (palette.isDark) Color(0xFF132B20) else Color(0xFFE8F5EE)) else (if (palette.isDark) Color(0xFF141F1A) else Color(0xFFFFFFFF))

                ),

                elevation = CardDefaults.cardElevation(if (isThisPlaying) 6.dp else 2.dp),

                border = if (isThisPlaying) {

                    androidx.compose.foundation.BorderStroke(2.dp, Brand.Gold)

                } else {

                    androidx.compose.foundation.BorderStroke(1.dp, if (palette.isDark) Color(0xFF23362A) else Color(0xFFE2E8F0))

                }

            ) {

                Column(

                    modifier = Modifier

                        .fillMaxWidth()

                        .padding(14.dp)

                ) {

                    // আয়াত নম্বর হেডার ও অ্যাকশন বাটন

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        verticalAlignment = Alignment.CenterVertically,

                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Box(

                                modifier = Modifier

                                    .size(32.dp)

                                    .clip(CircleShape)

                                    .background(if (isThisPlaying) Brand.Gold else (if (palette.isDark) Color(0xFF1E3A2F) else Color(0xFFE2E8F0))),

                                contentAlignment = Alignment.Center

                            ) {

                                Text(

                                    text = verse.ayah.toBn(),

                                    fontFamily = fonts.kalpurush,

                                    fontSize = 12.5.sp,

                                    fontWeight = FontWeight.Bold,

                                    color = if (isThisPlaying) Color.Black else (if (palette.isDark) Color.White else Color(0xFF0F172A))

                                )

                            }

                            Spacer(Modifier.width(8.dp))

                            Text(

                                text = "আয়াত: ${verse.ayahRef.toBn()}",

                                fontFamily = fonts.kalpurush,

                                fontSize = 11.5.sp,

                                fontWeight = FontWeight.Medium,

                                color = palette.bangla.copy(alpha = 0.75f)
                            )

                            if (isThisPlaying) {
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Brand.Gold,
                                    shadowElevation = 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(Modifier.width(3.dp))
                                        Text(
                                            text = "তিলাওয়াত চলছে",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }

                        // তিলাওয়াত প্লে/পজ বাটন

                        Surface(

                            shape = RoundedCornerShape(12.dp),

                            color = if (isThisPlaying) Brand.Gold else Brand.EmeraldDark.copy(alpha = 0.12f),

                            border = androidx.compose.foundation.BorderStroke(

                                0.8.dp,

                                if (isThisPlaying) Brand.Gold else Brand.EmeraldDark.copy(alpha = 0.3f)

                            ),

                            modifier = Modifier.clickable {

                                if (isThisPlaying) {

                                    QuranAudioPlayer.togglePlayPause()

                                } else {

                                    val allItems = consolidatedVerses.map { v ->
                                        val (s, a) = QuranAudioPlayer.parseSurahAndAyah(v.ayahRef, v.ayah, pageSurahNumber)
                                        QuranAudioItem(s, a, page.surahNameBn)
                                    }
                                    val idx = consolidatedVerses.indexOf(verse).coerceAtLeast(0)
                                    QuranAudioPlayer.playVersesQueue(allItems, idx)

                                }

                            }

                        ) {

                            Row(

                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),

                                verticalAlignment = Alignment.CenterVertically

                            ) {

                                Icon(

                                    imageVector = if (isThisPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,

                                    contentDescription = "তিলাওয়াত",

                                    tint = if (isThisPlaying) Color.Black else Brand.Gold,

                                    modifier = Modifier.size(14.dp)

                                )

                                Spacer(Modifier.width(4.dp))

                                Text(

                                    text = if (isThisPlaying) "চলছে" else "শুনুন",

                                    fontFamily = fonts.kalpurush,

                                    fontSize = 11.sp,

                                    fontWeight = FontWeight.Bold,

                                    color = if (isThisPlaying) Color.Black else Brand.Gold

                                )

                            }

                        }

                    }



                    Spacer(Modifier.height(10.dp))



                    // আরবি সম্পূর্ণ আয়াত

                    Text(

                        text = verse.arabic,

                        fontFamily = fonts.qariana,

                        fontSize = (24 * arabicScale).sp,

                        color = palette.arabic,

                        lineHeight = (32 * arabicScale).sp,

                        textAlign = TextAlign.End,

                        modifier = Modifier.fillMaxWidth()

                    )



                    // শব্দে শব্দে ইন্টারেক্টিভ পিল (আরবি ডান-থেকে-বাম RTL ক্রমে)
                    if (verse.words.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        androidx.compose.runtime.CompositionLocalProvider(
                            androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
                        ) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                verse.words.forEachIndexed { wIdx, wordText ->
                                    val markKey = WordRef(page.page, verseIdx, wIdx).id
                                    val markRaw = wordMarks[markKey]
                                    val pillBg = when (markRaw) {
                                        "mistake" -> if (palette.isDark) Color(0xFF451A1A) else Color(0xFFFEE2E2)
                                        "tajweed" -> if (palette.isDark) Color(0xFF42320F) else Color(0xFFFEF3C7)
                                        "memorized" -> if (palette.isDark) Color(0xFF143825) else Color(0xFFDCFCE7)
                                        "bookmarked" -> if (palette.isDark) Color(0xFF3B2F13) else Color(0xFFFEF9C3)
                                        else -> if (isThisPlaying) (if (palette.isDark) Color(0xFF1B3B2C) else Color(0xFFDCFCE7)) else (if (palette.isDark) Color(0xFF1D2B24) else Color(0xFFF8FAFC))
                                    }
                                    val pillBorder = when (markRaw) {
                                        "mistake" -> Color(0xFFEF4444)
                                        "tajweed" -> Color(0xFFF59E0B)
                                        "memorized" -> Color(0xFF10B981)
                                        "bookmarked" -> Brand.Gold
                                        else -> if (isThisPlaying) Brand.Gold.copy(alpha = 0.6f) else (if (palette.isDark) Color(0xFF283B30) else Color(0xFFE2E8F0))
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = pillBg,
                                        border = androidx.compose.foundation.BorderStroke(
                                            if (markRaw != null) 1.1.dp else 0.6.dp,
                                            pillBorder
                                        ),
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .clickable {
                                                val (surahNum, ayahNum) = QuranAudioPlayer.parseSurahAndAyah(
                                                    verse.ayahRef,
                                                    verse.ayah,
                                                    pageSurahNumber
                                                )
                                                QuranAudioPlayer.playWordPronunciation(
                                                    surah = surahNum,
                                                    ayah = ayahNum,
                                                    wordIndex = wIdx + 1,
                                                    wordText = wordText
                                                )
                                                onWordClick(
                                                    verseIdx,
                                                    wIdx,
                                                    wordText,
                                                    verse.transliteration,
                                                    verse.translation,
                                                    verse.ayahRef,
                                                    rowSurahName
                                                )
                                            }
                                    ) {
                                        Text(
                                            text = wordText,
                                            fontFamily = fonts.qariana,
                                            fontSize = (18 * arabicScale).sp,
                                            color = palette.arabic,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // বাংলা উচ্চারণ (প্রতিটি আয়াতের বিশুদ্ধ বাংলা উচ্চারণ)
                    if (verse.transliteration.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (palette.isDark) Color(0xFF162E22) else Color(0xFFF0FDF4),
                            border = androidx.compose.foundation.BorderStroke(
                                0.6.dp,
                                if (palette.isDark) Color(0xFF244837) else Color(0xFFBBF7D0)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
                                Text(
                                    text = "উচ্চারণ:",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = (12 * banglaScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Brand.Emerald
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = verse.transliteration,
                                    fontFamily = fonts.kalpurush,
                                    fontSize = (13.5 * banglaScale).sp,
                                    color = palette.bangla,
                                    lineHeight = (20 * banglaScale).sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // বাংলা অনুবাদ

                    if (verse.translation.isNotBlank()) {

                        Surface(

                            shape = RoundedCornerShape(8.dp),

                            color = if (palette.isDark) Color(0xFF15221C) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (palette.isDark) Color(0xFF22382D) else Color(0xFFE2E8F0)),

                            modifier = Modifier.fillMaxWidth()

                        ) {

                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {

                                Text(

                                    text = "অনুবাদ:",

                                    fontFamily = fonts.kalpurush,

                                    fontSize = (12 * banglaScale).sp,

                                    fontWeight = FontWeight.Bold,

                                    color = Brand.Gold

                                )

                                Spacer(Modifier.height(2.dp))

                                Text(

                                    text = verse.translation,

                                    fontFamily = fonts.kalpurush,

                                    fontSize = (14 * banglaScale).sp,

                                    fontWeight = FontWeight.Medium,

                                    color = palette.bangla,

                                    lineHeight = (21 * banglaScale).sp

                                )

                            }

                        }

                    }



                    // তাফসীর নোট (এক্সপান্ডেবল)

                    if (!verse.tafsir.isNullOrBlank()) {

                        Spacer(Modifier.height(8.dp))

                        Surface(

                            modifier = Modifier

                                .fillMaxWidth()

                                .clickable { isTafsirExpanded = !isTafsirExpanded },

                            shape = RoundedCornerShape(8.dp),

                            color = if (palette.isDark) Color(0xFF2A2312) else Color(0xFFFEFCE8),

                            border = androidx.compose.foundation.BorderStroke(1.dp, if (palette.isDark) Color(0xFF4D3F20) else Color(0xFFFEF08A))

                        ) {

                            Column(Modifier.padding(10.dp)) {

                                Row(

                                    modifier = Modifier.fillMaxWidth(),

                                    verticalAlignment = Alignment.CenterVertically,

                                    horizontalArrangement = Arrangement.SpaceBetween

                                ) {

                                    Text(

                                        text = "তাফসীর ও প্রাসঙ্গিক তথ্য",

                                        fontFamily = fonts.kalpurush,

                                        fontSize = 11.5.sp,

                                        fontWeight = FontWeight.Bold,

                                        color = Brand.Gold

                                    )

                                    Icon(

                                        imageVector = if (isTafsirExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,

                                        contentDescription = null,

                                        tint = Brand.Gold,

                                        modifier = Modifier.size(16.dp)

                                    )

                                }

                                if (isTafsirExpanded) {

                                    Spacer(Modifier.height(6.dp))

                                    Text(

                                        text = verse.tafsir,

                                        fontFamily = fonts.kalpurush,

                                        fontSize = (12 * banglaScale).sp,

                                        color = palette.bangla.copy(alpha = 0.95f),

                                        lineHeight = (18 * banglaScale).sp

                                    )

                                }

                            }

                        }

                    }



                    // আয়াত কপি ও শেয়ার অ্যাকশন বার

                    Row(

                        modifier = Modifier

                            .fillMaxWidth()

                            .padding(top = 10.dp),

                        horizontalArrangement = Arrangement.End,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        val context = LocalContext.current

                        val clipboardManager = LocalClipboardManager.current



                        // কপি বাটন

                        Surface(

                            shape = RoundedCornerShape(8.dp),

                            color = palette.bangla.copy(alpha = 0.08f),

                            modifier = Modifier.clickable {

                                val copyText = "${verse.arabic}\n\nঅনুবাদ: ${verse.translation}\n(${page.surahNameBn}, আয়াত: ${verse.ayahRef.toBn()})"

                                clipboardManager.setText(AnnotatedString(copyText))

                                Toast.makeText(context, "আয়াত ও অর্থ কপি হয়েছে", Toast.LENGTH_SHORT).show()

                            }

                        ) {

                            Row(

                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),

                                verticalAlignment = Alignment.CenterVertically

                            ) {

                                Icon(

                                    imageVector = Icons.Default.ContentCopy,

                                    contentDescription = "কপি করুন",

                                    tint = Brand.Gold,

                                    modifier = Modifier.size(13.dp)

                                )

                                Spacer(Modifier.width(4.dp))

                                Text(

                                    text = "কপি",

                                    fontFamily = fonts.kalpurush,

                                    fontSize = 11.sp,

                                    color = palette.bangla.copy(alpha = 0.85f)

                                )

                            }

                        }



                        Spacer(Modifier.width(8.dp))



                        // শেয়ার বাটন

                        Surface(

                            shape = RoundedCornerShape(8.dp),

                            color = palette.bangla.copy(alpha = 0.08f),

                            modifier = Modifier.clickable {

                                val shareText = "${verse.arabic}\n\nঅনুবাদ: ${verse.translation}\n(${page.surahNameBn}, আয়াত: ${verse.ayahRef.toBn()})\n\n— ক্বারীয়ানা কুরআন অ্যাপ"

                                val sendIntent = Intent().apply {

                                    action = Intent.ACTION_SEND

                                    putExtra(Intent.EXTRA_TEXT, shareText)

                                    type = "text/plain"

                                }

                                context.startActivity(Intent.createChooser(sendIntent, "আয়াত শেয়ার করুন"))

                            }

                        ) {

                            Row(

                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),

                                verticalAlignment = Alignment.CenterVertically

                            ) {

                                Icon(

                                    imageVector = Icons.Default.Share,

                                    contentDescription = "শেয়ার করুন",

                                    tint = Brand.Gold,

                                    modifier = Modifier.size(13.dp)

                                )

                                Spacer(Modifier.width(4.dp))

                                Text(

                                    text = "শেয়ার",

                                    fontFamily = fonts.kalpurush,

                                    fontSize = 11.sp,

                                    color = palette.bangla.copy(alpha = 0.85f)

                                )

                            }

                        }

                    }

                }

            }

        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (palette.isDark) Color(0xFF14241B) else Color(0xFFF1F8F4)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Brand.Emerald.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasPrevBatch) {
                        OutlinedButton(
                            onClick = { onPrevBatch?.invoke() },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Brand.Emerald.copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Brand.Emerald, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("পূর্ববর্তী অংশ", fontFamily = fonts.kalpurush, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Brand.Emerald)
                        }
                    } else {
                        Spacer(Modifier.width(1.dp))
                    }

                    if (hasNextBatch) {
                        Button(
                            onClick = { onNextBatch?.invoke() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Brand.Emerald)
                        ) {
                            Text("পরবর্তী অংশ পড়ুন", fontFamily = fonts.kalpurush, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

    }

}



/** পেওয়াল প্রম্পট (পৃষ্ঠা ২০-এর পরে অপশন) */

@Composable

private fun PaywallPagePrompt(

    pageNum: Int,

    readingMode: ReadingMode = ReadingMode.CLASSIC,

    onLogin: () -> Unit,

    onPay: () -> Unit,

) {

    val fonts = LocalAppFonts.current

    val previewRangeText = "১–${Publications.freePagesForMode(readingMode).toBn()} পৃষ্ঠা"



    Box(

        modifier = Modifier

            .fillMaxSize()

            .padding(24.dp),

        contentAlignment = Alignment.Center

    ) {

        Surface(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(20.dp),

            color = Brand.Obsidian,

            shadowElevation = 8.dp

        ) {

            Column(

                modifier = Modifier

                    .fillMaxWidth()

                    .padding(24.dp),

                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Box(

                    modifier = Modifier

                        .size(56.dp)

                        .clip(CircleShape)

                        .background(Brand.Gold.copy(alpha = 0.15f)),

                    contentAlignment = Alignment.Center

                ) {

                    Icon(

                        imageVector = Icons.Default.Lock,

                        contentDescription = null,

                        tint = Brand.Gold,

                        modifier = Modifier.size(28.dp)

                    )

                }



                Spacer(Modifier.height(14.dp))



                Text(

                    text = "সম্পূর্ণ কুরআন আনলক করুন",

                    fontFamily = fonts.kalpurush,

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold,

                    color = Brand.Gold

                )



                Spacer(Modifier.height(8.dp))



                Text(

                    text = "আপনি ক্বারীয়ানা কুরআনের ফ্রি প্রিভিউ ($previewRangeText) সম্পন্ন করেছেন। পৃষ্ঠা ${pageNum.toBn()} সহ সম্পূর্ণ ৩০ পারা পড়তে অনুগ্রহ করে লগইন করুন অথবা আনলক করুন।",

                    fontFamily = fonts.kalpurush,

                    fontSize = 13.sp,

                    color = Color.White.copy(alpha = 0.8f),

                    textAlign = TextAlign.Center,

                    lineHeight = 20.sp

                )



                Spacer(Modifier.height(20.dp))



                Button(

                    onClick = onPay,

                    colors = ButtonDefaults.buttonColors(containerColor = Brand.EmeraldDark),

                    shape = RoundedCornerShape(12.dp),

                    modifier = Modifier.fillMaxWidth()

                ) {

                    Text(

                        text = "এখনই কিনুন ও আনলক করুন",

                        fontFamily = fonts.kalpurush,

                        fontSize = 15.sp,

                        fontWeight = FontWeight.Bold,

                        color = Color.White

                    )

                }



                Spacer(Modifier.height(10.dp))



                OutlinedButton(

                    onClick = onLogin,

                    shape = RoundedCornerShape(12.dp),

                    modifier = Modifier.fillMaxWidth()

                ) {

                    Text(

                        text = "ইতিমধ্যে কিনেছেন? লগইন করুন",

                        fontFamily = fonts.kalpurush,

                        fontSize = 14.sp,

                        fontWeight = FontWeight.Medium,

                        color = Brand.Gold

                    )

                }

            }

        }

    }

}



@Composable

private fun IslamicPageSkeletonLoader(

    pageNum: Int,

    surah: SurahMeta,

    juz: JuzMeta,

    palette: com.karianaquran.reader.ui.theme.ThemePalette,

    readingMode: ReadingMode = ReadingMode.CLASSIC,

) {

    val fonts = LocalAppFonts.current

    val infiniteTransition = rememberInfiniteTransition(label = "skeleton_shimmer")

    val shimmerTranslate by infiniteTransition.animateFloat(

        initialValue = -300f,

        targetValue = 900f,

        animationSpec = infiniteRepeatable(

            animation = tween(durationMillis = 1100, easing = LinearEasing),

            repeatMode = RepeatMode.Restart

        ),

        label = "shimmer_translate"

    )

    val shimmerBrush = Brush.linearGradient(

        colors = listOf(

            palette.arabic.copy(alpha = 0.08f),

            palette.accent.copy(alpha = 0.32f),

            palette.arabic.copy(alpha = 0.08f)

        ),

        start = Offset(shimmerTranslate, shimmerTranslate),

        end = Offset(shimmerTranslate + 250f, shimmerTranslate + 250f)

    )



    Box(

        modifier = Modifier

            .fillMaxSize()

            .background(palette.bg)

            .padding(horizontal = 14.dp, vertical = 10.dp),

        contentAlignment = Alignment.Center

    ) {

        Card(

            modifier = Modifier

                .fillMaxSize()

                .border(

                    1.2.dp,

                    palette.accent.copy(alpha = 0.4f),

                    RoundedCornerShape(8.dp)

                ),

            shape = RoundedCornerShape(8.dp),

            colors = CardDefaults.cardColors(containerColor = palette.bar)

        ) {

            Column(

                modifier = Modifier

                    .fillMaxSize()

                    .padding(14.dp),

                verticalArrangement = Arrangement.SpaceBetween,

                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                // ১. পৃষ্ঠা হেডার স্কেলিটন (পারা ও সূরা)

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.SpaceBetween

                ) {

                    Text(

                        text = "পারা ${juz.number.toBn()} • ${juz.nameBn}",

                        fontFamily = fonts.kalpurush,

                        fontSize = 12.sp,

                        fontWeight = FontWeight.SemiBold,

                        color = palette.bangla.copy(alpha = 0.75f)

                    )

                    Text(

                        text = surah.nameAr,

                        fontFamily = fonts.arabic,

                        fontSize = 14.sp,

                        fontWeight = FontWeight.Bold,

                        color = palette.arabic.copy(alpha = 0.85f)

                    )

                }



                Spacer(Modifier.height(6.dp))



                // ২. বিসমিল্লাহ ওয়াটারমার্ক ও গ্লোয়িং ডিভাইডার

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(

                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",

                        fontFamily = fonts.arabic,

                        fontSize = 20.sp,

                        fontWeight = FontWeight.Bold,

                        color = palette.arabic.copy(alpha = 0.65f),

                        textAlign = TextAlign.Center

                    )

                    Spacer(Modifier.height(4.dp))

                    Box(

                        modifier = Modifier

                            .width(140.dp)

                            .height(2.dp)

                            .background(shimmerBrush)

                    )

                }



                Spacer(Modifier.height(10.dp))



                // ৩. সংস্করণভিত্তিক বিশেষায়িত শিমার স্কেলিটন বার

                when (readingMode) {

                    ReadingMode.CLASSIC -> {

                        // ক্লাসিক প্রিন্ট ভিউ স্কেলিটন: ১১টি ফুল-উইডথ রেখা

                        Column(

                            modifier = Modifier

                                .fillMaxWidth()

                                .weight(1f),

                            verticalArrangement = Arrangement.SpaceEvenly

                        ) {

                            for (i in 1..11) {

                                val rowWidthFraction = if (i == 1 || i == 11) 0.85f else if (i % 2 == 0) 0.98f else 0.94f

                                Box(

                                    modifier = Modifier

                                        .fillMaxWidth(rowWidthFraction)

                                        .height(15.dp)

                                        .clip(RoundedCornerShape(3.dp))

                                        .background(shimmerBrush)

                                        .align(Alignment.CenterHorizontally)

                                )

                            }

                        }

                    }

                    ReadingMode.PAGE, ReadingMode.HAFEZI -> {

                        // মুসহাফ পৃষ্ঠা ভিউ স্কেলিটন: ৯টি রো (বাম পাশে অর্থ ও ডান পাশে আরবি খোপ)

                        Column(

                            modifier = Modifier

                                .fillMaxWidth()

                                .weight(1f),

                            verticalArrangement = Arrangement.SpaceEvenly

                        ) {

                            for (i in 1..9) {

                                Row(

                                    modifier = Modifier

                                        .fillMaxWidth()

                                        .height(26.dp),

                                    horizontalArrangement = Arrangement.spacedBy(6.dp),

                                    verticalAlignment = Alignment.CenterVertically

                                ) {

                                    Box(

                                        modifier = Modifier

                                            .weight(0.35f)

                                            .fillMaxHeight()

                                            .clip(RoundedCornerShape(4.dp))

                                            .background(shimmerBrush)

                                    )

                                    Box(

                                        modifier = Modifier

                                            .weight(0.65f)

                                            .fillMaxHeight()

                                            .clip(RoundedCornerShape(4.dp))

                                            .background(shimmerBrush)

                                    )

                                }

                            }

                        }

                    }

                    ReadingMode.AYAH -> {

                        // আয়াত ও তাফসীর ভিউ স্কেলিটন: ৩টি স্ট্যাকড আয়াত কার্ড

                        Column(

                            modifier = Modifier

                                .fillMaxWidth()

                                .weight(1f),

                            verticalArrangement = Arrangement.spacedBy(8.dp)

                        ) {

                            for (i in 1..3) {

                                Surface(

                                    modifier = Modifier

                                        .fillMaxWidth()

                                        .weight(1f),

                                    shape = RoundedCornerShape(10.dp),

                                    color = palette.bg.copy(alpha = 0.5f),

                                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.accent.copy(alpha = 0.2f))

                                ) {

                                    Column(

                                        modifier = Modifier

                                            .fillMaxSize()

                                            .padding(8.dp),

                                        verticalArrangement = Arrangement.SpaceEvenly

                                    ) {

                                        Row(

                                            modifier = Modifier.fillMaxWidth(),

                                            horizontalArrangement = Arrangement.SpaceBetween,

                                            verticalAlignment = Alignment.CenterVertically

                                        ) {

                                            Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(shimmerBrush))

                                            Box(modifier = Modifier.width(60.dp).height(10.dp).clip(RoundedCornerShape(3.dp)).background(shimmerBrush))

                                        }

                                        Box(modifier = Modifier.fillMaxWidth(0.95f).height(18.dp).clip(RoundedCornerShape(4.dp)).background(shimmerBrush))

                                        Box(modifier = Modifier.fillMaxWidth(0.80f).height(12.dp).clip(RoundedCornerShape(3.dp)).background(shimmerBrush))

                                    }

                                }

                            }

                        }

                    }

                }



                Spacer(Modifier.height(8.dp))



                // ৪. ফুটার: পৃষ্ঠা নম্বর ও সংস্করণ ব্যাজ

                Box(

                    modifier = Modifier

                        .clip(RoundedCornerShape(6.dp))

                        .background(palette.accent.copy(alpha = 0.16f))

                        .padding(horizontal = 12.dp, vertical = 4.dp)

                ) {

                    Text(

                        text = when (readingMode) {

                            ReadingMode.CLASSIC -> "ক্লাসিক ছাপা পৃষ্ঠা ${pageNum.toBn()} লোড হচ্ছে..."

                            ReadingMode.PAGE -> "নতুন সংস্করণ পৃষ্ঠা ${pageNum.toBn()} লোড হচ্ছে..."

                            ReadingMode.AYAH -> "ডিজিটাল সংস্করণ পৃষ্ঠা ${pageNum.toBn()} লোড হচ্ছে..."
                            ReadingMode.HAFEZI -> "হাফেজী সংস্করণ পৃষ্ঠা ${pageNum.toBn()} লোড হচ্ছে..."

                        },

                        fontFamily = fonts.kalpurush,

                        fontSize = 11.5.sp,

                        fontWeight = FontWeight.Bold,

                        color = palette.accent

                    )

                }

            }

        }

    }

}

/**
 * রুটেড ডিভাইসের জন্য স্বচ্ছ ও তথ্যবহুল সুরক্ষা নোটিশ
 * (কপিরাইটযুক্ত কুরআন রিডার উইথহোল্ড করা হয়, কিন্তু হোম বা অন্যান্য ফিচারে ফিরে যাওয়া যায়)
 */
@Composable
fun ReaderRootSecurityNotice(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brand.Obsidian)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "সুরক্ষা সতর্কতা: রুটেড ডিভাইস",
                fontFamily = fonts.kalpurush,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF4444),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "আপনার ডিভাইসে রুট এক্সেস (Root Access) সনাক্ত হয়েছে।\n\nক্বারীয়ানা কুরআনের মেধাস্বত্ব ও ডিজিটাল এসেট সুরক্ষার স্বার্থে রুটেড ডিভাইসে কপিরাইটযুক্ত সংরক্ষিত কুরআন পৃষ্ঠাগুলো লোড করা সম্ভব নয়।\n\nঅ্যাপের অন্যান্য সাধারণ ফিচারসমূহ (হোম, সূচিপত্র, নামায, তাসবীহ ও প্রকাশনা তালিকা) আপনি যেকোনো সময় ব্যবহার করতে পারবেন।",
                fontFamily = fonts.kalpurush,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Brand.Emerald),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "হোম পেজে ফিরে যান",
                    fontFamily = fonts.kalpurush,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * ডেভেলপার মোড / ডিবাগিং / টেম্পার ঝুঁকির জন্য জেনেরিক ব্যবহারকারী-বান্ধব গাইডেন্স নোটিশ
 * (অভ্যন্তরীণ এক্সপ্লয়েট বা টেলিমেট্রি স্ট্রিং ফাঁস না করে কেবল সহায়ক নির্দেশনা দেওয়া হয়)
 */
@Composable
fun ReaderSecurityGuidanceNotice(
    onOpenDevSettings: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brand.Obsidian)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Brand.Gold.copy(alpha = 0.15f))
                    .border(1.dp, Brand.Gold.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Brand.Gold,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "নিরাপত্তা সংক্রান্ত নির্দেশনা",
                fontFamily = fonts.kalpurush,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Brand.Gold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "পবিত্র কুরআনের সংরক্ষিত সংস্করণ সুরক্ষার জন্য অ্যাপটি একটি নিরাপদ এনভায়রনমেন্ট প্রত্যাশা করে।\n\nঅনুগ্রহ করে আপনার ডিভাইসের ডেভেলপার অপশন (Developer Options) বা ইউএসবি ডিবাগিং (USB Debugging) বন্ধ করুন অথবা একটি অফিসিয়াল ফিজিক্যাল ডিভাইসে অ্যাপটি চালু করুন।",
                fontFamily = fonts.kalpurush,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onOpenDevSettings,
                colors = ButtonDefaults.buttonColors(containerColor = Brand.Gold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "ডেভেলপার সেটিংস খুলুন",
                    fontFamily = fonts.kalpurush,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "পুনরায় যাচাই করুন",
                    fontFamily = fonts.kalpurush,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(44.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "হোম পেজে ফিরে যান",
                    fontFamily = fonts.kalpurush,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}



