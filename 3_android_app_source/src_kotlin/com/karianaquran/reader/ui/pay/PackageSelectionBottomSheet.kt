package com.karianaquran.reader.ui.pay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.LocalAppFonts

data class MembershipPackage(
    val id: String,
    val nameBn: String,
    val priceBdt: String,
    val priceUsd: String,
    val badge: String?,
    val subtitleBn: String,
    val features: List<String>,
    val isPopular: Boolean = false,
)

val QARIANA_PACKAGES = listOf(
    MembershipPackage(
        id = "basic",
        nameBn = "১. বেসিক লাইসেন্স",
        priceBdt = "৳ ৫০০",
        priceUsd = "$5 USD",
        badge = null,
        subtitleBn = "যেকোনো ১টি সংস্করণ, আজীবন লাইসেন্স",
        features = listOf(
            "যেকোনো ১টি সংস্করণে সম্পূর্ণ ৩০ পারা পড়ার অনুমতি",
            "বিজ্ঞাপনমুক্ত আজীবন ডিজিটাল লাইসেন্স",
            "১টি পার্সোনাল ডিভাইসে সক্রিয়করণ"
        ),
        isPopular = false
    ),
    MembershipPackage(
        id = "standard",
        nameBn = "২. স্ট্যান্ডার্ড লাইসেন্স",
        priceBdt = "৳ ১,০০০",
        priceUsd = "$10 USD",
        badge = "সর্বাধিক নির্বাচিত",
        subtitleBn = "২টি সংস্করণ: নতুন মুসহাফ + ডিজিটাল সংস্করণ",
        features = listOf(
            "নতুন ভেক্টর মুসহাফ + ডিজিটাল সংস্করণ আনলকড",
            "প্রতি আরবি শব্দের উচ্চারণ, শব্দার্থ ও বঙ্গানুবাদ",
            "সম্পূর্ণ ৩০ পারা অফলাইন ডাউনলোড ভল্ট লাইব্রেরি",
            "ডিভাইস পরিবর্তন ও ইনস্ট্যান্ট রিকভারি সুবিধা"
        ),
        isPopular = true
    ),
    MembershipPackage(
        id = "vip_diamond",
        nameBn = "৩. ভিআইপি ডায়মন্ড লাইসেন্স",
        priceBdt = "৳ ১,৫০০",
        priceUsd = "$15 USD",
        badge = "আজীবন সেরা ভ্যালু",
        subtitleBn = "সকল ৩টি সংস্করণ সম্পূর্ণ আনলকড ও আজীবন সুবিধা",
        features = listOf(
            "সকল ৩টি সংস্করণ সম্পূর্ণ আনলকড (মুসহাফ, তাফসীর ও ক্লাসিক)",
            "পূর্ণাঙ্গ ৯৭৩ পেজ অফলাইন ভল্ট লাইব্রেরি",
            "ভবিষ্যতের সকল ইসলামিক প্রকাশনায় স্পেশাল আর্লি অ্যাক্সেস",
            "ভিআইপি প্রায়োরিটি কাস্টমার সাপোর্ট ও ডিভাইস রিকভারি"
        ),
        isPopular = false
    )
)

/**
 * ৩-টায়ার প্যাকেজ সিলেকশন বটমশিট
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageSelectionBottomSheet(
    onDismiss: () -> Unit,
    onProceedToPay: (MembershipPackage) -> Unit,
    onAlreadyHaveAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPackageId by remember { mutableStateOf("standard") }
    val selectedPkg = QARIANA_PACKAGES.firstOrNull { it.id == selectedPackageId } ?: QARIANA_PACKAGES[1]

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFBF9F5),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // ১. হেডার
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F6B56),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD4AF37).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFFFFDF79),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "ক্বারীয়ানা মেম্বারশিপ প্যাকেজ",
                                fontFamily = fonts.kalpurush,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "আপনার পছন্দের প্যাকেজটি নির্বাচন করুন",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ২. প্যাকেজ কার্ড লিস্ট
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QARIANA_PACKAGES.forEach { pkg ->
                    val isSelected = pkg.id == selectedPackageId
                    val borderColor = if (isSelected) Color(0xFF0F6B56) else Color(0xFFE2E8F0)
                    val bgColor = if (isSelected) Color(0xFF0F6B56).copy(alpha = 0.05f) else Color.White

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedPackageId = pkg.id },
                        shape = RoundedCornerShape(16.dp),
                        color = bgColor,
                        border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.8.dp else 1.dp, borderColor),
                        shadowElevation = if (isSelected) 3.dp else 1.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                            selectedColor = Color(0xFF0F6B56),
                                            unselectedColor = Color(0xFF94A3B8)
                                        ),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = pkg.nameBn,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF0F6B56) else Color(0xFF1E293B)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (pkg.badge != null) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
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
                                            text = pkg.priceBdt,
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF0F6B56)
                                        )
                                        Text(
                                            text = pkg.priceUsd,
                                            fontFamily = fonts.kalpurush,
                                            fontSize = 10.5.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = pkg.subtitleBn,
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )

                            Spacer(Modifier.height(8.dp))

                            pkg.features.forEach { feat ->
                                Row(
                                    modifier = Modifier.padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (isSelected) Color(0xFF0F6B56) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = feat,
                                        fontFamily = fonts.kalpurush,
                                        fontSize = 11.5.sp,
                                        color = if (isSelected) Color(0xFF1E293B) else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ৩. অ্যাকশন বাটনসমূহ
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onProceedToPay(selectedPkg)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F6B56))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Color(0xFFFFDF79),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${selectedPkg.nameBn} কিনুন (${selectedPkg.priceBdt})",
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                TextButton(
                    onClick = {
                        onAlreadyHaveAccount()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "আগে থেকেই লাইসেন্স কি আছে? লগইন করুন",
                        fontFamily = fonts.kalpurush,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F6B56)
                    )
                }
            }
        }
    }
}

