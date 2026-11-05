package com.karianaquran.reader.ui.pay

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.AppViewModel
import com.karianaquran.reader.ui.PayUi
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayScreen(
    vm: AppViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fonts = LocalAppFonts.current
    val payUi = vm.payUi
    val readerSettings by vm.settings.collectAsState()
    val isDark = readerSettings.themeId == "dark"

    var selectedPackageId by remember(vm.pendingPackageId) {
        mutableStateOf(vm.pendingPackageId ?: "standard")
    }
    var mobileInput by remember {
        mutableStateOf(vm.session?.mobile?.removePrefix("+880") ?: "")
    }

    val selectedPkg = QARIANA_PACKAGES.firstOrNull { it.id == selectedPackageId } ?: QARIANA_PACKAGES[1]

    val bgColor = if (isDark) Brand.Obsidian else Color(0xFFF8FAF9)
    val topBarColor = if (isDark) Brand.Obsidian else Color(0xFF064E3B)
    val titleColor = if (isDark) Brand.Gold else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    val textSubColor = if (isDark) Color.White.copy(alpha = 0.75f) else Color(0xFF475569)

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize().imePadding(),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarColor,
                    titleContentColor = titleColor
                ),
                title = {
                    Text(
                        text = "ক্বারীয়ানা কুরআন কিনুন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        vm.resetPay()
                        vm.back()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান",
                            tint = if (isDark) Brand.Gold else Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            when (val state = payUi) {
                is PayUi.Success -> {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "পেমেন্ট সফল হয়েছে!",
                        fontFamily = fonts.kalpurush,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Brand.Gold else Color(0xFF064E3B),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "আপনার অ্যাকাউন্টে লাইসেন্স সক্রিয় হয়েছে। এখন সম্পূর্ণ কুরআন পড়তে পারবেন।",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        color = textSubColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { vm.resetPay(); vm.navigate(com.karianaquran.reader.ui.Screen.Reader(1)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF064E3B)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text(
                            text = "কুরআন পড়া শুরু করুন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                PayUi.Idle, is PayUi.Error, is PayUi.Creating -> {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Brand.EmeraldDark.copy(alpha = 0.4f) else Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = if (isDark) Brand.Gold else Color(0xFF064E3B),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "মেম্বারশিপ প্যাকেজ নির্বাচন করুন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Brand.Gold else Color(0xFF064E3B)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "বিকাশ, নগদ, রকেট অথবা কার্ডের মাধ্যমে ইনস্ট্যান্ট আজীবনের ডিজিটাল লাইসেন্স সক্রিয়করণ।",
                        fontFamily = fonts.kalpurush,
                        fontSize = 12.5.sp,
                        color = textSubColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    // ৩টি প্যাকেজ কার্ড
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QARIANA_PACKAGES.forEach { pkg ->
                            val isSelected = pkg.id == selectedPackageId
                            val cardBorderColor = if (isSelected) {
                                if (isDark) Brand.Gold else Color(0xFF0F6B56)
                            } else {
                                if (isDark) Color(0xFF1E382A) else Color(0xFFE2E8F0)
                            }
                            val cardBgColor = if (isSelected) {
                                if (isDark) Color(0xFF0F6B56).copy(alpha = 0.15f) else Color(0xFF0F6B56).copy(alpha = 0.05f)
                            } else {
                                if (isDark) Color.White.copy(alpha = 0.04f) else Color.White
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { selectedPackageId = pkg.id },
                                shape = RoundedCornerShape(14.dp),
                                color = cardBgColor,
                                border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.8.dp else 1.dp, cardBorderColor),
                                shadowElevation = if (isSelected) 2.dp else 0.5.dp
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedPackageId = pkg.id },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = if (isDark) Brand.Gold else Color(0xFF0F6B56),
                                                    unselectedColor = Color(0xFF94A3B8)
                                                ),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = pkg.nameBn,
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) (if (isDark) Brand.Gold else Color(0xFF0F6B56)) else textColor
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            if (pkg.badge != null) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (pkg.id == "vip_diamond") Color(0xFFFEF3C7) else Color(0xFFD1FAE5)
                                                ) {
                                                    Text(
                                                        text = pkg.badge,
                                                        fontFamily = fonts.kalpurush,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (pkg.id == "vip_diamond") Color(0xFFB45309) else Color(0xFF065F46),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = pkg.priceBdt.toBn(),
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (isDark) Brand.Gold else Color(0xFF0F6B56)
                                                )
                                                Text(
                                                    text = pkg.priceUsd,
                                                    fontFamily = fonts.kalpurush,
                                                    fontSize = 10.sp,
                                                    color = textSubColor
                                                )
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = pkg.subtitleBn,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.5.sp,
                                        color = textSubColor
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    pkg.features.forEach { feat ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 1.5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = if (isSelected) (if (isDark) Brand.Gold else Color(0xFF0F6B56)) else Color(0xFF94A3B8),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = feat,
                                                fontFamily = fonts.kalpurush,
                                                fontSize = 11.sp,
                                                color = if (isSelected) textColor else textSubColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // মোবাইল নম্বর ইনপুট
                    OutlinedTextField(
                        value = mobileInput,
                        onValueChange = { mobileInput = it },
                        label = {
                            Text(
                                text = "আপনার সচল মোবাইল নম্বর (01XXXXXXXXX)",
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = if (isDark) Brand.Gold else Color(0xFF064E3B)
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) Brand.Gold else Color(0xFF064E3B),
                            unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.3f) else Color(0xFFCBD5E1),
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedContainerColor = if (isDark) Color.Transparent else Color.White,
                            unfocusedContainerColor = if (isDark) Color.Transparent else Color.White
                        )
                    )

                    if (state is PayUi.Error) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = state.message,
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.sp,
                            color = Color(0xFFEF4444),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = { vm.startPayment(mobileInput.trim()) },
                        enabled = state !is PayUi.Creating && mobileInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF064E3B),
                            disabledContainerColor = Color(0xFF064E3B).copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (state is PayUi.Creating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "${selectedPkg.priceBdt} দিয়ে পেমেন্ট এগিয়ে নিন",
                                fontFamily = fonts.kalpurush,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    androidx.compose.material3.OutlinedButton(
                        onClick = { vm.navigate(com.karianaquran.reader.ui.Screen.Login) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, if (isDark) Brand.Gold else Color(0xFF064E3B))
                    ) {
                        Text(
                            text = "আগে থেকে প্যাকেজ নেওয়া থাকলে লগইন করুন ›",
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Brand.Gold else Color(0xFF064E3B)
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }

                is PayUi.Checkout -> {
                    CircularProgressIndicator(color = if (isDark) Brand.Gold else Color(0xFF064E3B), modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "পেমেন্ট প্রক্রিয়াধীন...",
                        fontFamily = fonts.kalpurush,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Brand.Gold else Color(0xFF064E3B)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "ব্রাউজারে পেমেন্ট সম্পন্ন করুন। পেমেন্ট সম্পন্ন হওয়া মাত্রই অ্যাপটি স্বয়ংক্রিয়ভাবে সম্পূর্ণ কুরআন আনলক করে নিবে।",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        color = textSubColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.order.checkoutUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF064E3B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "পেমেন্ট পেজ পুনরায় খুলুন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { vm.resetPay() }) {
                        Text(
                            text = "বাতিল করুন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.sp,
                            color = textSubColor
                        )
                    }
                }
            }
        }
    }
}
