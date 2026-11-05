package com.karianaquran.reader.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.data.location.District
import com.karianaquran.reader.data.location.LocationHelper
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * জেলা নির্বাচন ও জিপিএস অটো-ডিটেকশন মডাল বটমশীট (৬৪ জেলা সমর্থিত)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictSelectSheet(
    isDark: Boolean,
    currentDistrict: District,
    onDistrictSelected: (District) -> Unit,
    onDismiss: () -> Unit
) {
    val fonts = LocalAppFonts.current
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var searchQuery by remember { mutableStateOf("") }

    // রানটাইম লোকেশন পারমিশন লঞ্চার
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            LocationHelper.detectDeviceLocation(context) { detectedDistrict ->
                onDistrictSelected(detectedDistrict)
                onDismiss()
            }
        }
    }

    val filteredDistricts = remember(searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            LocationHelper.districts
        } else {
            LocationHelper.districts.filter {
                it.nameBn.contains(q) || it.nameEn.lowercase().contains(q) || it.divisionBn.contains(q)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF0F1E19) else Color(0xFFF9FDFB),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
        ) {
            // শীর্ষ বার: টাইটেল ও ক্লোজ বাটন
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF1E3A30) else Color(0xFFD1FAE5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "আপনার জেলা নির্বাচন করুন",
                            fontFamily = fonts.kalpurush,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF064E3B)
                        )
                        Text(
                            text = "সঠিক নামাজের ওয়াক্ত ও কিবলার জন্য",
                            fontFamily = fonts.kalpurush,
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF0F766E)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "বন্ধ করুন",
                        tint = if (isDark) Color.White else Color(0xFF374151)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // জিপিএস অটো-ডিটেকশন বাটন
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isDark) Color(0xFF163228) else Color(0xFFE8F6ED),
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    if (isDark) Color(0xFF34D399).copy(alpha = 0.5f) else Color(0xFF059669).copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (LocationHelper.hasLocationPermission(context)) {
                            LocationHelper.detectDeviceLocation(context) { detected ->
                                onDistrictSelected(detected)
                                onDismiss()
                            }
                        } else {
                            LocationHelper.setPermissionRequested(context)
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "GPS",
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "বর্তমান লোকেশন স্বয়ংক্রিয়ভাবে শনাক্ত করুন (GPS)",
                            fontFamily = fonts.kalpurush,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF064E3B)
                        )
                        Text(
                            text = "ডিভাইসের অবস্থান অনুযায়ী জেলা স্বয়ংক্রিয়ভাবে সেট হবে",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFA7F3D0) else Color(0xFF0F766E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // সার্চ বার
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "জেলা খুঁজুন (যেমন: ঢাকা, চট্টগ্রাম, সিলেট...)",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        color = Color(0xFF9CA3AF)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "অনুসন্ধান",
                        tint = Color(0xFF059669)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF059669),
                    unfocusedBorderColor = if (isDark) Color(0xFF1E3A30) else Color(0xFFE2E8F0),
                    focusedContainerColor = if (isDark) Color(0xFF12261F) else Color.White,
                    unfocusedContainerColor = if (isDark) Color(0xFF12261F) else Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ৬৪ জেলার তালিকা
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredDistricts, key = { it.id }) { district ->
                    val isSelected = currentDistrict.id == district.id
                    DistrictItemRow(
                        district = district,
                        isSelected = isSelected,
                        isDark = isDark,
                        onClick = {
                            LocationHelper.saveSelectedDistrict(context, district)
                            onDistrictSelected(district)
                            onDismiss()
                        }
                    )
                    HorizontalDivider(
                        color = if (isDark) Color(0xFF162D24) else Color(0xFFF1F5F9),
                        thickness = 0.8.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DistrictItemRow(
    district: District,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val fonts = LocalAppFonts.current

    Surface(
        color = if (isSelected) (if (isDark) Color(0xFF1B3D30) else Color(0xFFD1FAE5)) else Color.Transparent,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = district.nameBn,
                        fontFamily = fonts.kalpurush,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) (if (isDark) Color.White else Color(0xFF064E3B)) else (if (isDark) Color.White else Color(0xFF1F2937))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${district.nameEn})",
                        fontSize = 11.5.sp,
                        color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
                    )
                }
                Text(
                    text = "${district.divisionBn} বিভাগ",
                    fontFamily = fonts.kalpurush,
                    fontSize = 11.5.sp,
                    color = if (isSelected) Color(0xFF059669) else (if (isDark) Color(0xFF6EE7B7) else Color(0xFF0F766E))
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF059669)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "নির্বাচিত",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
