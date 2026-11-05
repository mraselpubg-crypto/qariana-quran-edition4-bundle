package com.karianaquran.reader.ui.worship

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts

data class DhikrItem(
    val arabic: String,
    val translit: String,
    val meaning: String,
    val defaultTarget: Int = 33
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val context = LocalContext.current

    var dhikrList by remember {
        mutableStateOf(
            listOf(
                DhikrItem("سُبْحَانَ اللَّهِ", "সুবহানাল্লাহ", "আল্লাহ মহাপবিত্র", 33),
                DhikrItem("الْحَمْدُ لِلَّهِ", "আলহামদুলিল্লাহ", "সকল প্রশংসা আল্লাহর", 33),
                DhikrItem("اللَّهُ أَكْبَرُ", "আল্লাহু আকবার", "আল্লাহ মহান", 34),
                DhikrItem("لَا إِلٰهَ إِلَّا اللَّهُ", "লা ইলাহা ইল্লাল্লাহ", "আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই", 100),
                DhikrItem("أَسْتَغْفِرُ اللَّهَ", "আস্তাগফিরুল্লাহ", "আমি আল্লাহর কাছে ক্ষমা চাই", 100),
                DhikrItem("اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", "সাল্লাল্লাহু আলাইহি ওয়াসাল্লাম", "মুহাম্মাদ (সা.)-এর উপর দরূদ", 100)
            )
        )
    }

    var showCustomDhikrDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var customDhikrText by remember { mutableStateOf("") }
    var customDhikrMeaning by remember { mutableStateOf("") }
    var customTargetText by remember { mutableStateOf("100") }

    var selectedDhikrIdx by remember { mutableIntStateOf(0) }
    val currentDhikr = dhikrList[selectedDhikrIdx]

    var count by remember { mutableIntStateOf(0) }
    var target by remember { mutableIntStateOf(currentDhikr.defaultTarget) }
    var totalSessionCount by remember { mutableIntStateOf(0) }
    var isVibrationEnabled by remember { mutableStateOf(true) }

    fun triggerVibration() {
        if (!isVibrationEnabled) return
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                v?.vibrate(35)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isDark) Color(0xFF0A120E) else Color(0xFFF8FAF9),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // ১. বিকাশ অ্যাপ স্ট্যান্ডার্ড টপ বার (#0F6B56)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F6B56),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { vm.back() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "পেছনে যান",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(Modifier.width(4.dp))

                        Column {
                            Text(
                                text = "ডিজিটাল তাসবীহ",
                                fontFamily = fonts.kalpurush,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "দৈনিক জিকির ও তাসবীহ গণনা",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // ভাইব্রেশন ও সাউন্ড টগল
                    IconButton(onClick = { isVibrationEnabled = !isVibrationEnabled }) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "ভাইব্রেশন টগল",
                            tint = if (isVibrationEnabled) Color(0xFFFDE68A) else Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // ২. জিকির অনুভূমিক নির্বাচন চিপস
            Spacer(Modifier.height(14.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
            ) {
                items(dhikrList.indices.toList()) { idx ->
                    val item = dhikrList[idx]
                    val isSelected = idx == selectedDhikrIdx
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFF0F6B56) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF0F6B56) else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.clickable {
                            selectedDhikrIdx = idx
                            target = item.defaultTarget
                            count = 0
                        }
                    ) {
                        Text(
                            text = item.translit,
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }

                // কাস্টম জিকির যোগ করার বাটন (+)
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF0F6B56).copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0F6B56)),
                        modifier = Modifier.clickable { showCustomDhikrDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "নতুন জিকির যোগ", tint = Color(0xFF0F6B56), modifier = Modifier.size(15.dp))
                            Text(
                                text = "কাস্টম জিকির",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F6B56)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ৩. নির্বাচিত জিকির প্রদর্শন কার্ড
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentDhikr.arabic,
                        fontFamily = fonts.arabic,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F6B56),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = currentDhikr.translit,
                        fontFamily = fonts.kalpurush,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currentDhikr.meaning,
                        fontFamily = fonts.kalpurush,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // ৪. ডিজিটাল কাউন্টার সার্কেল বাটন
            val progress = if (target > 0) (count.toFloat() / target).coerceIn(0f, 1f) else 0f
            val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // সার্কুলার প্রগ্রেস ট্র্যাক
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFE2E8F0),
                    strokeWidth = 10.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F6B56),
                    strokeWidth = 10.dp,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                // বিশাল স্পর্শ বাটন
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0F6B56), Color(0xFF0A4F3F))
                            )
                        )
                        .border(3.dp, Color(0xFFD4AF37), CircleShape)
                        .clickable {
                            triggerVibration()
                            count++
                            totalSessionCount++
                            if (count >= target && target > 0) {
                                // গোল পূর্ণ হলে ডাবল ভাইব্রেশন
                                triggerVibration()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = count.toBn(),
                            fontFamily = fonts.kalpurush,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "লক্ষ্য: ${target.toBn()}",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.sp,
                            color = Color(0xFFFDE68A)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "স্পর্শ করুন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ৫. বটম কন্ট্রোল: রিসেট বাটন ও মোট সংখ্যা
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "মোট পাঠ: ${totalSessionCount.toBn()} বার",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "বর্তমান চক্র: ${(count / (if (target > 0) target else 1)).toBn()} সম্পন্ন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // রিসেট বাটন
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f)),
                        modifier = Modifier.clickable {
                            if (count > 0) {
                                showResetConfirmDialog = true
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Refresh, null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "রিসেট",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }
        }
    }

    // কাস্টম জিকির যোগ ডায়লগ
    if (showCustomDhikrDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDhikrDialog = false },
            title = {
                Text(
                    text = "কাস্টম জিকির যোগ করুন",
                    fontFamily = fonts.kalpurush,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F6B56)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = customDhikrText,
                        onValueChange = { customDhikrText = it },
                        label = { Text("জিকিরের নাম / উচ্চারণ", fontFamily = fonts.kalpurush) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customDhikrMeaning,
                        onValueChange = { customDhikrMeaning = it },
                        label = { Text("অর্থ বা নিয়ত (ঐচ্ছিক)", fontFamily = fonts.kalpurush) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customTargetText,
                        onValueChange = { customTargetText = it },
                        label = { Text("লক্ষ্য সংখ্যা (যেমন: 33, 100, 1000)", fontFamily = fonts.kalpurush) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customDhikrText.isNotBlank()) {
                            val t = customTargetText.toIntOrNull() ?: 100
                            val newItem = DhikrItem(
                                arabic = customDhikrText.trim(),
                                translit = customDhikrText.trim(),
                                meaning = customDhikrMeaning.ifBlank { "ব্যক্তিগত কাস্টম জিকির" },
                                defaultTarget = t
                            )
                            dhikrList = dhikrList + newItem
                            selectedDhikrIdx = dhikrList.size - 1
                            target = t
                            count = 0
                            showCustomDhikrDialog = false
                            customDhikrText = ""
                            customDhikrMeaning = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
                ) {
                    Text("যুক্ত করুন", fontFamily = fonts.kalpurush)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDhikrDialog = false }) {
                    Text("বাতিল", fontFamily = fonts.kalpurush)
                }
            }
        )
    }

    // রিসেট নিশ্চিতকরণ ডায়লগ
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(
                    text = "তাসবীহ রিসেট নিশ্চিতকরণ",
                    fontFamily = fonts.kalpurush,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )
            },
            text = {
                Text(
                    text = "আপনি কি বর্তমান পাঠ গণনা (${count.toBn()}) শূন্য (০) তে রিসেট করতে চান?",
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.5.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        count = 0
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("হ্যাঁ, রিসেট করুন", fontFamily = fonts.kalpurush, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("বাতিল", fontFamily = fonts.kalpurush)
                }
            }
        )
    }
}
