package com.karianaquran.reader.ui.reader

import com.karianaquran.reader.data.ReadingMode
import com.karianaquran.reader.ui.components.QuranMetadata
import com.karianaquran.reader.ui.components.SecureDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts

private data class QuickJumpSurah(val name: String, val page: Int)

private val POPULAR_SURAH_CHIPS = listOf(
    QuickJumpSurah("সূরা ফাতিহা", 1),
    QuickJumpSurah("সূরা বাকারা", 2),
    QuickJumpSurah("সূরা ইয়াসীন", 440),
    QuickJumpSurah("সূরা আর-রহমান", 531),
    QuickJumpSurah("সূরা আল-মুলক", 562),
    QuickJumpSurah("সূরা আল-ওয়াকিআহ", 534),
    QuickJumpSurah("সূরা আল-কাহফ", 293),
    QuickJumpSurah("৩০তম পারা (আমপারা)", 582),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoToPageDialog(
    currentPage: Int,
    totalPages: Int,
    readingMode: ReadingMode = ReadingMode.CLASSIC,
    onNavigatePage: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    var inputText by remember { mutableStateOf(currentPage.toString()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }

    fun parsePageNumber(text: String): Int? {
        val converted = text.trim().map { ch ->
            when (ch) {
                '০' -> '0'; '১' -> '1'; '২' -> '2'; '৩' -> '3'; '৪' -> '4'
                '৫' -> '5'; '৬' -> '6'; '৭' -> '7'; '৮' -> '8'; '৯' -> '9'
                else -> ch
            }
        }.joinToString("")
        return converted.toIntOrNull()
    }

    fun submit() {
        val p = parsePageNumber(inputText)
        if (p == null || p < 1 || p > totalPages) {
            errorMsg = "১ থেকে ${totalPages.toBn()} এর মধ্যে একটি পৃষ্ঠা লিখুন"
        } else {
            onNavigatePage(p)
            onDismiss()
        }
    }

    SecureDialog(onDismiss = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Brand.EmeraldDark.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                // হেডার
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brand.EmeraldDark.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Brand.EmeraldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "নির্দিষ্ট পৃষ্ঠায় যান",
                            fontFamily = fonts.kalpurush,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "১ থেকে ${totalPages.toBn()} পৃষ্ঠার মধ্যে যেকোনো পৃষ্ঠা নম্বর লিখুন:",
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(10.dp))

                // ইনপুট বক্স
                OutlinedTextField(
                    value = inputText,
                    onValueChange = {
                        inputText = it
                        errorMsg = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "যেমন: ${currentPage.toBn()}",
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.sp
                        )
                    },
                    isError = errorMsg != null,
                    supportingText = errorMsg?.let {
                        { Text(text = it, fontFamily = fonts.kalpurush, color = MaterialTheme.colorScheme.error) }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(onGo = { submit() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Brand.Gold,
                        unfocusedBorderColor = Brand.Gold.copy(alpha = 0.4f),
                        focusedLabelColor = Brand.Gold
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(14.dp))

                // কুইক সূরা চিপস হেডার
                Text(
                    text = "দ্রুত পড়ার জন্য জনপ্রিয় সূরা ও পারা:",
                    fontFamily = fonts.kalpurush,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Brand.EmeraldDark
                )

                Spacer(Modifier.height(8.dp))

                // চিপস ফ্লোরো
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    POPULAR_SURAH_CHIPS.forEach { chip ->
                        val targetPage = QuranMetadata.targetPageForMode(chip.page, readingMode)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Brand.EmeraldDark.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Brand.EmeraldDark.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable {
                                onNavigatePage(targetPage)
                                onDismiss()
                            }
                        ) {
                            Text(
                                text = "${chip.name} (${targetPage.toBn()})",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Brand.EmeraldDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // বাটনস
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "বাতিল",
                            fontFamily = fonts.kalpurush,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { submit() },
                        colors = ButtonDefaults.buttonColors(containerColor = Brand.EmeraldDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "যান",
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
}
