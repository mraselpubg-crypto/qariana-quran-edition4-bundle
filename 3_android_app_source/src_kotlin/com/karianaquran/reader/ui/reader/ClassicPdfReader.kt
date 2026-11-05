package com.karianaquran.reader.ui.reader

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.karianaquran.reader.ui.components.ClassicPageShimmer
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.security.ClassicPdfEngine
import com.karianaquran.reader.security.SecurityGuard
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * ক্বারীয়ানা কুরআন - নেটিভ ক্লাসিক পিডিএফ পৃষ্ঠা রিডার (জনপ্রিয় সংস্করণ)
 *
 * - শতভাগ নেটিভ হাই-রেজোলিউশন বিটম্যাপ রেন্ডারিং ও এজ-টু-এজ ফুল-স্ক্রিন ফিট
 * - সব থিমে বাংলা অর্থ ও আরবি আয়াতের সর্বোচ্চ স্পষ্টতা ও কন্ট্রাস্ট
 * - পিনচ-টু-জুম ও ডাবল-ট্যাপ জুম সমর্থন
 */
@Composable
fun ClassicPdfReader(
    pageNumber: Int,
    themeId: String,
    cropMargins: Boolean = true,
    showMeaning: Boolean = true,
    customBg: Int? = null,
    customArabic: Int? = null,
    customBangla: Int? = null,
    customInk: Int? = null,
    isImmersive: Boolean = false,
    modifier: Modifier = Modifier,
    onToggleImmersive: () -> Unit = {},
) {
    val context = LocalContext.current
    val fonts = LocalAppFonts.current
    val config = LocalConfiguration.current
    val densityDpi = config.densityDpi
    val displayPageNum = (pageNumber - 10).coerceAtLeast(1)

    // ১. সিকিউরিটি স্ট্যাটাস পরীক্ষা (রুট / ডিবাগিং / হুকিং)
    val security = remember(context) { SecurityGuard.evaluate(context) }
    val isSecurityBlocked = remember(security) { SecurityGuard.shouldBlock(security) }

    if (isSecurityBlocked) {
        ReaderSecurityGuidanceCard(security = security)
        return
    }

    // ২. থিম অনুযায়ী নিখুঁত ব্যাকগ্রাউন্ড ও টেক্সট কালার
    val pageBgColor = remember(themeId, customBg) {
        Color(ClassicPdfEngine.resolvePageBackgroundColor(themeId, customBg))
    }
    val isDarkSurface = remember(pageBgColor) { pageBgColor.luminance() < 0.45f }
    val primaryStatusTextColor = if (isDarkSurface) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val secondaryStatusTextColor = if (isDarkSurface) Color(0xFF94A3B8) else Color(0xFF475569)

    // ৩. বিটম্যাপ ও রিট্রাই স্টেট
    var retryCount by remember(pageNumber) { mutableIntStateOf(0) }
    var pageBitmap by remember(pageNumber, themeId, cropMargins, showMeaning, customBg, customArabic, customBangla, customInk) {
        mutableStateOf<Bitmap?>(null)
    }
    var isLoading by remember(pageNumber, themeId, cropMargins, showMeaning, customBg, customArabic, customBangla, customInk, retryCount) {
        mutableStateOf(true)
    }

    // ৪. জুম ও প্যান স্টেট
    var scale by remember(pageNumber) { mutableFloatStateOf(1f) }
    var offsetX by remember(pageNumber) { mutableFloatStateOf(0f) }
    var offsetY by remember(pageNumber) { mutableFloatStateOf(0f) }

    LaunchedEffect(pageNumber, themeId, cropMargins, showMeaning, customBg, customArabic, customBangla, customInk, retryCount) {
        isLoading = true
        scale = 1f
        offsetX = 0f
        offsetY = 0f
        val bmp = ClassicPdfEngine.getPageBitmap(
            context = context,
            pageNumber = pageNumber,
            themeId = themeId,
            densityDpi = densityDpi,
            cropMargins = cropMargins,
            showMeaning = showMeaning,
            customBg = customBg,
            customArabic = customArabic,
            customBangla = customBangla,
            customInk = customInk
        )
        pageBitmap = bmp
        isLoading = false
        if (bmp != null) {
            ClassicPdfEngine.prefetchPage(context, pageNumber + 1, !showMeaning)
            ClassicPdfEngine.prefetchPage(context, pageNumber + 2, !showMeaning)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(pageBgColor)
            .pointerInput(pageNumber) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > 1.2f) 1f else 2.2f
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onTap = { onToggleImmersive() }
                )
            }
            .pointerInput(pageNumber) {
                // ১x জুমে সিঙ্গেল-ফিঙ্গার সোয়াইপ সম্পূর্ণ আনকনজিউমড থাকবে যাতে HorizontalPager স্মুথলি পৃষ্ঠা উল্টাতে পারে
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    do {
                        val event = awaitPointerEvent()
                        val pressedCount = event.changes.count { it.pressed }
                        if (pressedCount >= 2) {
                            val zoomChange = event.calculateZoom()
                            val panChange = event.calculatePan()
                            val newScale = (scale * zoomChange).coerceIn(1f, 4.0f)
                            scale = newScale
                            if (newScale > 1.02f) {
                                val maxX = (size.width * (newScale - 1f)) / 2f
                                val maxY = (size.height * (newScale - 1f)) / 2f
                                offsetX = (offsetX + panChange.x).coerceIn(-maxX, maxX)
                                offsetY = (offsetY + panChange.y).coerceIn(-maxY, maxY)
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                            }
                            event.changes.forEach { if (it.positionChanged()) it.consume() }
                        } else if (pressedCount == 1 && scale > 1.02f) {
                            val panChange = event.calculatePan()
                            val maxX = (size.width * (scale - 1f)) / 2f
                            val maxY = (size.height * (scale - 1f)) / 2f
                            val nextX = (offsetX + panChange.x).coerceIn(-maxX, maxX)
                            val nextY = (offsetY + panChange.y).coerceIn(-maxY, maxY)
                            val consumedX = kotlin.math.abs(nextX - offsetX) > 0.5f
                            val consumedY = kotlin.math.abs(nextY - offsetY) > 0.5f
                            offsetX = nextX
                            offsetY = nextY
                            if (consumedX || consumedY) {
                                event.changes.forEach { if (it.positionChanged()) it.consume() }
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        val bmp = pageBitmap
        if (bmp != null && !bmp.isRecycled) {
            val imageBitmap = remember(bmp) { bmp.asImageBitmap() }

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (cropMargins && pageNumber != 11) {
                    val bmpAspect = (bmp.width.toFloat() / bmp.height.toFloat().coerceAtLeast(1f))
                        .coerceIn(0.45f, 1.25f)
                    val vpAspect = if (maxHeight.value > 1f) {
                        maxWidth.value / maxHeight.value
                    } else {
                        bmpAspect
                    }

                    val imgW: androidx.compose.ui.unit.Dp
                    val imgH: androidx.compose.ui.unit.Dp
                    if (vpAspect <= bmpAspect) {
                        // পোর্ট্রেট মোবাইল ভিউপোর্ট: ডানে-বামে ১০০% ফুল-উইডথ এজ-টু-এজ ফিট (০px সাইড গ্যাপ) এবং প্রাকৃতিক অনুপাত
                        imgW = maxWidth
                        imgH = maxWidth / bmpAspect
                    } else {
                        // ট্যাবলেট / ল্যান্ডস্কেপ ভিউপোর্ট
                        imgH = maxHeight
                        imgW = maxHeight * bmpAspect
                    }

                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "পৃষ্ঠা ${displayPageNum.toBn()}",
                        contentScale = ContentScale.Fit,
                        filterQuality = FilterQuality.High,
                        modifier = Modifier
                            .size(width = imgW, height = imgH)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY
                            )
                    )
                } else {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "পৃষ্ঠা ${displayPageNum.toBn()}",
                        contentScale = ContentScale.Fit,
                        filterQuality = FilterQuality.High,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY
                            )
                    )
                }
            }

            // কুইক জুম কন্ট্রোল (জুম করা অবস্থায় রিসেট ও ফাইন-টিউনের জন্য দৃশ্যমান হবে, স্বাভাবিক ভিউতে ফুটার না ঢাকতে লুকানো থাকবে)
            AnimatedVisibility(
                visible = scale > 1.05f,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 10.dp, end = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.Black.copy(alpha = if (scale > 1.05f) 0.72f else 0.48f))
                        .padding(horizontal = 3.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { scale = (scale + 0.35f).coerceAtMost(4.0f) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "জুম ইন", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = {
                            scale = (scale - 0.35f).coerceAtLeast(1f)
                            if (scale <= 1.02f) {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "জুম আউট", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "স্ক্রিন ফিট", tint = Brand.Gold, modifier = Modifier.size(16.dp))
                    }
                }
            }
        } else if (isLoading) {
            ClassicPageShimmer(
                pageNum = displayPageNum,
                isDark = isDarkSurface,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // পেজ লোড ব্যর্থ বা অফলাইন
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFD4AF37),
                    modifier = Modifier.size(44.dp)
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "পৃষ্ঠা ${displayPageNum.toBn()} লোড করা যায়নি",
                    fontFamily = fonts.kalpurush,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = primaryStatusTextColor
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "পৃষ্ঠাটি পড়তে ইন্টারনেট সংযোগ নিশ্চিত করে পুনরায় চেষ্টা করুন অথবা সম্পূর্ণ সংস্করণ আনলক করুন।",
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    color = secondaryStatusTextColor,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { retryCount++ },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brand.EmeraldDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Brand.Gold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "পুনরায় চেষ্টা করুন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * রিডার স্ক্রিনে প্রদর্শিত নন-ক্র্যাশিং সিকিউরিটি গাইডেন্স কার্ড
 */
@Composable
private fun ReaderSecurityGuidanceCard(
    security: SecurityGuard.Result,
    modifier: Modifier = Modifier
) {
    val fonts = LocalAppFonts.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = if (security.rooted) "ডিভাইস সুরক্ষা সতর্কতা" else "নিরাপত্তা নীতি নির্দেশিকা",
                    fontFamily = fonts.kalpurush,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(10.dp))

                val guidanceText = if (security.rooted) {
                    "আপনার ডিভাইসে রুট (Root) অ্যাক্সেস সক্রিয় রয়েছে। কপিরাইট ও কুরআন পৃষ্ঠাগুলোর সর্বোচ্চ নিরাপত্তা নিশ্চিত করতে রুট করা ডিভাইসে মূল পৃষ্ঠাগুলো প্রদর্শন সুরক্ষিত রাখা হয়েছে।"
                } else {
                    "আপনার ডিভাইসের বিশেষ নিরাপত্তা সেটিংস (যেমন: Developer Options বা USB Debugging) সক্রিয় রয়েছে। সুরক্ষিত কুরআন পাঠের জন্য সেটিংস থেকে এই অপশনগুলো বন্ধ করে সাধারণ মোডে ব্যবহার করুন।"
                }

                Text(
                    text = guidanceText,
                    fontFamily = fonts.kalpurush,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "অ্যাপসের অন্যান্য অপশন ও ফিচারস আপনি সাধারণভাবে ব্যবহার করতে পারবেন।",
                    fontFamily = fonts.kalpurush,
                    fontSize = 12.sp,
                    color = Color(0xFFD4AF37),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
