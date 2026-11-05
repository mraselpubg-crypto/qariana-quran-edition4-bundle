package com.karianaquran.reader.ui.reader



import androidx.compose.foundation.background

import androidx.compose.foundation.border

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.ExperimentalLayoutApi

import androidx.compose.foundation.layout.FlowRow

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.size

import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info

import androidx.compose.material.icons.filled.Close

import androidx.compose.material.icons.filled.FormatSize

import androidx.compose.material.icons.filled.Palette

import androidx.compose.material.icons.filled.Refresh

import androidx.compose.material.icons.filled.ViewDay

import androidx.compose.material3.DividerDefaults

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Slider

import androidx.compose.material3.SliderDefaults

import androidx.compose.material3.Surface

import androidx.compose.material3.Switch

import androidx.compose.material3.SwitchDefaults

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp

import com.karianaquran.reader.data.ReaderSettings

import com.karianaquran.reader.data.ReadingMode

import com.karianaquran.reader.ui.components.SecureBottomSheet

import com.karianaquran.reader.ui.components.toBn

import com.karianaquran.reader.ui.theme.Brand

import com.karianaquran.reader.ui.theme.LocalAppFonts

import com.karianaquran.reader.ui.theme.Palettes



@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

@Composable

fun SettingsSheet(

    settings: ReaderSettings,

    isUnlocked: Boolean = false,

    offlineState: com.karianaquran.reader.data.remote.OfflineDownloadState? = null,

    onStartOfflineDownload: (() -> Unit)? = null,

    onCancelOfflineDownload: (() -> Unit)? = null,

    onUpdateSettings: ((ReaderSettings) -> ReaderSettings) -> Unit,

    onDismiss: () -> Unit,

    onChangeReadingMode: ((ReadingMode) -> Unit)? = null,

) {

    val fonts = LocalAppFonts.current

    val switchMode: (ReadingMode) -> Unit = { targetMode ->

        if (onChangeReadingMode != null) {

            onChangeReadingMode(targetMode)

        } else {

            onUpdateSettings { it.copy(readingMode = targetMode) }

        }

    }



    SecureBottomSheet(onDismiss = onDismiss) {

        Column(

            modifier = Modifier

                .fillMaxWidth()

                .padding(horizontal = 20.dp, vertical = 6.dp)

                .padding(bottom = 24.dp)

                .verticalScroll(rememberScrollState())

        ) {

            // হেডার

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(

                        imageVector = Icons.Default.Palette,

                        contentDescription = null,

                        tint = Brand.EmeraldDark

                    )

                    Spacer(Modifier.width(8.dp))

                    Text(

                        text = "রিডার সেটিংস ও থিম",

                        fontFamily = fonts.kalpurush,

                        fontSize = 18.sp,

                        fontWeight = FontWeight.Bold,

                        color = MaterialTheme.colorScheme.onSurface

                    )

                }

                IconButton(onClick = onDismiss) {

                    Icon(

                        imageVector = Icons.Default.Close,

                        contentDescription = "বন্ধ করুন",

                        tint = MaterialTheme.colorScheme.onSurfaceVariant

                    )

                }

            }



            Spacer(Modifier.height(14.dp))



            // রিডিং মোড সিলেকশন

            Text(

                text = "পড়ার ভিউ মোড:",

                fontFamily = fonts.kalpurush,

                fontSize = 14.sp,

                fontWeight = FontWeight.Bold,

                color = MaterialTheme.colorScheme.onSurface

            )

            Spacer(Modifier.height(8.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(8.dp)

            ) {

                // ১. ক্লাসিক ছাপা বাটন

                val isClassicView = settings.readingMode == ReadingMode.CLASSIC

                Box(

                    modifier = Modifier

                        .weight(1f)

                        .clip(RoundedCornerShape(12.dp))

                        .background(if (isClassicView) Brand.EmeraldDark else Brand.EmeraldDark.copy(alpha = 0.08f))

                        .border(1.dp, Brand.EmeraldDark, RoundedCornerShape(12.dp))

                        .clickable { switchMode(ReadingMode.CLASSIC) }

                        .padding(vertical = 10.dp),

                    contentAlignment = Alignment.Center

                ) {

                    Text(

                        text = "জনপ্রিয় সংস্করণ",

                        fontFamily = fonts.kalpurush,

                        fontSize = 12.sp,

                        fontWeight = if (isClassicView) FontWeight.Bold else FontWeight.Medium,

                        color = if (isClassicView) Color.White else MaterialTheme.colorScheme.onSurface

                    )

                }



                // ২. মুসহাফ পৃষ্ঠা বাটন

                val isPageView = settings.readingMode == ReadingMode.PAGE

                Box(

                    modifier = Modifier

                        .weight(1f)

                        .clip(RoundedCornerShape(12.dp))

                        .background(if (isPageView) Brand.EmeraldDark else Brand.EmeraldDark.copy(alpha = 0.08f))

                        .border(1.dp, Brand.EmeraldDark, RoundedCornerShape(12.dp))

                        .clickable { switchMode(ReadingMode.PAGE) }

                        .padding(vertical = 10.dp),

                    contentAlignment = Alignment.Center

                ) {

                    Text(

                        text = "নতুন সংস্করণ",

                        fontFamily = fonts.kalpurush,

                        fontSize = 12.sp,

                        fontWeight = if (isPageView) FontWeight.Bold else FontWeight.Medium,

                        color = if (isPageView) Color.White else MaterialTheme.colorScheme.onSurface

                    )

                }



                                // ৩. আয়াত ও তাফসীর বাটন
                val isAyahView = settings.readingMode == ReadingMode.AYAH
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isAyahView) Brand.EmeraldDark else Brand.EmeraldDark.copy(alpha = 0.08f))
                        .border(1.dp, Brand.EmeraldDark, RoundedCornerShape(12.dp))
                        .clickable { switchMode(ReadingMode.AYAH) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ডিজিটাল সংস্করণ",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.sp,
                        fontWeight = if (isAyahView) FontWeight.Bold else FontWeight.Medium,
                        color = if (isAyahView) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

                // ৪. হাফেজী সংস্করণ বাটন
                val isHafeziView = settings.readingMode == ReadingMode.HAFEZI
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isHafeziView) Brand.EmeraldDark else Brand.EmeraldDark.copy(alpha = 0.08f))
                        .border(1.dp, Brand.EmeraldDark, RoundedCornerShape(12.dp))
                        .clickable { switchMode(ReadingMode.HAFEZI) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "হাফেজী সংস্করণ",
                        fontFamily = fonts.kalpurush,
                        fontSize = 11.sp,
                        fontWeight = if (isHafeziView) FontWeight.Bold else FontWeight.Medium,
                        color = if (isHafeziView) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

            }



            Spacer(Modifier.height(16.dp))



            // ১. বাংলা অর্থ প্রদর্শন সুইচ (শুধুমাত্র নতুন সংস্করণ বা ভেক্টর মুসহাফে প্রযোজ্য)
            if (settings.readingMode == ReadingMode.PAGE || settings.readingMode == ReadingMode.HAFEZI) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "বাংলা শব্দার্থ প্রদর্শন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (settings.showMeaning) "আরবি শব্দের নিচে বাংলা অর্থ দৃশ্যমান" else "অর্থ লুকানো",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.showMeaning,
                        onCheckedChange = { onUpdateSettings { it.copy(showMeaning = !it.showMeaning) } },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Brand.Gold,
                            checkedTrackColor = Brand.EmeraldDark
                        )
                    )
                }
            }

            // ২. মার্জিন ক্রপ সুইচ (ক্লাসিক ও মুসহাফ উভয় পৃষ্ঠা মোডে)

            if (settings.readingMode != ReadingMode.AYAH) {

                Spacer(Modifier.height(12.dp))

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.SpaceBetween

                ) {

                    Column(modifier = Modifier.weight(1f)) {

                        Text(

                            text = "মার্জিন ক্রপ (বড় ফন্টে ফিট)",

                            fontFamily = fonts.kalpurush,

                            fontSize = 14.sp,

                            fontWeight = FontWeight.Bold,

                            color = MaterialTheme.colorScheme.onSurface

                        )

                        Text(

                            text = if (settings.cropMargins) "বাইরের মার্জিন বাদ দিয়ে ফুলস্ক্রিনে বড় লেখা" else "সম্পূর্ণ বর্ডার ও মার্জিনসহ আসল পাতা",

                            fontFamily = fonts.kalpurush,

                            fontSize = 12.sp,

                            color = MaterialTheme.colorScheme.onSurfaceVariant

                        )

                    }

                    Switch(

                        checked = settings.cropMargins,

                        onCheckedChange = { onUpdateSettings { it.copy(cropMargins = !it.cropMargins) } },

                        colors = SwitchDefaults.colors(

                            checkedThumbColor = Brand.Gold,

                            checkedTrackColor = Brand.EmeraldDark

                        )

                    )

                }

            }



            // ৩. তিলাওয়াতের সময় স্ক্রিন অন রাখার সুইচ

            Spacer(Modifier.height(12.dp))

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                Column(modifier = Modifier.weight(1f)) {

                    Text(

                        text = "তিলাওয়াতের সময় স্ক্রিন অন রাখুন",

                        fontFamily = fonts.kalpurush,

                        fontSize = 14.sp,

                        fontWeight = FontWeight.Bold,

                        color = MaterialTheme.colorScheme.onSurface

                    )

                    Text(

                        text = if (settings.keepScreenOn) "পড়ার সময় ফোনের স্ক্রিন বন্ধ হবে না" else "সিস্টেম অনুযায়ী স্ক্রিন বন্ধ হবে",

                        fontFamily = fonts.kalpurush,

                        fontSize = 12.sp,

                        color = MaterialTheme.colorScheme.onSurfaceVariant

                    )

                }

                Switch(

                    checked = settings.keepScreenOn,

                    onCheckedChange = { onUpdateSettings { it.copy(keepScreenOn = !it.keepScreenOn) } },

                    colors = SwitchDefaults.colors(

                        checkedThumbColor = Brand.Gold,

                        checkedTrackColor = Brand.EmeraldDark

                    )

                )

            }



            Spacer(Modifier.height(16.dp))

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.3f))

            Spacer(Modifier.height(14.dp))



            // থিম নির্বাচন (৬টি প্রিসেট থিম)

            Text(

                text = "থিম প্রিসেট (৬টি কালার স্কিম):",

                fontFamily = fonts.kalpurush,

                fontSize = 14.sp,

                fontWeight = FontWeight.Bold,

                color = MaterialTheme.colorScheme.onSurface

            )

            Spacer(Modifier.height(10.dp))

            FlowRow(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(8.dp),

                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {

                for (themePalette in Palettes.all) {

                    val isSelected = settings.themeId == themePalette.id && settings.customBg == null

                    Surface(

                        modifier = Modifier

                            .clip(RoundedCornerShape(10.dp))

                            .border(

                                width = if (isSelected) 2.dp else 1.dp,

                                color = if (isSelected) Brand.Gold else Color.Gray.copy(alpha = 0.3f),

                                shape = RoundedCornerShape(10.dp)

                            )

                            .clickable {

                                onUpdateSettings {

                                    it.copy(

                                        themeId = themePalette.id,

                                        customBg = null,

                                        customArabic = null,

                                        customBangla = null

                                    )

                                }

                            },

                        shape = RoundedCornerShape(10.dp),

                        color = themePalette.bg

                    ) {

                                                Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(themePalette.arabic)
                            )
                            Spacer(Modifier.width(7.dp))
                            Text(
                                text = themePalette.nameBn,
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (themePalette.isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                            )
                            if (isSelected) {
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Brand.Gold,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                    }

                }

            }



            Spacer(Modifier.height(20.dp))

            Spacer(Modifier.height(16.dp))



            // কাস্টম মাল্টি-কালার পিকার (পটভূমি, আরবি ও বাংলা অর্থ আলাদাভাবে কাস্টমাইজেশন)

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                Text(

                    text = "কাস্টম কালার পছন্দ করুন:",

                    fontFamily = fonts.kalpurush,

                    fontSize = 14.sp,

                    fontWeight = FontWeight.Bold,

                    color = MaterialTheme.colorScheme.onSurface

                )

                if (settings.customBg != null || settings.customArabic != null || settings.customBangla != null) {

                    Surface(

                        shape = RoundedCornerShape(8.dp),

                        color = Brand.Gold.copy(alpha = 0.16f),

                        modifier = Modifier.clickable {

                            onUpdateSettings {

                                it.copy(customBg = null, customArabic = null, customBangla = null)

                            }

                        }

                    ) {

                        Row(

                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),

                            verticalAlignment = Alignment.CenterVertically

                        ) {

                            Icon(

                                imageVector = Icons.Default.Refresh,

                                contentDescription = "রিসেট",

                                tint = Brand.EmeraldDark,

                                modifier = Modifier.size(13.dp)

                            )

                            Spacer(Modifier.width(4.dp))

                            Text(

                                text = "প্রিসেট রিসেট",

                                fontFamily = fonts.kalpurush,

                                fontSize = 11.sp,

                                fontWeight = FontWeight.Bold,

                                color = Brand.EmeraldDark

                            )

                        }

                    }

                }

            }



            Spacer(Modifier.height(8.dp))



            val bgSwatches = listOf(

                0xFFFFFFFF.toInt() to "সাদা",

                0xFFFDFBF7.toInt() to "পার্চমেন্ট",

                0xFFF4ECD8.toInt() to "সেপিয়া",

                0xFFECFDF5.toInt() to "মিন্ট",

                0xFF0B131E.toInt() to "অবসিডিয়ান",

                0xFF063B2C.toInt() to "এমেরাল্ড"

            )

            val arSwatches = listOf(

                0xFF0F172A.toInt() to "কালো",

                0xFF064E3B.toInt() to "সবুজ",

                0xFF3B2F1E.toInt() to "বাদামী",

                0xFFFFFFFF.toInt() to "সাদা",

                0xFFFEF3C7.toInt() to "ক্রিম",

                0xFFFDE68A.toInt() to "গোল্ড"

            )

            val bnSwatches = listOf(

                0xFF334155.toInt() to "স্লেট",

                0xFF065F46.toInt() to "এমেরাল্ড",

                0xFF5B4636.toInt() to "সেপিয়া",

                0xFFE2E8F0.toInt() to "সিলভার",

                0xFFA7F3D0.toInt() to "পেস্ট",

                0xFFD4AF37.toInt() to "স্বর্ণালী"

            )



            CustomColorRow(

                label = "পটভূমির রঙ:",

                swatches = bgSwatches,

                selectedColor = settings.customBg,

                onSelect = { chosen -> onUpdateSettings { it.copy(customBg = chosen) } }

            )

            Spacer(Modifier.height(8.dp))

            CustomColorRow(

                label = "আরবি লেখার রঙ:",

                swatches = arSwatches,

                selectedColor = settings.customArabic,

                onSelect = { chosen -> onUpdateSettings { it.copy(customArabic = chosen) } }

            )

            Spacer(Modifier.height(8.dp))

            CustomColorRow(

                label = "বাংলা অর্থের রঙ:",

                swatches = bnSwatches,

                selectedColor = settings.customBangla,

                onSelect = { chosen -> onUpdateSettings { it.copy(customBangla = chosen) } }

            )



            Spacer(Modifier.height(18.dp))

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.3f))

            Spacer(Modifier.height(14.dp))



            if (settings.readingMode == ReadingMode.AYAH) {
                // ১. ডিজিটাল সংস্করণ (আয়াত ও তাফসীর ভিউ): ডায়নামিক টেক্সট টাইপোগ্রাফি ও ফন্ট সাইজ
                Text(
                    text = "বাংলা ফন্ট ও টাইপোগ্রাফি:",
                    fontFamily = fonts.kalpurush,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val fontOpts = listOf(
                        Pair("kalpurush", "কালপুরুষ"),
                        Pair("hind_siliguri", "হিন্দ শিলিগুড়ি"),
                        Pair("fira_sans", "ফিরাস্যান্স")
                    )
                    fontOpts.forEach { (key, name) ->
                        val normKey = key.replace("_", "")
                        val currentNorm = settings.banglaFont.replace("_", "")
                        val isSel = currentNorm == normKey
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onUpdateSettings { it.copy(banglaFont = key) } },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) Brand.EmeraldDark else Brand.EmeraldDark.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Brand.EmeraldDark)
                        ) {
                            Text(
                                text = name,
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // বোল্ড মোড সুইচ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "বোল্ড (Bold) টেক্সট মোড",
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "টেক্সটের ঘনত্ব বৃদ্ধি করে স্পষ্ট পড়া",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.fontBold,
                        onCheckedChange = { onUpdateSettings { it.copy(fontBold = !it.fontBold) } },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Brand.Gold,
                            checkedTrackColor = Brand.EmeraldDark
                        )
                    )
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.3f))
                Spacer(Modifier.height(14.dp))

                // আরবি ফন্ট সাইজ অ্যাডজাস্টমেন্ট
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "আরবি লেখার আকার:",
                        fontFamily = fonts.kalpurush,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${settings.arabicScale.times(100).toInt().toBn()}%",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brand.EmeraldDark
                    )
                }
                Slider(
                    value = settings.arabicScale,
                    onValueChange = { scale ->
                        onUpdateSettings { it.copy(arabicScale = (scale * 10).toInt() / 10f) }
                    },
                    valueRange = 0.8f..1.8f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = Brand.EmeraldDark,
                        activeTrackColor = Brand.EmeraldDark
                    )
                )

                // বাংলা ফন্ট সাইজ অ্যাডজাস্টমেন্ট
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "বাংলা অর্থের আকার:",
                        fontFamily = fonts.kalpurush,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${settings.banglaScale.times(100).toInt().toBn()}%",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brand.EmeraldDark
                    )
                }
                Slider(
                    value = settings.banglaScale,
                    onValueChange = { scale ->
                        onUpdateSettings { it.copy(banglaScale = (scale * 10).toInt() / 10f) }
                    },
                    valueRange = 0.8f..1.6f,
                    steps = 7,
                    colors = SliderDefaults.colors(
                        thumbColor = Brand.EmeraldDark,
                        activeTrackColor = Brand.EmeraldDark
                    )
                )
            } else {
                // ২. নতুন সংস্করণ ও জনপ্রিয় সংস্করণ (মুদ্রিত পাতা/এসভিজি): সম্পূর্ণ পেজ জুম ও স্কেলিং
                val currentZoom = (settings.fontScale * 100).toInt().coerceIn(100, 300)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "সম্পূর্ণ পৃষ্ঠা জুম (Page Zoom):",
                        fontFamily = fonts.kalpurush,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${currentZoom.toBn()}%",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brand.EmeraldDark
                    )
                }
                Spacer(Modifier.height(8.dp))

                // কুইক জুম প্রিসেট বাটনসমূহ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val zoomPresets = listOf(100, 125, 150, 200, 250)
                    zoomPresets.forEach { p ->
                        val isSel = currentZoom == p
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onUpdateSettings { it.copy(fontScale = p / 100f) } },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Brand.EmeraldDark else Brand.EmeraldDark.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Brand.Gold else Brand.EmeraldDark.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "${p.toBn()}%",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 7.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))
                Slider(
                    value = settings.fontScale,
                    onValueChange = { scale ->
                        onUpdateSettings { it.copy(fontScale = (scale * 20).toInt() / 20f) }
                    },
                    valueRange = 1.0f..3.0f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = Brand.EmeraldDark,
                        activeTrackColor = Brand.EmeraldDark
                    )
                )

                // জুম ও প্যানিং টিপ কার্ড
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Brand.EmeraldDark.copy(alpha = 0.06f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Brand.EmeraldDark.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "দুই আঙুলে টেনে (Pinch-to-zoom) কিংবা ডাবল ট্যাপ করে সরাসরি জুম করতে পারেন। জুম অবস্থায় এক আঙুলে যেকোনো দিকে (উপরে-নিচে ও ডানে-বামে) মসৃণভাবে স্ক্রল করা যাবে।",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }
                }
            }


            // তাজবীদ কালার গাইড সহায়িকা

            Text(

                text = "ক্বারীয়ানা তাজবীদ কালার সহায়িকা:",

                fontFamily = fonts.kalpurush,

                fontSize = 14.sp,

                fontWeight = FontWeight.Bold,

                color = MaterialTheme.colorScheme.onSurface

            )

            Spacer(Modifier.height(10.dp))



            FlowRow(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(8.dp),

                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {

                val rules = listOf(

                    Triple("গুন্নাহ (ওয়াজিব/ইদগাম)", Color(0xFFEAB308), "নাসিক্য ধ্বনি"),

                    Triple("ইদগাম (বা-গুন্নাহ)", Color(0xFFF59E0B), "মিলিয়ে পড়া"),

                    Triple("ইখফা (হাকীকী)", Color(0xFF3B82F6), "গোপন করে গুন্নাহ"),

                    Triple("কলকলাহ (প্রতিধ্বনি)", Color(0xFF10B981), "কাঁপিয়ে উচ্চারণ"),

                    Triple("মাদ্দ (৩/৪ আলিফ)", Color(0xFFEF4444), "দীর্ঘ টানা"),

                    Triple("ইক্বলাব (মীম রূপান্তর)", Color(0xFF8B5CF6), "মীম-এ রূপান্তর")

                )



                rules.forEach { (name, color, desc) ->

                    Surface(

                        shape = RoundedCornerShape(10.dp),

                        color = color.copy(alpha = 0.12f),

                        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.45f))

                    ) {

                        Row(

                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),

                            verticalAlignment = Alignment.CenterVertically

                        ) {

                            Box(

                                modifier = Modifier

                                    .size(10.dp)

                                    .clip(CircleShape)

                                    .background(color)

                            )

                            Spacer(Modifier.width(6.dp))

                            Column {

                                Text(

                                    text = name,

                                    fontFamily = fonts.kalpurush,

                                    fontSize = 12.sp,

                                    fontWeight = FontWeight.Bold,

                                    color = MaterialTheme.colorScheme.onSurface

                                )

                                Text(

                                    text = desc,

                                    fontFamily = fonts.kalpurush,

                                    fontSize = 10.sp,

                                    color = MaterialTheme.colorScheme.onSurfaceVariant

                                )

                            }

                        }

                    }

                }

            }



            if (isUnlocked && offlineState != null) {

                Spacer(Modifier.height(18.dp))

                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.3f))

                Spacer(Modifier.height(14.dp))



                Text(

                    text = "অফলাইন কনটেন্ট ও ডেটা:",

                    fontFamily = fonts.kalpurush,

                    fontSize = 14.sp,

                    fontWeight = FontWeight.Bold,

                    color = MaterialTheme.colorScheme.onSurface

                )

                Spacer(Modifier.height(8.dp))



                Surface(

                    shape = RoundedCornerShape(12.dp),

                    color = Brand.EmeraldDark.copy(alpha = 0.08f),

                    border = androidx.compose.foundation.BorderStroke(1.dp, Brand.EmeraldDark.copy(alpha = 0.3f)),

                    modifier = Modifier.fillMaxWidth()

                ) {

                    Column(modifier = Modifier.padding(12.dp)) {

                        when (offlineState) {

                            is com.karianaquran.reader.data.remote.OfflineDownloadState.Downloading -> {

                                Row(

                                    modifier = Modifier.fillMaxWidth(),

                                    horizontalArrangement = Arrangement.SpaceBetween,

                                    verticalAlignment = Alignment.CenterVertically

                                ) {

                                    Text(

                                        text = "ডাউনলোড হচ্ছে... (${offlineState.progressPercent.toBn()}%)",

                                        fontFamily = fonts.kalpurush,

                                        fontSize = 13.sp,

                                        fontWeight = FontWeight.Bold,

                                        color = Brand.EmeraldDark

                                    )

                                    Text(

                                        text = "বাতিল",

                                        fontFamily = fonts.kalpurush,

                                        fontSize = 12.sp,

                                        fontWeight = FontWeight.Bold,

                                        color = Color(0xFFEF4444),

                                        modifier = Modifier.clickable { onCancelOfflineDownload?.invoke() }

                                    )

                                }

                                Spacer(Modifier.height(6.dp))

                                androidx.compose.material3.LinearProgressIndicator(

                                    progress = { offlineState.progressPercent / 100f },

                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),

                                    color = Brand.EmeraldDark

                                )

                                Spacer(Modifier.height(4.dp))

                                Text(

                                    text = offlineState.currentStage,

                                    fontFamily = fonts.kalpurush,

                                    fontSize = 11.sp,

                                    color = MaterialTheme.colorScheme.onSurfaceVariant

                                )

                            }

                            is com.karianaquran.reader.data.remote.OfflineDownloadState.Idle -> {

                                Text(

                                    text = if (offlineState.isReady) "সম্পূর্ণ ৩০ পারা অফলাইনে সংরক্ষিত আছে" else "ইন্টারনেট ছাড়া পড়তে অফলাইন ডেটা ডাউনলোড করুন",

                                    fontFamily = fonts.kalpurush,

                                    fontSize = 12.5.sp,

                                    fontWeight = FontWeight.Medium,

                                    color = MaterialTheme.colorScheme.onSurface

                                )

                                Spacer(Modifier.height(8.dp))

                                Box(

                                    modifier = Modifier

                                        .fillMaxWidth()

                                        .clip(RoundedCornerShape(8.dp))

                                        .background(Brand.EmeraldDark)

                                        .clickable { onStartOfflineDownload?.invoke() }

                                        .padding(vertical = 8.dp),

                                    contentAlignment = Alignment.Center

                                ) {

                                    Text(

                                        text = if (offlineState.isReady) "পুনরায় সিঙ্ক করুন" else "অফলাইন ডাউনলোড শুরু করুন",

                                        fontFamily = fonts.kalpurush,

                                        fontSize = 12.sp,

                                        fontWeight = FontWeight.Bold,

                                        color = Color.White

                                    )

                                }

                            }

                            else -> {}

                        }

                    }

                }

            }

        }

    }

}



