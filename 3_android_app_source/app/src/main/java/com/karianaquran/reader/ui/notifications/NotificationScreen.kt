package com.karianaquran.reader.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.Screen
import com.karianaquran.reader.ui.theme.LocalAppFonts

data class NotificationItem(
    val id: String,
    val category: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val icon: ImageVector,
    val iconColor: Color,
    val actionText: String? = null,
    val targetScreen: Screen? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"

    var selectedCategory by remember { mutableStateOf("সকল") }

    val notifications = listOf(
        NotificationItem(
            id = "1",
            category = "আপডেট",
            title = "ক্বারীয়ানা অর্থো-কুরআন সংস্করণ ২.০ সক্রিয় হয়েছে!",
            body = "৩০ পারা পূর্ণাঙ্গ রঙিন তাজবীদ মুসহাফ ও ভাসমান শব্দের অর্থ সহ ইনডিজাইন প্রিন্ট রেপ্লিকা এখন অ্যাপে সম্পূর্ণ প্রস্তুত।",
            timeAgo = "১০ মিনিট আগে",
            icon = Icons.Default.AutoAwesome,
            iconColor = Color(0xFF0F6B56),
            actionText = "কুরআন পড়ুন >",
            targetScreen = Screen.QuranOverview(1)
        ),
        NotificationItem(
            id = "2",
            category = "অনুস্মারক",
            title = "আজকের তিলাওয়াত অনুস্মারক",
            body = "রাসূলুল্লাহ ﷺ বলেছেন: 'তোমাদের মধ্যে সর্বোত্তম ব্যক্তি সে, যে নিজে কুরআন শেখে এবং অন্যকে তা শেখায়।' — সহীহ বুখারী",
            timeAgo = "আজ সকাল ৮:০০",
            icon = Icons.Default.NotificationsActive,
            iconColor = Color(0xFFD97706),
            actionText = "তিলাওয়াত শুরু করুন >",
            targetScreen = Screen.QuranOverview(1)
        ),
        NotificationItem(
            id = "3",
            category = "লাইসেন্স",
            title = "ডিভাইস ভেরিফিকেশন ও অফলাইন প্রিভিউ",
            body = "আপনার ডিভাইসে ২০ পৃষ্ঠা পর্যন্ত অফলাইন প্রিভিউ সংরক্ষিত রয়েছে। সম্পূর্ণ বই পড়তে আপনার লাইসেন্স কি প্রবেশ করান।",
            timeAgo = "গতকাল",
            icon = Icons.Default.Verified,
            iconColor = Color(0xFF2563EB),
            actionText = "লগইন করুন >",
            targetScreen = Screen.Login
        ),
        NotificationItem(
            id = "4",
            category = "আপডেট",
            title = "নতুন গ্রন্থ: ক্বারীয়ানা নূরানী কায়দা শীঘ্রই আসছে!",
            body = "মাখরাজ, সিফাত ও রঙিন তাজবীদ চিহ্নের ব্যবহারিক সহজ কুরআন শিক্ষা গাইডের প্রস্তুতি চলছে।",
            timeAgo = "২ দিন আগে",
            icon = Icons.Default.Star,
            iconColor = Color(0xFF7C3AED),
            actionText = "লাইব্রেরি দেখুন >",
            targetScreen = Screen.Library
        ),
        NotificationItem(
            id = "5",
            category = "অনুস্মারক",
            title = "শুক্রবার জুমু'আর বিশেষ তিলাওয়াত",
            body = "সূরা আল-কাহাফ পাঠের ফজিলত অপরিসীম। নূর দিয়ে দুই জুমু'আর মধ্যবর্তী সময় আলোকিত হয়।",
            timeAgo = "৩ দিন আগে",
            icon = Icons.Default.CalendarMonth,
            iconColor = Color(0xFF059669)
        )
    )

    val categories = listOf("সকল", "আপডেট", "অনুস্মারক", "লাইসেন্স")

    val filteredList = if (selectedCategory == "সকল") notifications else notifications.filter { it.category == selectedCategory }

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
            // ১. হেডার (বিকাশ অ্যাপের মতো পারফেক্ট স্ট্যাটাস বার ও ব্যাক বাটন)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
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
                                text = "বিজ্ঞপ্তি ও নোটিফিকেশন",
                                fontFamily = fonts.kalpurush,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "অ্যাপ আপডেট, দৈনিক হাদিস ও অনুস্মারক",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "সব পঠিত",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // ২. ক্যাটাগরি ফিল্টার চিপস
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color(0xFF0F6B56) else (if (isDark) Color(0xFF16251E) else Color.White),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF0F6B56) else (if (isDark) Color(0xFF264034) else Color(0xFFE2E8F0))
                        ),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else (if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155)),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // ৩. নোটিফিকেশন কার্ড তালিকা
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF13221A) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(item.iconColor.copy(alpha = 0.14f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = item.iconColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(Modifier.width(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                item.iconColor.copy(alpha = 0.12f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = item.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = item.iconColor
                                        )
                                    }
                                }

                                Text(
                                    text = item.timeAgo,
                                    fontSize = 10.5.sp,
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF94A3B8)
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = item.title,
                                fontFamily = fonts.kalpurush,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = item.body,
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                                lineHeight = 17.sp
                            )

                            if (item.actionText != null && item.targetScreen != null) {
                                Spacer(Modifier.height(12.dp))

                                Button(
                                    onClick = { vm.navigate(item.targetScreen) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(
                                        text = item.actionText,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
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
