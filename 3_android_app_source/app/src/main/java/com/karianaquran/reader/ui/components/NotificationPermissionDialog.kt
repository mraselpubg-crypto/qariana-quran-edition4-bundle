package com.karianaquran.reader.ui.components

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * ক্বারীয়ানা কুরআন — নোটিফিকেশন পারমিশন রিকোয়েস্ট পপআপ
 * ইউজারকে সুস্পষ্টভাবে অবগত করে যাতে নামাজের আযান ও অডিও সাউন্ড
 * কোন অ্যাপ থেকে বাজছে তা নোটিফিকেশনে দেখতে ও সহজেই নিয়ন্ত্রণ করতে পারেন।
 */
@Composable
fun NotificationPermissionDialog(
    onDismiss: () -> Unit,
    onGranted: () -> Unit = {}
) {
    val context = LocalContext.current
    val fonts = LocalAppFonts.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
            onDismiss()
        } else {
            // সেটিংস পেইজে পাঠানোর ব্যবস্থা
            runCatching {
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                } else {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    }
                }
                context.startActivity(intent)
            }
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = Color(0xFFFBF9F5),
        icon = {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F6B56).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = Color(0xFF0F6B56),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "নোটিফিকেশন সক্রিয় করুন",
                fontFamily = fonts.kalpurush,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D1F17)
            )
        },
        text = {
            Column {
                Text(
                    text = "ক্বারীয়ানা কুরআনে নামাজের ওয়াক্তে আযান ও তিলাওয়াতের শব্দ শুনতে নোটিফিকেশন চালু রাখা আবশ্যক।",
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "নোটিফিকেশন সক্রিয় থাকলে কোন অ্যাপ থেকে আজান বা তিলাওয়াত বাজছে তা আপনি সরাসরি স্ক্রিনে দেখতে পারবেন এবং ১-ট্যাপে আজান বন্ধ করতে পারবেন।",
                    fontFamily = fonts.kalpurush,
                    fontSize = 12.sp,
                    color = Color(0xFF0F6B56),
                    lineHeight = 17.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        // Android 12 ও নিচে সেটিংস ওপেন করা
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "অনুমতি দিন",
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "পরে করুন",
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    )
}
