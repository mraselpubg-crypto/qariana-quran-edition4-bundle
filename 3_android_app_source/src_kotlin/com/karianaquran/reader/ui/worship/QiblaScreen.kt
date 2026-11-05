package com.karianaquran.reader.ui.worship

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.R
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.LocalAppFonts
import kotlin.math.abs

/**
 * ডেডিকেটেড ফুল স্ক্রিন কিবলা কম্পাস (Qibla Compass Dedicated Screen)
 * হার্ডওয়্যার ওরিয়েন্টেশন সেন্সর, স্মুথ অ্যানিমেটেড ডায়াল এবং কাবার কোণ মিললে ভিজ্যুয়াল ইন্ডিকেটর।
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"

    val primaryEmerald = Color(0xFF0F6B56)
    val accentGold = Color(0xFFD4AF37)
    val bgPage = if (isDark) Color(0xFF0B131E) else Color(0xFFF6FFF9)
    val cardBg = if (isDark) Color(0xFF131F2A) else Color.White
    val textPrimary = if (isDark) Color.White else Color(0xFF1E293B)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    // বাংলাদেশের সাধারণ কিবলা কোণ: ২৪২° (পশ্চিম-দক্ষিণ-পশ্চিম)
    val targetQiblaAngle = 242f

    // হার্ডওয়্যার সেন্সর হেডিং
    var deviceHeading by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        if (sensorManager != null && rotSensor != null) {
            val listener = object : SensorEventListener {
                private val rotationMatrix = FloatArray(9)
                private val orientationAngles = FloatArray(3)

                override fun onSensorChanged(event: SensorEvent?) {
                    event ?: return
                    if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)
                        val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                        deviceHeading = (azimuth + 360f) % 360f
                    } else if (event.sensor.type == Sensor.TYPE_ORIENTATION) {
                        deviceHeading = (event.values[0] + 360f) % 360f
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sensorManager.registerListener(listener, rotSensor, SensorManager.SENSOR_DELAY_UI)
            onDispose { sensorManager.unregisterListener(listener) }
        } else {
            onDispose {}
        }
    }

    // স্মুথ ডায়াল রোটেশন
    val animatedHeading by animateFloatAsState(
        targetValue = -deviceHeading,
        animationSpec = spring(stiffness = 300f),
        label = "compass_dial"
    )

    // ডিভাইসের মুখ কাবার সাথে মিলেছে কিনা (৪ ডিগ্রির মধ্যে)
    val relativeAngle = (targetQiblaAngle - deviceHeading + 360f) % 360f
    val isAligned = abs(relativeAngle) <= 4f || abs(relativeAngle - 360f) <= 4f

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgPage,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // ১. টপ অ্যাপ বার (বিকাশ স্ট্যান্ডার্ড সলিড এমারেল্ড হেডার)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = primaryEmerald,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { vm.back() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "পেছনে",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    Column {
                        Text(
                            text = "কিবলা কম্পাস",
                            fontFamily = fonts.kalpurush,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "পবিত্র কাবার সঠিক দিক ও কোণ",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // ২. স্ক্রলযোগ্য কনটেন্ট
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ২.১ কাবার দিকে মুখ করলে অভিনন্দন ব্যানার
                if (isAligned) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = primaryEmerald)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = accentGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "আপনি সঠিক কিবলার অভিমুখে আছেন!",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "এখন নামাজ শুরু করতে পারেন",
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.5.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }

                // ২.২ মূল কম্পাস সার্কেল
                item {
                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .shadow(12.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.2f))
                            .clip(CircleShape)
                            .background(cardBg)
                            .border(
                                3.dp,
                                if (isAligned) accentGold else primaryEmerald.copy(alpha = 0.5f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // ব্যাকগ্রাউন্ড গ্র্যাডিয়েন্ট
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            (if (isAligned) accentGold else primaryEmerald).copy(alpha = 0.08f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // ঘূর্ণায়মান কম্পাস ডায়াল
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.92f)
                                .rotate(animatedHeading),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.compassicon),
                                contentDescription = "কম্পাস ডায়াল",
                                modifier = Modifier.fillMaxSize(0.85f)
                            )
                        }

                        // কাবার দিক নির্দেশক তীর (ফিক্সড কোণ অনুযায়ী নির্দেশ করে)
                        Box(
                            modifier = Modifier
                                .fillMaxSize(0.92f)
                                .rotate(animatedHeading + targetQiblaAngle),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isAligned) accentGold else Color(0xFFDC2626),
                                    modifier = Modifier.size(28.dp),
                                    shadowElevation = 3.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Box(
                                            modifier = Modifier
                                                .size(13.dp)
                                                .background(Color(0xFF0F172A), RoundedCornerShape(2.dp))
                                                .border(0.8.dp, Color(0xFFFFDF79), RoundedCornerShape(2.dp))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 3.dp)
                                                    .fillMaxWidth()
                                                    .height(2.5.dp)
                                                    .background(Color(0xFFD4AF37))
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // সেন্টারে লাইভ ডিগ্রি রিডআউট
                        Surface(
                            shape = CircleShape,
                            color = primaryEmerald,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val deg = (deviceHeading.toInt() % 360).toBn()
                                    Text(
                                        text = "$deg°",
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

                // ২.৩ তথ্য কার্ডসমূহ
                item {
                    Spacer(Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = primaryEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "অবস্থান ও দূরত্বের তথ্য",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            InfoRow(
                                label = "কিবলার আদর্শ কোণ:",
                                value = "${(targetQiblaAngle.toInt()).toBn()}° (পশ্চিম-দক্ষিণ-পশ্চিম)",
                                isDark = isDark
                            )

                            Spacer(Modifier.height(8.dp))

                            InfoRow(
                                label = "মক্কা শরীফের দূরত্ব:",
                                value = "প্রায় ৪,৮৫০ কিলোমিটার",
                                isDark = isDark
                            )

                            Spacer(Modifier.height(8.dp))

                            InfoRow(
                                label = "ডিভাইসের বর্তমান হেডিং:",
                                value = "${(deviceHeading.toInt()).toBn()}°",
                                isDark = isDark
                            )
                        }
                    }
                }

                // ২.৪ ক্যালিব্রেশন নির্দেশিকা
                item {
                    Spacer(Modifier.height(14.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF16251E) else Color(0xFFEFF6F3)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = primaryEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "কম্পাসের নির্ভুলতার জন্য ফোনটি সমতল রাখুন এবং প্রয়োজনে বাতাসে ৪ বা ৮ (8) আকারে কয়েকবার ঘোরান। ধাতব বস্তু থেকে দূরে থাকুন।",
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                color = textSecondary,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    isDark: Boolean,
) {
    val fonts = LocalAppFonts.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = fonts.kalpurush,
            fontSize = 13.sp,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
        )
        Text(
            text = value,
            fontFamily = fonts.kalpurush,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color(0xFF0F6B56)
        )
    }
}