@Composable

private fun CustomColorRow(

    label: String,

    swatches: List<Pair<Int, String>>,

    selectedColor: Int?,

    onSelect: (Int) -> Unit,

) {

    val fonts = LocalAppFonts.current

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment = Alignment.CenterVertically,

        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Text(

            text = label,

            fontFamily = fonts.kalpurush,

            fontSize = 12.5.sp,

            color = MaterialTheme.colorScheme.onSurfaceVariant,

            modifier = Modifier.width(96.dp)

        )

        Row(

            horizontalArrangement = Arrangement.spacedBy(8.dp),

            verticalAlignment = Alignment.CenterVertically

        ) {

            swatches.forEach { (argb, name) ->

                val isSelected = selectedColor == argb

                val swatchColor = Color(argb)

                val checkTint = if ((0.299f * swatchColor.red + 0.587f * swatchColor.green + 0.114f * swatchColor.blue) > 0.6f) {

                    Color(0xFF0F172A)

                } else {

                    Color.White

                }

                Box(

                    modifier = Modifier

                        .size(28.dp)

                        .clip(CircleShape)

                        .background(swatchColor)

                        .border(

                            width = if (isSelected) 2.2.dp else 1.dp,

                            color = if (isSelected) Brand.Gold else Color.Gray.copy(alpha = 0.45f),

                            shape = CircleShape

                        )

                        .clickable { onSelect(argb) },

                    contentAlignment = Alignment.Center

                ) {

                    if (isSelected) {

                        Icon(

                            imageVector = Icons.Default.Check,

                            contentDescription = name,

                            tint = checkTint,

                            modifier = Modifier.size(14.dp)

                        )

                    }

                }

            }

        }

    }

}

