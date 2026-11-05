package com.karianaquran.reader.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * ডায়ালগের ধরণ / স্টাইল
 */
enum class ModernDialogType {
    SUCCESS,
    INFO,
    WARNING,
    ERROR
}

/**
 * আধুনিক আন্তর্জাতিক ইসলামিক স্ট্যান্ডার্ড মডাল নোটিশ ডায়ালগ
 * রেফারেন্স স্ক্রিনশটের আদলে সফট ৩ডি আইকন ব্যাজ, সেন্টার্ড টাইটেল ও স্লিক ব্ল্যাক পিল বাটন
 */
@Composable
fun ModernNoticeDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    type: ModernDialogType = ModernDialogType.INFO,
    confirmText: String = "ঠিক আছে",
    onConfirm: () -> Unit = onDismissRequest,
    dismissText: String? = null,
    isDark: Boolean = false,
    customIcon: ImageVector? = null,
) {
    val fonts = LocalAppFonts.current

    val (badgeBg, iconColor, defaultIcon) = when (type) {
        ModernDialogType.SUCCESS -> Triple(
            if (isDark) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFFD1FAE5),
            Color(0xFF10B981),
            Icons.Default.Check
        )
        ModernDialogType.INFO -> Triple(
            if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.4f) else Color(0xFFDBEAFE),
            Color(0xFF3B82F6),
            Icons.Default.Info
        )
        ModernDialogType.WARNING -> Triple(
            if (isDark) Color(0xFF78350F).copy(alpha = 0.45f) else Color(0xFFFEF3C7),
            Color(0xFFF59E0B),
            Icons.Default.PriorityHigh
        )
        ModernDialogType.ERROR -> Triple(
            if (isDark) Color(0xFF7F1D1D).copy(alpha = 0.45f) else Color(0xFFFEE2E2),
            Color(0xFFEF4444),
            Icons.Default.Warning
        )
    }

    val cardBg = if (isDark) Color(0xFF11221B) else Color.White
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val btnColor = if (isDark) Color(0xFF0F6B56) else Color(0xFF091712)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = cardBg,
            border = BorderStroke(1.dp, cardBorder),
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(22.dp)) {
                // টপ-রাইট ক্লোজ বাটন
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "বন্ধ করুন",
                        tint = textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(4.dp))

                    // সফট ৩ডি আইকন সার্কেল
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(badgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = customIcon ?: defaultIcon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // টাইটেল
                    Text(
                        text = title,
                        fontFamily = fonts.kalpurush,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    // মেসেজ
                    Text(
                        text = message,
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Spacer(Modifier.height(22.dp))

                    // প্রধান অ্যাকশন বাটন (স্লিক ফুল-উইডথ পিল)
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = btnColor)
                    ) {
                        Text(
                            text = confirmText,
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // ঐচ্ছিক সেকেন্ডারি বাটন
                    if (dismissText != null) {
                        Spacer(Modifier.height(6.dp))
                        TextButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Text(
                                text = dismissText,
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.5.sp,
                                color = textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

