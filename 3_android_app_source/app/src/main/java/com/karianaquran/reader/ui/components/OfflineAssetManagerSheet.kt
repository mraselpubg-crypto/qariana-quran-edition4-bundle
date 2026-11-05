package com.karianaquran.reader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.data.ReadingMode
import com.karianaquran.reader.data.remote.OfflineDownloadState
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.theme.LocalAppFonts

data class ParaDownloadItem(
    val paraNumber: Int,
    val nameBn: String,
    val surahsBn: String,
    val pageRange: String,
    val sizeMb: String,
    val isDownloaded: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineAssetManagerSheet(
    vm: AppViewModel,
    isDark: Boolean,
    isEnglish: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val offlineState by vm.offlineDownloadState.collectAsState()
    val isUnlocked = vm.isUnlocked
    val isAllOfflineReady = offlineState is OfflineDownloadState.Completed ||
        (offlineState is OfflineDownloadState.Idle && (offlineState as OfflineDownloadState.Idle).isReady)

    val downloadedCount = when (val s = offlineState) {
        is OfflineDownloadState.Idle -> s.downloadedCount
        is OfflineDownloadState.Downloading -> s.downloadedCount
        is OfflineDownloadState.Completed -> s.totalPages
        is OfflineDownloadState.Error -> s.downloadedCount
    }

    val currentReadingMode = vm.settings.collectAsState().value.readingMode
    var selectedEdition by remember(currentReadingMode) {
        mutableStateOf(currentReadingMode)
    }

    val currentEditionDownloadedCount = when (selectedEdition) {
        ReadingMode.CLASSIC -> vm.getClassicDownloadedCount()
        ReadingMode.PAGE, ReadingMode.HAFEZI -> downloadedCount
        ReadingMode.AYAH -> 6236
    }
    val currentEditionTotalPages = when (selectedEdition) {
        ReadingMode.CLASSIC -> 973
        ReadingMode.PAGE, ReadingMode.HAFEZI -> 611
        ReadingMode.AYAH -> 6236
    }
    val isCurrentEditionReady = when (selectedEdition) {
        ReadingMode.CLASSIC -> vm.isClassicFullyDownloaded()
        ReadingMode.PAGE, ReadingMode.HAFEZI -> isAllOfflineReady
        ReadingMode.AYAH -> true
    }
    val hasDeletableCurrentData = isUnlocked && (isCurrentEditionReady || currentEditionDownloadedCount > 10)

    // ডাউনলোড করা পারাসমূহের স্টেট
    var downloadedParas by remember(isUnlocked, isCurrentEditionReady, selectedEdition) {
        mutableStateOf(if (isUnlocked && isCurrentEditionReady) (1..30).toSet() else emptySet())
    }

    // ৩০টি পারার সমৃদ্ধ ডেটাসেট (নতুন সংস্করণ ৬১১ পৃষ্ঠা)
    val modernParaList = remember {
        listOf(
            ParaDownloadItem(1, "আলিফ লাম মীম", "সূরা আল-ফাতিহা - আল-বাকারাহ (১-১৪১)", "১ - ২১", "১.৮ MB", true),
            ParaDownloadItem(2, "সায়াকুলু", "সূরা আল-বাকারাহ (১৪২-২৫২)", "২২ - ৪১", "১.৯ MB", true),
            ParaDownloadItem(3, "তিলকার রুসুল", "সূরা আল-বাকারাহ (২৫৩) - আল-ইমরান (৯২)", "৪২ - ৬১", "১.৮ MB", false),
            ParaDownloadItem(4, "লান তানালু", "সূরা আল-ইমরান (৯৩) - আন-নিসা (২৩)", "৬২ - ৮১", "১.৭ MB", false),
            ParaDownloadItem(5, "ওয়াল মুহসানাতু", "সূরা আন-নিসা (২৪-১৪৭)", "৮২ - ১০১", "১.৮ MB", false),
            ParaDownloadItem(6, "লা ইউহিব্বুল্লাহু", "সূরা আন-নিসা (১৪৮) - আল-মায়িদাহ (৮১)", "১০২ - ১২১", "১.৮ MB", false),
            ParaDownloadItem(7, "ওয়া ইজা সামিয়ু", "সূরা আল-মায়িদাহ (৮২) - আল-আনআম (১১০)", "১২২ - ১৪১", "১.৯ MB", false),
            ParaDownloadItem(8, "ওয়া লাও আন্নানা", "সূরা আল-আনআম (১১১) - আল-আরাফ (৮৭)", "১৪২ - ১৬১", "১.৮ MB", false),
            ParaDownloadItem(9, "ক্বলাল মালাউ", "সূরা আল-আরাফ (৮৮) - আল-আনফাল (৪০)", "১৬২ - ১৮১", "১.৭ MB", false),
            ParaDownloadItem(10, "ওয়া’লামু", "সূরা আল-আনফাল (৪১) - আত-তাওবাহ (৯২)", "১৮২ - ২০১", "১.৮ MB", false),
            ParaDownloadItem(11, "ইয়া’তাজিরুনা", "সূরা আত-তাওবাহ (৯৩) - হুদ (৫)", "২০২ - ২২১", "১.৮ MB", false),
            ParaDownloadItem(12, "ওয়া মা মিন দাব্বাহ", "সূরা হুদ (৬) - ইউসুফ (৫২)", "২২২ - ২৪১", "১.৯ MB", false),
            ParaDownloadItem(13, "ওয়া মা উবাররিউ", "সূরা ইউসুফ (৫৩) - ইবরাহীম (৫২)", "২৪২ - ২৬১", "১.৮ MB", false),
            ParaDownloadItem(14, "রুবামা", "সূরা আল-হিজর (১) - আন-নাহল (১২৮)", "২৬২ - ২৮১", "১.৮ MB", false),
            ParaDownloadItem(15, "সুবহানাল্লাজি", "সূরা বনী ইসরাঈল (১) - আল-কাহফ (৭৪)", "২৮২ - ৩০১", "১.৯ MB", false),
            ParaDownloadItem(16, "ক্বলা আলাম", "সূরা আল-কাহফ (৭৫) - ত্ব-হা (১৩৫)", "৩০২ - ৩২১", "১.৮ MB", false),
            ParaDownloadItem(17, "ইক্বতারাবা লিন্নাস", "সূরা আল-আম্বিয়া (১) - আল-হাজ্জ (৭৮)", "৩২২ - ৩৪১", "১.৮ MB", false),
            ParaDownloadItem(18, "ক্বাদ আফলাহা", "সূরা আল-মুমিনুন (১) - আল-ফুরকান (২০)", "৩৪২ - ৩৬১", "১.৭ MB", false),
            ParaDownloadItem(19, "ওয়া ক্বলাল্লাজিনা", "সূরা আল-ফুরকান (২১) - আন-নামল (৫৫)", "৩৬২ - ৩৮১", "১.৮ MB", false),
            ParaDownloadItem(20, "আম্মান খালাক্বা", "সূরা আন-নামল (৫৬) - আল-আনকাবুত (৪৫)", "৩৮২ - ৪০১", "১.৮ MB", false),
            ParaDownloadItem(21, "উতলু মা উহিয়া", "সূরা আল-আনকাবুত (৪৬) - আল-আহযাব (৩০)", "৪০২ - ৪২১", "১.৮ MB", false),
            ParaDownloadItem(22, "ওয়া মাই ইয়াক্বনুত", "সূরা আল-আহযাব (৩১) - ইয়াসীন (২৭)", "৪২২ - ৪৪১", "১.৮ MB", false),
            ParaDownloadItem(23, "ওয়া মা লিয়া", "সূরা ইয়াসীন (২৮) - আয-যুমার (৩১)", "৪৪২ - ৪৬১", "১.৮ MB", false),
            ParaDownloadItem(24, "ফামান আজলামু", "সূরা আয-যুমার (৩২) - ফুসসিলাত (৪৬)", "৪৬২ - ৪৮১", "১.৭ MB", false),
            ParaDownloadItem(25, "ইলাইহি ইউরাদ্দু", "সূরা ফুসসিলাত (৪৭) - আল-জাসিয়াহ (৩৭)", "৪৮২ - ৫০১", "১.৮ MB", false),
            ParaDownloadItem(26, "হা-মীম", "সূরা আল-আহক্বাফ (১) - আয-যারিয়াত (৩০)", "৫০২ - ৫২১", "১.৮ MB", false),
            ParaDownloadItem(27, "ক্বলা ফামা খাতবুকুম", "সূরা আয-যারিয়াত (৩১) - আল-হাদীদ (২৯)", "৫২২ - ৫৪১", "১.৯ MB", false),
            ParaDownloadItem(28, "ক্বাদ সামিআল্লাহু", "সূরা আল-মুজাদালাহ (১) - আত-তাহরীম (১২)", "৫৪২ - ৫৬১", "১.৮ MB", false),
            ParaDownloadItem(29, "তাবারাকাল্লাজি", "সূরা আল-মুলক (১) - আল-মুরসালাত (৫০)", "৫৬২ - ৫৮১", "১.৮ MB", false),
            ParaDownloadItem(30, "আম্মা ইয়াতাসাআলুন", "সূরা আন-নাবা (১) - আন-নাস (৬)", "৫৮২ - ৬১২", "২.১ MB", true)
        )
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: প্যাকেজসমূহ, 1: পারাভিত্তিক তালিকা
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // সংস্করণ অনুযায়ী পারাসমূহের সঠিক পৃষ্ঠা রেঞ্জ
    val currentParaList = remember(selectedEdition) {
        if (selectedEdition == ReadingMode.CLASSIC) {
            QuranMetadata.juzList.mapIndexed { idx, juz ->
                val start = QuranMetadata.classicParaPages.getOrElse(idx) { 2 }
                val nextStart = QuranMetadata.classicParaPages.getOrNull(idx + 1) ?: 974
                val end = nextStart - 1
                ParaDownloadItem(
                    paraNumber = juz.number,
                    nameBn = juz.nameBn,
                    surahsBn = juz.surahsBn,
                    pageRange = "${start.toBn()} - ${end.toBn()}",
                    sizeMb = "২.৮ MB",
                    isDownloaded = if (isUnlocked && isCurrentEditionReady) true else idx < 1
                )
            }
        } else {
            modernParaList.map { item ->
                item.copy(isDownloaded = if (isUnlocked && isCurrentEditionReady) true else item.paraNumber <= 2)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF0A1610) else Color(0xFFFBFDFB),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // ১. আধুনিক হেডার
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF064E3B).copy(alpha = 0.12f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = Color(0xFF0F766E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isEnglish) "Offline Quran Manager" else "কুরআন অফলাইন ডাউনলোড ম্যানেজার",
                            fontFamily = fonts.kalpurush,
                            fontSize = 17.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF064E3B)
                        )
                        Text(
                            text = if (isEnglish) "100% offline access anywhere without internet" else "ইন্টারনেট ছাড়া নিরবচ্ছিন্ন তিলাওয়াত ও অডিও সুবিধা",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.5.sp,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (isDark) Color(0xFF16251E) else Color(0xFFF1F5F9), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "বন্ধ করুন",
                        tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ১.১ সংস্করণ নির্বাচক (Edition Selector Tabs: জনপ্রিয় ৯৭৩ পৃ. | নতুন ৬১১ পৃ. | ডিজিটাল)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (isDark) Color(0xFF16251E) else Color(0xFFF1F5F9),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val editions = listOf(
                        Triple(ReadingMode.CLASSIC, "জনপ্রিয় সংস্করণ", "৯৭৩ পৃষ্ঠা"),
                        Triple(ReadingMode.PAGE, "নতুন সংস্করণ", "৬১১ পৃষ্ঠা"),
                        Triple(ReadingMode.AYAH, "ডিজিটাল", "আয়াত ও অর্থ")
                    )

                    editions.forEach { (mode, name, sub) ->
                        val isSel = selectedEdition == mode
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedEdition = mode },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) Color(0xFF064E3B) else Color.Transparent,
                            shadowElevation = if (isSel) 2.dp else 0.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = name,
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color(0xFFFFDF79) else if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = sub,
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 9.5.sp,
                                    color = if (isSel) Color(0xFFA7F3D0) else if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // ২. ডিভাইস স্টোরেজ ও পারসিস্টেন্ট স্ট্যাটাস স্ট্রিপ
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = if (isDark) Color(0xFF13221A) else Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isCurrentEditionReady -> when (selectedEdition) {
                                        ReadingMode.CLASSIC -> "জনপ্রিয় সংস্করণ: ৯৭৩ পৃষ্ঠা অফলাইনে সংরক্ষিত (৭৮ MB)"
                                        ReadingMode.PAGE -> "নতুন সংস্করণ: ৬১১ পৃষ্ঠা অফলাইনে সংরক্ষিত (২৮ MB)"
                                        ReadingMode.AYAH -> "ডিজিটাল সংস্করণ: ৬,২৩৬টি আয়াত প্রস্তুত (৮ MB)"
                                        ReadingMode.HAFEZI -> "হাফেজী সংস্করণ: ৬১০ পৃষ্ঠা প্রস্তুত"
                                    }
                                    hasDeletableCurrentData -> "সংরক্ষিত: ${currentEditionDownloadedCount.toBn()} / ${currentEditionTotalPages.toBn()} পৃষ্ঠা"
                                    else -> "ফ্রি প্রিভিউ: ১০ পৃষ্ঠা সংরক্ষিত"
                                },
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF065F46),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = when {
                                    isCurrentEditionReady -> "অফলাইনে প্রস্তুত"
                                    !isUnlocked -> "প্রিভিউ মোড"
                                    hasDeletableCurrentData -> "আংশিক প্রস্তুত"
                                    else -> "ডাউনলোড হয়নি"
                                },
                                fontFamily = fonts.kalpurush,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669),
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        if (hasDeletableCurrentData) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showDeleteConfirm = true },
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEF4444).copy(alpha = 0.12f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "ডিলিট",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        text = "মুছুন",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }

                    if (offlineState is OfflineDownloadState.Downloading) {
                        val dl = offlineState as OfflineDownloadState.Downloading
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (dl.progressPercent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF059669),
                            trackColor = Color(0xFF10B981).copy(alpha = 0.2f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${dl.currentStage} (${dl.progressPercent.toBn()}%)",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF065F46)
                        )
                    }
                }
            }

            // ফ্রি ইউজার সতর্কবার্তা ব্যানার
            if (!isUnlocked) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7).copy(alpha = if (isDark) 0.14f else 1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "ফ্রি প্রিভিউ মোড: শুধুমাত্র ১ম-১০ম পৃষ্ঠা অফলাইনে পড়তে পারবেন। সম্পূর্ণ ৩০ পারা অফলাইন ডাউনলোডের জন্য মেম্বারশিপ প্যাকেজ প্রয়োজন।",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp,
                            color = if (isDark) Color(0xFFFCD34D) else Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ৩. সেগমেন্টেড ট্যাব (প্যাকেজসমূহ | পারাভিত্তিক)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xFF16251E) else Color(0xFFE2E8F0).copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = 0 },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedTab == 0) (if (isDark) Color(0xFF064E3B) else Color.White) else Color.Transparent,
                        shadowElevation = if (selectedTab == 0) 2.dp else 0.dp
                    ) {
                        Text(
                            text = if (isEnglish) "Subscription Packages" else "সাবস্ক্রিপশন প্যাকেজসমূহ",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) (if (isDark) Color(0xFF86EFAC) else Color(0xFF064E3B)) else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = 1 },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedTab == 1) (if (isDark) Color(0xFF064E3B) else Color.White) else Color.Transparent,
                        shadowElevation = if (selectedTab == 1) 2.dp else 0.dp
                    ) {
                        Text(
                            text = if (isEnglish) "Para by Para (1-30)" else "পারাভিত্তিক তালিকা (১-৩০)",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) (if (isDark) Color(0xFF86EFAC) else Color(0xFF064E3B)) else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ৪. কনটেন্ট সেকশন
            if (selectedTab == 0) {
                // ---- সাবস্ক্রিপশন প্যাকেজসমূহ ----
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isDark) Color(0xFF11261B) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD4AF37).copy(alpha = 0.7f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFD4AF37).copy(alpha = 0.18f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = null,
                                                tint = if (isDark) Color(0xFFFFE082) else Color(0xFF92400E),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = when (selectedEdition) {
                                                    ReadingMode.CLASSIC -> "জনপ্রিয় সংস্করণ (৯৭৩ পৃষ্ঠা) সম্পূর্ণ প্যাক"
                                                    ReadingMode.PAGE -> "নতুন ভেক্টর সংস্করণ (৬১১ পৃষ্ঠা) সম্পূর্ণ প্যাক"
                                                    ReadingMode.AYAH -> "ডিজিটাল সংস্করণ (৬,২৩৬ আয়াত ও তাফসীর)"
                                                    ReadingMode.HAFEZI -> "হাফেজী সংস্করণ (১৫ ও ১৪ লাইন)"
                                                },
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDark) Color(0xFFFFE082) else Color(0xFF92400E)
                                            )
                                        }
                                    }

                                    Text(
                                        text = when (selectedEdition) {
                                            ReadingMode.CLASSIC -> "৭৮.২ MB"
                                            ReadingMode.PAGE -> "২৮.৪ MB"
                                            ReadingMode.AYAH -> "৮.২ MB"
                                            ReadingMode.HAFEZI -> "২৮.৪ MB"
                                        },
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B)
                                    )
                                }

                                Spacer(Modifier.height(10.dp))

                                Text(
                                    text = when (selectedEdition) {
                                        ReadingMode.CLASSIC -> "ক্বারীয়ানা জনপ্রিয় ক্লাসিক সম্পূর্ণ ৩০ পারা (৯৭৩ পৃষ্ঠা)"
                                        ReadingMode.PAGE -> "ক্বারীয়ানা নতুন ভেক্টর সম্পূর্ণ ৩০ পারা (৬১১ পৃষ্ঠা)"
                                        ReadingMode.AYAH -> "ক্বারীয়ানা ডিজিটাল কুরআন (শব্দার্থ ও তাফসীর)"
                                        ReadingMode.HAFEZI -> "ক্বারীয়ানা হাফেজী ভেক্টর কুরআন (১৫ ও ১৪ লাইন)"
                                    },
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = when (selectedEdition) {
                                        ReadingMode.CLASSIC -> "মুদ্রিত ক্বারীয়ানা ইনডিজাইন বইয়ের মূল ৯৭৩ পৃষ্ঠা। উচ্চারণ, শব্দে শব্দে অর্থ ও ৭-রঙিন তাজবীদসহ সকল পৃষ্ঠা ১০০% অফলাইনে দ্রুত লোড হবে।"
                                        ReadingMode.PAGE -> "আন্তর্জাতিক ১৫-লাইনের ক্রিস্টাল ক্লিয়ার ভেক্টর মুসহাফ পৃষ্ঠা ও তাজবীদ ফন্টসহ সম্পূর্ণ পৃষ্ঠা অফলাইনে সংরক্ষণ।"
                                        ReadingMode.AYAH -> "১১৪টি সূরার ৬,২৩৬টি আয়াতের পূর্ণাঙ্গ আরবি, উচ্চারণ, শব্দে শব্দে বাংলা অর্থ ও তাফসীর ডাটাবেস।"
                                        ReadingMode.HAFEZI -> "ঐতিহ্যবাহী ১৫ ও ১৪ লাইন হাফেজী পৃষ্ঠা বিন্যাস, বিশুদ্ধ উচ্চারণ ও রঙিন তাজবীদসহ সম্পূর্ণ পৃষ্ঠা অফলাইনে সংরক্ষণ।"
                                    },
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                                )

                                Spacer(Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        if (!isUnlocked) {
                                            onDismiss()
                                            vm.navigate(com.karianaquran.reader.ui.Screen.MembershipPackages)
                                        } else if (!isCurrentEditionReady) {
                                            downloadedParas = (1..30).toSet()
                                            vm.startOfflineDownload(selectedEdition)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isUnlocked) Color(0xFF064E3B) else Color(0xFF9CA3AF)
                                    )
                                ) {
                                    Icon(
                                        imageVector = when {
                                            !isUnlocked -> Icons.Default.Lock
                                            isCurrentEditionReady -> Icons.Default.CheckCircle
                                            else -> Icons.Default.Download
                                        },
                                        contentDescription = null,
                                        tint = Color(0xFFFFDF79),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = when {
                                            !isUnlocked -> "মেম্বারশিপ নিয়ে সম্পূর্ণ অফলাইন আনলক করুন"
                                            isCurrentEditionReady -> "সম্পূর্ণ বান্ডেল অফলাইনে সংরক্ষিত রয়েছে"
                                            selectedEdition == ReadingMode.CLASSIC -> "১-ট্যাপে জনপ্রিয় ৯৭৩ পৃষ্ঠা ডাউনলোড করুন"
                                            selectedEdition == ReadingMode.PAGE -> "১-ট্যাপে নতুন ৬১১ পৃষ্ঠা ডাউনলোড করুন"
                                            else -> "১-ট্যাপে ডিজিটাল ডাটাবেস সংরক্ষণ করুন"
                                        },
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // ২. অতিরিক্ত প্যাকেজ ইনফো কার্ড
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = if (isDark) Color(0xFF13221A) else Color(0xFFF8FAF9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MenuBook,
                                                contentDescription = null,
                                                tint = Color(0xFF059669),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "উচ্চগতির অফলাইন সুবিধা",
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF059669)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "১০০% ভল্ট সিকিউরড",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B)
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "একবার ডাউনলোড, আজীবন অফলাইন রিডিং",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF1E293B)
                                )

                                Text(
                                    text = "AES-256 এনক্রিপ্টেড অফলাইন ভল্ট প্রযুক্তির মাধ্যমে যেকোনো স্থানে ইন্টারনেট ছাড়াই পলকের মধ্যে পৃষ্ঠা ওপেন হবে।",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 11.5.sp,
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            } else {
                // ---- পারাভিত্তিক তালিকা (১-৩০) ----
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "পারাভিত্তিক অফলাইন প্যাক (পারা ০১ – ৩০)",
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFF86EFAC) else Color(0xFF064E3B)
                            )
                            TextButton(
                                onClick = {
                                    if (!isUnlocked) {
                                        onDismiss()
                                        vm.navigate(com.karianaquran.reader.ui.Screen.MembershipPackages)
                                    } else if (!isCurrentEditionReady) {
                                        downloadedParas = (1..30).toSet()
                                        vm.startOfflineDownload(selectedEdition)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (!isUnlocked) Icons.Default.Lock else if (isCurrentEditionReady) Icons.Default.CheckCircle else Icons.Default.Download,
                                    contentDescription = null,
                                    tint = Color(0xFF0F766E),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = when {
                                        !isUnlocked -> "আনলক করে সব ডাউনলোড"
                                        isCurrentEditionReady -> "সব পারা সংরক্ষিত"
                                        else -> "সব ডাউনলোড করুন"
                                    },
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F766E)
                                )
                            }
                        }
                    }

                    items(currentParaList) { item ->
                        val isDownloaded = isUnlocked && (downloadedParas.contains(item.paraNumber) || isCurrentEditionReady)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = if (isDark) Color(0xFF13221A) else Color(0xFFF8FAF9),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDownloaded) Color(0xFF10B981).copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isDownloaded) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFF064E3B).copy(alpha = 0.08f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = item.paraNumber.toBn().padStart(2, '০'),
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDownloaded) Color(0xFF059669) else Color(0xFF064E3B)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "পারা ${item.paraNumber.toBn()} • ${item.nameBn}",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else Color(0xFF1E293B),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${item.surahsBn} • পৃষ্ঠা ${item.pageRange}",
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 11.sp,
                                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(Modifier.width(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                if (!isUnlocked) {
                                                    onDismiss()
                                                    vm.navigate(com.karianaquran.reader.ui.Screen.MembershipPackages)
                                                } else if (!isDownloaded) {
                                                    downloadedParas = downloadedParas + item.paraNumber
                                                    vm.startOfflineDownload(selectedEdition)
                                                }
                                            },
                                        shape = RoundedCornerShape(8.dp),
                                        color = when {
                                            !isUnlocked -> Color(0xFFFEF3C7)
                                            isDownloaded -> Color(0xFF10B981).copy(alpha = 0.15f)
                                            else -> Color(0xFF064E3B).copy(alpha = 0.08f)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = when {
                                                    !isUnlocked -> Icons.Default.Lock
                                                    isDownloaded -> Icons.Default.Check
                                                    else -> Icons.Default.Download
                                                },
                                                contentDescription = null,
                                                tint = when {
                                                    !isUnlocked -> Color(0xFFB45309)
                                                    isDownloaded -> Color(0xFF059669)
                                                    else -> Color(0xFF064E3B)
                                                },
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = when {
                                                    !isUnlocked -> if (item.paraNumber == 1) "প্রিভিউ ১–১০" else "লকড"
                                                    isDownloaded -> "রেডি"
                                                    else -> item.sizeMb
                                                },
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    !isUnlocked -> Color(0xFFB45309)
                                                    isDownloaded -> Color(0xFF059669)
                                                    else -> Color(0xFF064E3B)
                                                },
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    }

                                    if (isDownloaded) {
                                        Surface(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { showDeleteConfirm = true },
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFEF4444).copy(alpha = 0.12f)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "ডিলিট",
                                                    tint = Color(0xFFDC2626),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ডিলিট কনফার্মেশন ডায়ালগ
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = if (isEnglish) "Delete Offline Data?" else "অফলাইন ডেটা মুছে ফেলবেন?",
                    fontFamily = fonts.kalpurush,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = if (isEnglish)
                        "All downloaded pages for this edition will be removed from the encrypted vault, except the free preview."
                    else
                        "ফ্রি প্রিভিউ রেখে নির্বাচিত সংস্করণের ডাউনলোড করা সকল পৃষ্ঠা এনক্রিপ্টেড ভল্ট থেকে মুছে ফেলা হবে।",
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        if (selectedEdition == ReadingMode.CLASSIC) {
                            vm.deleteClassicPages(preservePreview = true)
                        } else {
                            vm.deleteOfflinePages(preservePreview = true)
                        }
                        downloadedParas = emptySet()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text(
                        text = if (isEnglish) "Delete" else "ডিলিট করুন",
                        fontFamily = fonts.kalpurush,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(
                        text = if (isEnglish) "Cancel" else "বাতিল",
                        fontFamily = fonts.kalpurush
                    )
                }
            }
        )
    }
}
