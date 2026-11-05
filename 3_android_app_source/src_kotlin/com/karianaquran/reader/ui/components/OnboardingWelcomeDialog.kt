package com.karianaquran.reader.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * ক্বারীয়ানা কুরআন - পূর্ণাঙ্গ ৩-ধাপের আকর্ষণীয় অনবোর্ডিং ফিচার গাইড ও নোটিফিকেশন অনুমোদন ডায়ালগ।
 */
@Composable
fun OnboardingWelcomeDialog(
    onAllow: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    var currentStep by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onSkip,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            color = Color.White,
            shadowElevation = 14.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // ১. আকর্ষণীয় ইসলামিক গ্রিন হেডার ব্যানার
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0F6B56), Color(0xFF073F37))
                            )
                        )
                        .padding(vertical = 22.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD4AF37).copy(alpha = 0.22f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD4AF37)),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val headerIcon = when (currentStep) {
                                    0 -> Icons.Default.MenuBook
                                    1 -> Icons.Default.Palette
                                    else -> Icons.Default.NotificationsActive
                                }
                                Icon(
                                    imageVector = headerIcon,
                                    contentDescription = null,
                                    tint = Color(0xFFFFDF79),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = when (currentStep) {
                                0 -> "ক্বারীয়ানা কুরআনে স্বাগতম!"
                                1 -> "৩টি অনন্য পাঠ সংস্করণ"
                                else -> "দৈনিক ইবাদত ও ওয়াক্ত অ্যালার্ট"
                            },
                            fontFamily = fonts.kalpurush,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ধাপ  / ৩ — অ্যাপ গাইড ও পরিচিতি",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            color = Color(0xFFFDE68A)
                        )
                    }
                }

                // ২. স্লাইড কন্টেন্ট (অ্যানিমেটেড ট্রানজিশন)
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "guide_step"
                ) { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        when (step) {
                            0 -> {
                                Text(
                                    text = "ক্বারীয়ানা অর্থো-কুরআন — আন্তর্জাতিক মানের রঙিন তাজবীদ মুসহাফ ও অর্থসহ বিশুদ্ধ কুরআন শিক্ষা:",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 17.sp
                                )
                                GuideFeatureRow(
                                    icon = Icons.Default.AutoStories,
                                    title = "রঙিন তাজবীদ ও শব্দের অর্থ",
                                    desc = "মাদ্দ, গুন্নাহ, ইখফা স্পষ্ট রঙে চিহ্নিত এবং প্রতিটি আরবি শব্দের নিচে বাংলা উচ্চারণ ও অর্থ।"
                                )
                                GuideFeatureRow(
                                    icon = Icons.Default.MenuBook,
                                    title = "ইন্টারেক্টিভ শব্দ পপআপ",
                                    desc = "কুরআনের যেকোনো শব্দে ট্যাপ করলেই ভেসে উঠবে বড় হরফে উচ্চারণ, অর্থ ও তাজবীদ বিধান।"
                                )
                            }
                            1 -> {
                                Text(
                                    text = "আপনার পড়ার সুবিধার্থে রিডারে রয়েছে ৩টি স্বতন্ত্র সংস্করণ ও ৬টি চোখের জন্য আরামদায়ক থিম:",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 17.sp
                                )
                                GuideFeatureRow(
                                    icon = Icons.Default.MenuBook,
                                    title = "মুসহাফ পৃষ্ঠা ভিউ (Page View)",
                                    desc = "ইনডিজাইন প্রিন্ট পেজের মতো অবিকল লেআউট ও ফুলস্ক্রিন রিডিং মোড।"
                                )
                                GuideFeatureRow(
                                    icon = Icons.Default.Palette,
                                    title = "আয়াত ও ক্লাসিক প্রেস সংস্করণ",
                                    desc = "আয়াতভিত্তিক কার্ড অনুবাদ ভিউ এবং মূল ছাপা বইয়ের অবিকল ঐতিহ্যবাহী ক্লাসিক সংস্করণ।"
                                )
                            }
                            2 -> {
                                Text(
                                    text = "ইবাদতের নিয়মিত রিমাইন্ডার ও নির্ভুল আযান অ্যালার্ট পেতে নোটিফিকেশন অনুমোদন করুন:",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 17.sp
                                )
                                GuideFeatureRow(
                                    icon = Icons.Default.Timer,
                                    title = "নামাজের সময়সূচি ও আযান",
                                    desc = "৬৪ জেলার নির্ভুল নামাজের ওয়াক্ত এবং ওয়াক্তের পূর্বে সতর্কবার্তা।"
                                )
                                GuideFeatureRow(
                                    icon = Icons.Default.CompassCalibration,
                                    title = "কিবলা কম্পাস, তসবিহ ও দোয়া",
                                    desc = "সঠিক কিবলা দিকনির্দেশনা, ডিজিটাল তসবিহ কাউন্টার এবং সহীহ দোয়ার ভাণ্ডার।"
                                )
                            }
                        }
                    }
                }

                // ৩. ডট ইন্ডিকেটর
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { i ->
                        val isSel = i == currentStep
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (isSel) 20.dp else 7.dp, height = 7.dp)
                                .clip(CircleShape)
                                .background(if (isSel) Color(0xFF0F6B56) else Color(0xFFCBD5E1))
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ৪. নেভিগেশন ও অ্যাকশন বাটনসমূহ
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentStep < 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (currentStep > 0) {
                                OutlinedButton(
                                    onClick = { currentStep-- },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(22.dp)
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("পেছনে", fontFamily = fonts.kalpurush, fontSize = 13.sp)
                                }
                            }
                            Button(
                                onClick = { currentStep++ },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
                            ) {
                                Text("পরবর্তী", fontFamily = fonts.kalpurush, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(Modifier.width(4.dp))
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    } else {
                        Button(
                            onClick = onAllow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(23.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
                        ) {
                            Text(
                                text = "অনুমতি দিন ও তিলাওয়াত শুরু করুন",
                                fontFamily = fonts.kalpurush,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        TextButton(
                            onClick = onSkip,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "সরাসরি শুরু করুন (পরে অনুমতি দেব)",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideFeatureRow(
    icon: ImageVector,
    title: String,
    desc: String,
) {
    val fonts = LocalAppFonts.current

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F6B56).copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF0F6B56),
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(Modifier.width(11.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = fonts.kalpurush,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = desc,
                fontFamily = fonts.kalpurush,
                fontSize = 11.5.sp,
                color = Color(0xFF64748B),
                lineHeight = 16.sp
            )
        }
    }
}
