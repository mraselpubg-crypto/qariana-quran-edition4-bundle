package com.karianaquran.reader.ui.profile

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.theme.LocalAppFonts

private data class IslamicAvatarOption(
    val id: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val titleBn: String,
    val titleEn: String
)

/**
 * পূর্ণাঙ্গ ডেডিকেটেড এডিট প্রোফাইল স্ক্রিন (১০ জন ভাই ও ১০ জন বোনদের ইসলামিক কার্টুন অবতার + ইসলামিক শালীনতা গাইডেন্স মডাল)
 */
@Composable
fun EditProfileScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"
    val isEnglish = readerSettings.appLanguage == "en"
    val session = vm.session

    val context = androidx.compose.ui.platform.LocalContext.current
    val defaultName = if (session?.mobile == "01906655779") "রাসেল গাজী" else if (session?.mobile == "01717056816") "সাদ্দাম হোসেন" else "ক্বারীয়ানা পাঠক"
    val savedCustomerName: String = remember { UserProfileHelper.getProfileName(context, defaultName) }
    var editName by remember { mutableStateOf<String>(savedCustomerName) }
    var editPhone by remember { mutableStateOf(session?.mobile ?: "01906655779") }
    var editEmail by remember { mutableStateOf(if (session?.mobile == "01906655779") "rasel@karianaquran.com" else "") }
    var editDob by remember { mutableStateOf("15/05/1996") }

    // জেন্ডার ও অবতার স্টেট
    var selectedGender by remember { mutableStateOf(UserProfileHelper.getSavedGender(context)) } // "male" or "female"
    var selectedAvatarId by remember { mutableIntStateOf(UserProfileHelper.getSavedAvatarId(context)) }
    var selectedAvatarEmoji by remember { mutableStateOf(UserProfileHelper.getSavedAvatarEmoji(context)) }
    var customPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showModestyDialog by remember { mutableStateOf(false) }

    // গ্যালারি থেকে ছবি নির্বাচনের ফটো পিকার
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            customPhotoUri = uri
            vm.notice = if (isEnglish) "Profile picture selected!" else "প্রোফাইল ছবি নির্বাচন করা হয়েছে!"
        }
    }

    // ১০ জন ভাইদের ইসলামিক অবতার (ভেক্টর আইকন)
    val brotherAvatars = remember {
        listOf(
            IslamicAvatarOption(1, Icons.Default.Person, "সুন্নতি পাগড়ি", "Sunnah Turban"),
            IslamicAvatarOption(2, Icons.Default.CheckCircle, "শায়খ ও হাফেজ", "Shaykh & Hafez"),
            IslamicAvatarOption(3, Icons.Default.Star, "বিনম্র মুসল্লি", "Humble Musalli"),
            IslamicAvatarOption(4, Icons.Default.Security, "মার্জিত মুমিন", "Modest Professional"),
            IslamicAvatarOption(5, Icons.Default.Person, "তালিবুল ইলম", "Student of Knowledge"),
            IslamicAvatarOption(6, Icons.Default.Star, "মসজিদ অনুরাগী", "Mosque Lover"),
            IslamicAvatarOption(7, Icons.Default.CheckCircle, "কুরআন তিলাওয়াতকারী", "Quran Reciter"),
            IslamicAvatarOption(8, Icons.Default.Star, "হাজী ও মুসাফির", "Hajji Pilgrim"),
            IslamicAvatarOption(9, Icons.Default.Person, "স্নিগ্ধ মুত্তাকী", "Serene Mumin"),
            IslamicAvatarOption(10, Icons.Default.Star, "আলোকিত মুমিন", "Enlightened Mumin")
        )
    }

    // ১০ জন বোনদের ইসলামিক অবতার (ভেক্টর আইকন)
    val sisterAvatars = remember {
        listOf(
            IslamicAvatarOption(11, Icons.Default.Person, "হিযাব পরিহিতা", "Hijabi Sister"),
            IslamicAvatarOption(12, Icons.Default.CheckCircle, "শুভ্র স্নিগ্ধ হিযাব", "Pure White Hijab"),
            IslamicAvatarOption(13, Icons.Default.Star, "মার্জিত হিযাব", "Modern Modest"),
            IslamicAvatarOption(14, Icons.Default.Security, "বিনম্র মুসলিমাহ", "Modest Muslimah"),
            IslamicAvatarOption(15, Icons.Default.Person, "নামাজরত বোন", "Devoted Sister"),
            IslamicAvatarOption(16, Icons.Default.CheckCircle, "মুআল্লিমা ও হাফেজা", "Quran Teacher"),
            IslamicAvatarOption(17, Icons.Default.Star, "শান্তিময়ী বোন", "Serene Muslimah"),
            IslamicAvatarOption(18, Icons.Default.Security, "হাজ্জাহ ও মুতামিরা", "Hajjah Pilgrim"),
            IslamicAvatarOption(19, Icons.Default.Person, "নেককার মুসলিমাহ", "Pious Muslimah"),
            IslamicAvatarOption(20, Icons.Default.Star, "আলোকিত বোন", "Graceful Sister")
        )
    }

    BackHandler { vm.back() }

    val bg = if (isDark) Color(0xFF0A120E) else Color(0xFFF8FAF9)
    val cardBg = if (isDark) Color(0xFF13221A) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    val subTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
    ) {
        // ১. টপ বার
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            color = Color(0xFF0F6B56),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { vm.back() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "পেছনে", tint = Color.White)
                }

                Text(
                    text = if (isEnglish) "Edit Profile" else "প্রোফাইল সম্পাদনা",
                    fontFamily = fonts.kalpurush,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )

                IconButton(
                    onClick = {
                        UserProfileHelper.saveProfile(context, editName.trim(), editEmail.trim())
                        UserProfileHelper.saveAvatar(context, selectedGender, selectedAvatarId, selectedAvatarEmoji)
                        vm.notice = if (isEnglish) "Profile updated successfully!" else "প্রোফাইল সফলভাবে আপডেট করা হয়েছে!"
                        vm.back()
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = "সংরক্ষণ", tint = Color(0xFFFDE68A))
                }
            }
        }

        // ২. স্ক্রলযোগ্য ফর্ম
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // অবতার প্রিভিউ ও ক্যামেরা বাটন
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = if (selectedGender == "female") Color(0xFFFCE7F3) else Color(0xFFDCFCE7),
                    border = BorderStroke(3.dp, Color(0xFFD4AF37)),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = UserProfileHelper.getAvatarIcon(selectedAvatarId),
                            contentDescription = null,
                            tint = Color(0xFF0F6B56),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable { showModestyDialog = true },
                    shape = CircleShape,
                    color = Color(0xFF0F6B56),
                    border = BorderStroke(2.dp, Color.White),
                    shadowElevation = 3.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "ছবি পরিবর্তন",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            // জেন্ডার সিলেকশন পিল
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .clickable {
                            selectedGender = "male"
                            selectedAvatarId = 1
                            selectedAvatarEmoji = "brother"
                        },
                    shape = RoundedCornerShape(22.dp),
                    color = if (selectedGender == "male") Color(0xFF0F6B56) else cardBg,
                    border = BorderStroke(
                        1.5.dp,
                        if (selectedGender == "male") Color(0xFF0F6B56) else Color(0xFFCBD5E1)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (selectedGender == "male") Color.White else textColor,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "Brother" else "ভাই (Brother)",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedGender == "male") Color.White else textColor
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .clickable {
                            selectedGender = "female"
                            selectedAvatarId = 11
                            selectedAvatarEmoji = "sister"
                        },
                    shape = RoundedCornerShape(22.dp),
                    color = if (selectedGender == "female") Color(0xFF0F6B56) else cardBg,
                    border = BorderStroke(
                        1.5.dp,
                        if (selectedGender == "female") Color(0xFF0F6B56) else Color(0xFFCBD5E1)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (selectedGender == "female") Color.White else textColor,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "Sister" else "বোন (Sister)",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedGender == "female") Color.White else textColor
                        )
                    }
                }
            }

            // ১০টি ইসলামিক অবতারের ক্যারোসেল নির্বাচন
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = cardBg,
                border = BorderStroke(1.dp, if (isDark) Color(0xFF1E2E26) else Color(0xFFE2E8F0)),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isEnglish) "Select Islamic Cartoon Avatar (10 Choices)" else "ইসলামিক অবতার নির্বাচন করুন (১০টি পছন্দ):",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F6B56)
                    )
                    Spacer(Modifier.height(10.dp))

                    val currentList = if (selectedGender == "female") sisterAvatars else brotherAvatars

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        items(currentList) { av ->
                            val isChosen = selectedAvatarId == av.id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        selectedAvatarId = av.id
                                        selectedAvatarEmoji = "avatar_${av.id}"
                                    }
                            ) {
                                Surface(
                                    modifier = Modifier.size(56.dp),
                                    shape = CircleShape,
                                    color = if (isChosen) (if (isDark) Color(0xFF1B3D31) else Color(0xFFD1FAE5)) else (if (isDark) Color(0xFF1A2621) else Color(0xFFF1F5F9)),
                                    border = BorderStroke(
                                        if (isChosen) 2.5.dp else 1.dp,
                                        if (isChosen) Color(0xFFD4AF37) else (if (isDark) Color(0xFF2E3D35) else Color(0xFFE2E8F0))
                                    ),
                                    shadowElevation = if (isChosen) 3.dp else 0.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = av.icon,
                                            contentDescription = null,
                                            tint = if (isChosen) Color(0xFF0F6B56) else subTextColor,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = if (isEnglish) av.titleEn else av.titleBn,
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isChosen) Color(0xFF0F6B56) else subTextColor,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // ইনপুট ফর্ম কার্ড
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = cardBg,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, if (isDark) Color(0xFF1E2E26) else Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // নাম
                    Column {
                        Text(
                            text = if (isEnglish) "Full Name" else "পূর্ণ নাম",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = subTextColor
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    // মোবাইল
                    Column {
                        Text(
                            text = if (isEnglish) "Mobile Number" else "মোবাইল নম্বর",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = subTextColor
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Text("BD +880", fontFamily = fonts.kalpurush, fontWeight = FontWeight.Bold, color = Color(0xFF0F6B56), modifier = Modifier.padding(start = 10.dp))
                            },
                            singleLine = true
                        )
                    }

                    // জন্মতারিখ
                    Column {
                        Text(
                            text = if (isEnglish) "Date of Birth" else "জন্ম তারিখ (DD/MM/YYYY)",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = subTextColor
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editDob,
                            onValueChange = { editDob = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    // ইমেইল
                    Column {
                        Text(
                            text = if (isEnglish) "Email Address" else "ইমেইল ঠিকানা",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = subTextColor
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }
            }

            // সংরক্ষণ বাটন
            Button(
                onClick = {
                    UserProfileHelper.saveProfile(context, editName.trim(), editEmail.trim())
                    UserProfileHelper.saveAvatar(context, selectedGender, selectedAvatarId, selectedAvatarEmoji)
                    vm.notice = if (isEnglish) "Profile updated successfully!" else "প্রোফাইল সফলভাবে আপডেট করা হয়েছে!"
                    vm.back()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
            ) {
                Text(
                    text = if (isEnglish) "Save Changes" else "পরিবর্তন সংরক্ষণ করুন",
                    fontFamily = fonts.kalpurush,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    // ইসলামিক পর্দা ও শালীনতা বিষয়ক নির্দেশনা ডায়ালগ
    if (showModestyDialog) {
        AlertDialog(
            onDismissRequest = { showModestyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF0F6B56), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isEnglish) "Islamic Modesty Guidance" else "ইসলামিক শালীনতা ও পর্দার বিধান",
                        fontFamily = fonts.kalpurush,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F6B56)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isEnglish)
                            "Qariana Quran is a sacred Islamic platform dedicated to the Holy Quran. Please ensure any custom profile picture adheres to Islamic modesty (Pardah / Hijab for sisters, and modest respectful attire for brothers). Using the built-in Islamic avatars is highly recommended."
                        else
                            "ক্বারীয়ানা কুরআন মহান আল্লাহর কালামুল্লাহ তিলাওয়াতের একটি পবিত্র মাধ্যম। এখানে নিজস্ব ছবি ব্যবহারের ক্ষেত্রে অবশ্যই ইসলামিক শালীনতা ও পর্দার বিধান (বোনদের ক্ষেত্রে যথাযথ হিযাব/শালীনতা এবং ভাইদের ক্ষেত্রে মার্জিত রূপ) বজায় রাখার অনুরোধ করা হচ্ছে। অ্যাপের তৈরি সুন্দর ইসলামিক কার্টুন অবতারসমূহ ব্যবহার করাই সর্বোত্তম।",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                        lineHeight = 19.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showModestyDialog = false
                        photoPickerLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isEnglish) "I Understand • Upload Photo" else "মেনে চলছি • ছবি নির্বাচন করুন",
                        fontFamily = fonts.kalpurush,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showModestyDialog = false }) {
                    Text(
                        text = if (isEnglish) "Use Islamic Avatars" else "ইসলামিক অবতার ব্যবহার করুন",
                        fontFamily = fonts.kalpurush,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F6B56)
                    )
                }
            }
        )
    }
}
