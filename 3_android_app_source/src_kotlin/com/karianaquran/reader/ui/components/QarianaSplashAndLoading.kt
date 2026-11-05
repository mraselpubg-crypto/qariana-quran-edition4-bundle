package com.karianaquran.reader.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.R
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * আন্তর্জাতিক ইসলামিক স্ট্যান্ডার্ড, হাই-কোয়ালিটি ও অপটিমাইজড অফিসিয়াল স্প্ল্যাশ স্ক্রিন
 * নেটিভ জিপিইউ-অ্যাক্সিলারেটেড ৬০/১২০ এফপিএস অ্যানিমেশন ও অফিশিয়াল লোগো মেডেলিয়ন
 */
@Composable
fun QarianaSplashScreen(
    modifier: Modifier = Modifier
) {
    val fonts = LocalAppFonts.current
    val transition = rememberInfiniteTransition(label = "qariana_splash_motion")

    // ১. ধীরগতির মার্জিত গোল্ডেন আউটার রিং রোটেশন (১৬ সেকেন্ড ক্লকওয়াইজ)
    val outerRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_rotation"
    )

    // ২. কাউন্টার-রোটেশন ইনার ইসলামিক জিওমেট্রিক রিং (১২ সেকেন্ড অ্যান্টি-ক্লকওয়াইজ)
    val innerRotation by transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_rotation"
    )

    // ৩. শ্বাসপ্রশ্বাসের মতো প্রশান্তিদায়ক ব্রিদিং পালস (২.৪ সেকেন্ড)
    val logoScale by transition.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    // ৪. গোল্ডেন অরা গ্লো অ্যানিমেশন
    val glowAlpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // ৫. প্রগ্রেস বার ট্রাভেলিং শিমার
    val shimmerOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F4D3F),
                        Color(0xFF072A22),
                        Color(0xFF031612)
                    ),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // ব্যাকগ্রাউন্ডের ব্যাকড্রপ ওয়াটারমার্ক (৮-পয়েন্ট ইসলামিক তারকা)
        Canvas(
            modifier = Modifier
                .size(420.dp)
                .rotate(outerRotation * 0.4f)
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val rOuter = size.minDimension / 2f
            val rInner = rOuter * 0.76f

            val path = Path()
            for (i in 0 until 16) {
                val angle = Math.toRadians((i * 360.0 / 16.0) - 90.0)
                val r = if (i % 2 == 0) rOuter else rInner
                val x = (cx + r * Math.cos(angle)).toFloat()
                val y = (cy + r * Math.sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()

            drawPath(
                path = path,
                color = Color(0xFFD4AF37).copy(alpha = 0.05f),
                style = Stroke(width = 2f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // ==========================================
            // ১. সেন্ট্রাল প্রিমিয়াম লোগো মেডেলিয়ন
            // ==========================================
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .scale(logoScale),
                contentAlignment = Alignment.Center
            ) {
                // ১.১. গোল্ডেন অরা ব্লুম
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFD4AF37).copy(alpha = glowAlpha * 0.35f),
                                Color(0xFF0F6B56).copy(alpha = glowAlpha * 0.20f),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension / 1.85f
                    )
                }

                // ১.২. আউটার রোটেটিং ৮-পয়েন্ট গোল্ডেন অর্বিট
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(outerRotation)
                ) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val rOuter = size.minDimension / 2.05f
                    val rInner = rOuter * 0.88f

                    val path = Path()
                    for (i in 0 until 16) {
                        val angle = Math.toRadians((i * 360.0 / 16.0) - 90.0)
                        val r = if (i % 2 == 0) rOuter else rInner
                        val x = (cx + r * Math.cos(angle)).toFloat()
                        val y = (cy + r * Math.sin(angle)).toFloat()
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

                        // তারকার শীর্ষে সূক্ষ্ম গোল্ডেন নোড
                        if (i % 2 == 0) {
                            drawCircle(
                                color = Color(0xFFFFDF79),
                                radius = 2.5f,
                                center = Offset(x, y)
                            )
                        }
                    }
                    path.close()

                    drawPath(
                        path = path,
                        color = Color(0xFFD4AF37).copy(alpha = 0.55f),
                        style = Stroke(width = 1.6f)
                    )
                }

                // ১.৩. ইনার কাউন্টার-রোটেটিং ড্যাশড রিং
                Canvas(
                    modifier = Modifier
                        .size(174.dp)
                        .rotate(innerRotation)
                ) {
                    drawCircle(
                        color = Color(0xFF34D399).copy(alpha = 0.50f),
                        style = Stroke(
                            width = 1.4f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                        )
                    )
                }

                // ১.৪. মূল অফিশিয়াল ক্বারীয়ানা লোগো মেডেলিয়ন
                Box(
                    modifier = Modifier
                        .size(152.dp)
                        .shadow(24.dp, CircleShape, ambientColor = Color(0xFF021612), spotColor = Color(0xFFD4AF37))
                        .clip(CircleShape)
                        .background(Color(0xFF063328))
                        .border(
                            2.dp,
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFD4AF37),
                                    Color(0xFFFFDF79),
                                    Color(0xFFB4861F),
                                    Color(0xFFFFDF79),
                                    Color(0xFFD4AF37)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kariana_logo),
                        contentDescription = "ক্বারীয়ানা কুরআন লোগো",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            // ==========================================
            // ২. মার্জিত ব্র্যান্ডিং টাইপোগ্রাফি
            // ==========================================
            Text(
                text = "ক্বারীয়ানা অর্থ-কুরআন",
                fontFamily = fonts.kalpurush,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFDF79),
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "ক্বারীয়ানা পাবলিকেশন্স • আন্তর্জাতিক ইসলামিক স্ট্যান্ডার্ড",
                fontFamily = fonts.kalpurush,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFA7F3D0).copy(alpha = 0.90f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))

            // ==========================================
            // ৩. আলট্রা-স্মুথ ইসলামিক প্রগ্রেস শিমার বার
            // ==========================================
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF093B2F)),
                contentAlignment = Alignment.CenterStart
            ) {
                // শিমার ট্রাভেলিং কালার
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val shimmerWidth = w * 0.45f
                    val startX = (w + shimmerWidth) * shimmerOffset - shimmerWidth

                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFFFFDF79),
                                Color(0xFF10B981),
                                Color.Transparent
                            ),
                            startX = startX,
                            endX = startX + shimmerWidth
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // লোডিং স্ট্যাটাস ও পালসিং ডট
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = glowAlpha))
                )
                Text(
                    text = "পবিত্র কুরআন পাঠ্য লোড হচ্ছে...",
                    fontFamily = fonts.kalpurush,
                    fontSize = 12.sp,
                    color = Color(0xFFD4AF37).copy(alpha = 0.95f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * প্রিমিয়াম ইসলামিক লোডিং ইন্ডিকেটর (অ্যাপের যেকোনো স্থানে রিইউজেবল লোডার)
 * অফিশিয়াল ব্র্যান্ড আইডেন্টিটির সাথে ১০০% সমন্বিত
 */
@Composable
fun QarianaLoadingView(
    modifier: Modifier = Modifier,
    message: String = "কুরআন পাঠ্য লোড হচ্ছে...",
    compact: Boolean = false,
    indicatorSize: Dp = if (compact) 54.dp else 72.dp
) {
    val fonts = LocalAppFonts.current
    val transition = rememberInfiniteTransition(label = "qariana_loading_view")

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulse by transition.animateFloat(
        initialValue = 0.93f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(indicatorSize)
                .scale(pulse),
            contentAlignment = Alignment.Center
        ) {
            // রোটেটিং ৮-পয়েন্ট গোল্ডেন ইসলামিক তারকা
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotation)
            ) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val rOuter = size.minDimension / 2.05f
                val rInner = rOuter * 0.72f

                val path = Path()
                for (i in 0 until 16) {
                    val angle = Math.toRadians((i * 360.0 / 16.0) - 90.0)
                    val r = if (i % 2 == 0) rOuter else rInner
                    val x = (cx + r * Math.cos(angle)).toFloat()
                    val y = (cy + r * Math.sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()

                drawPath(
                    path = path,
                    color = Color(0xFFD4AF37),
                    style = Stroke(width = 2f)
                )
            }

            // অফিশিয়াল লোগো সেন্টার
            Box(
                modifier = Modifier
                    .size(indicatorSize * 0.72f)
                    .clip(CircleShape)
                    .background(Color(0xFF063328))
                    .border(1.dp, Color(0xFFD4AF37).copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kariana_logo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(1.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        if (message.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = message,
                fontFamily = fonts.kalpurush,
                fontSize = if (compact) 11.sp else 12.5.sp,
                color = Color(0xFF0F6B56),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
