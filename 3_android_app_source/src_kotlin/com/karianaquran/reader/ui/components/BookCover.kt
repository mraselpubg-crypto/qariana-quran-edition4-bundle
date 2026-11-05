package com.karianaquran.reader.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.data.model.Publication
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * আন্তর্জাতিক মানের ৩ডি ইসলামিক বই কভার (Real 3D Book Cover)
 * চামড়ার বাঁধাই, সোনালী ক্যালিগ্রাফি মেডেলিয়ন, বর্ডার ও স্পাইন শেডো সহ
 */
@Composable
fun RealBookCover(
    publication: Publication,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    showSpine: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val fonts = LocalAppFonts.current
    val primaryColor = Color(publication.primaryColor)
    val accentColor = Color(publication.accentColor)
    val width = height * 0.68f

    val baseModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else modifier

    Box(
        modifier = baseModifier
            .width(width)
            .height(height)
            .shadow(10.dp, shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp, topStart = 4.dp, bottomStart = 4.dp), ambientColor = Color.Black.copy(alpha = 0.5f), spotColor = Color.Black.copy(alpha = 0.6f))
    ) {
        // বইয়ের ডান পাশের পৃষ্ঠার স্তূপ (Paper Edge Bevel)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(0.96f)
                .width(5.dp)
                .offset(x = 3.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFFE2D9C8), Color(0xFFC7BBA2), Color(0xFFF5EFE0))
                    ),
                    RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp)
                )
        )

        val isOrthoQuran = publication.id == com.karianaquran.reader.data.model.Publications.ORTHO_QURAN_ID || publication.id == "ortho_quran"

        // মূল কভার
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp, topStart = 4.dp, bottomStart = 4.dp),
            color = if (isOrthoQuran) Color(0xFF063B2C) else primaryColor,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Brush.linearGradient(
                    listOf(accentColor.copy(alpha = 0.8f), accentColor.copy(alpha = 0.3f), accentColor)
                )
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val coverRes = publication.coverDrawableRes ?: if (isOrthoQuran) com.karianaquran.reader.R.drawable.qariana_ortho_cover else null
                if (coverRes != null) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = coverRes),
                        contentDescription = publication.titleBn,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.85f),
                                        primaryColor,
                                        Color(0xFF02100C)
                                    ),
                                    radius = 450f
                                )
                            )
                    ) {
                        Canvas(Modifier.fillMaxSize()) {
                            val goldAlpha = accentColor.copy(alpha = 0.12f)
                            val step = size.minDimension / 3.4f
                            var y = 0f
                            var row = 0
                            while (y < size.height + step) {
                                var x = if (row % 2 == 0) 0f else step / 2
                                while (x < size.width + step) {
                                    drawPath(
                                        starPath(Offset(x, y), step * 0.38f, step * 0.18f, 8),
                                        goldAlpha,
                                        style = Stroke(width = 1.2f)
                                    )
                                    x += step
                                }
                                y += step * 0.86f
                                row++
                            }

                            val inset = 8.dp.toPx()
                            drawRect(
                                color = accentColor.copy(alpha = 0.45f),
                                topLeft = Offset(inset, inset),
                                size = Size(size.width - inset * 2, size.height - inset * 2),
                                style = Stroke(width = 1.2f)
                            )
                            val inset2 = 12.dp.toPx()
                            drawRect(
                                color = accentColor.copy(alpha = 0.25f),
                                topLeft = Offset(inset2, inset2),
                                size = Size(size.width - inset2 * 2, size.height - inset2 * 2),
                                style = Stroke(width = 0.8f)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(accentColor.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
                                    .border(0.6.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "بِسْمِ اللَّهِ",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(height * 0.42f)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(accentColor.copy(alpha = 0.22f), Color.Transparent)
                                        ),
                                        CircleShape
                                    )
                                    .border(1.2.dp, accentColor.copy(alpha = 0.7f), CircleShape)
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (publication.titleAr.isNotEmpty()) {
                                        Text(
                                            text = publication.titleAr,
                                            fontSize = (height.value * 0.082f).sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor,
                                            textAlign = TextAlign.Center,
                                            lineHeight = (height.value * 0.095f).sp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.AutoStories,
                                            contentDescription = null,
                                            tint = accentColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = publication.titleBn,
                                    fontFamily = fonts.kalpurush,
                                    fontSize = (height.value * 0.075f).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(Modifier.height(2.dp))

                                Text(
                                    text = if (publication.totalPages > 0) "${publication.totalPages.toBn()} পৃষ্ঠা" else publication.badge,
                                    fontSize = 8.5.sp,
                                    color = accentColor.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // বাম পাশের বুক স্পাইন শেডো (Spine Bevel Shadow)
                if (showSpine) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxHeight()
                            .width(10.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black.copy(alpha = 0.2f),
                                        Color.White.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                // রিবন বুকমার্ক (টপে ঝুলন্ত ফিতা)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp)
                        .width(9.dp)
                        .height(18.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(accentColor, accentColor.copy(alpha = 0.7f))
                            ),
                            RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp)
                        )
                )
            }
        }
    }
}

/** পুরাতন ভেক্টর বুক কভার সাপোর্ট */
@Composable
fun BookCover(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(Brand.EmeraldLight, Brand.Emerald, Color(0xFF021E17)),
    corner: Int = 18,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(corner.dp)
    Box(
        modifier
            .clip(shape)
            .background(Brush.linearGradient(colors))
            .border(1.5.dp, Brush.linearGradient(listOf(Brand.Gold, Brand.Gold.copy(alpha = 0.3f), Brand.Gold)), shape)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val gold = Brand.Gold.copy(alpha = 0.16f)
            val step = size.minDimension / 3.2f
            var y = 0f
            var row = 0
            while (y < size.height + step) {
                var x = if (row % 2 == 0) 0f else step / 2
                while (x < size.width + step) {
                    drawPath(starPath(Offset(x, y), step * 0.42f, step * 0.2f, 8), gold, style = Stroke(width = 1.4f))
                    x += step
                }
                y += step * 0.86f
                row++
            }
        }
        content()
    }
}

fun starPath(center: Offset, outer: Float, inner: Float, points: Int): Path {
    val p = Path()
    val total = points * 2
    for (i in 0 until total) {
        val r = if (i % 2 == 0) outer else inner
        val a = -PI / 2 + i * PI / points
        val x = center.x + (r * cos(a)).toFloat()
        val y = center.y + (r * sin(a)).toFloat()
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    return p
}
