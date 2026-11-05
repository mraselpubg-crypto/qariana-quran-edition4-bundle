package com.karianaquran.reader.ui.home

import androidx.compose.ui.graphics.Color

/**
 * হোম স্ক্রিন ও তার child কম্পোনেন্টগুলোতে ব্যবহৃত শেয়ারড ডিজাইন টোকেন।
 *
 * ⚠️ কেন আলাদা ফাইল: HomeScreen.kt (৩৪০০+ লাইন) ভাগ করার সময় সব child কম্পোনেন্ট
 * এই একই রঙ ব্যবহার করে। একই `com.karianaquran.reader.ui.home` প্যাকেজে রাখায়
 * HomeScreen.kt-এ কোনো import যোগ না করেই বিদ্যমান ৭৭টি ব্যবহার অপরিবর্তিত থাকে।
 *
 * নতুন টোকেন `ui/theme/QColors.kt`-এ যোগ হবে; এগুলো হোম-স্থানীয় অ্যালিয়াস।
 */
internal val PrimaryEmerald = Color(0xFF0F6F52)
internal val PrimaryDarkEmerald = Color(0xFF36805F)
internal val ScreenBgLight = Color(0xFFF6FFF9)
internal val ScreenBgDark = Color(0xFF0B131E)
internal val LightBlackText = Color(0xFF333333)
internal val MutedText = Color(0xFF6B7280)
internal val PrayerGold = Color(0xFFFFD166)
internal val AccentMint = Color(0xFFA7F3D0)
internal val BorderSubtle = Color(0xFFE5E7EB)
