package com.karianaquran.reader.ui.reader

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.data.model.WordMarkType
import com.karianaquran.reader.data.model.WordRef
import com.karianaquran.reader.service.QuranAudioPlayer
import com.karianaquran.reader.ui.components.SecureDialog
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts

data class SelectedWordInfo(
    val ref: WordRef,
    val arabic: String,
    val translit: String,
    val meaning: String,
    val ayahRef: String,
    val surahName: String,
    val nextWord: String? = null,
    val surahNumber: Int = 1,
    val ayahNumber: Int = 1,
    val wordIndexInAyah: Int = 1,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WordDetailSheet(
    info: SelectedWordInfo,
    currentMark: WordMarkType?,
    onSetMark: (WordMarkType?) -> Unit,
    onDismiss: () -> Unit,
) {
    val fonts = LocalAppFonts.current
    val tajweedRules = TajweedAnalyzer.analyze(info.arabic, info.nextWord)
    val wordAudioState by QuranAudioPlayer.wordAudioState.collectAsState()

    val isThisWordPlaying = wordAudioState.isPlaying && wordAudioState.currentWordId == info.ref.id
    val isThisWordLoading = wordAudioState.isLoading && wordAudioState.currentWordId == info.ref.id

    // শব্দে ক্লিক করার সাথে সাথে স্বয়ংক্রিয়ভাবে একবার উচ্চারণ চালানো
    LaunchedEffect(info.ref.id) {
        QuranAudioPlayer.playWordPronunciation(
            surah = info.surahNumber,
            ayah = info.ayahNumber,
            wordIndex = info.wordIndexInAyah,
            wordId = info.ref.id
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            QuranAudioPlayer.stopWordAudio()
        }
    }

    // পালসিং স্কেল ইফেক্ট যখন শব্দ উচ্চারণ চলতে থাকবে
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isThisWordPlaying) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    SecureDialog(onDismiss = {
        QuranAudioPlayer.stopWordAudio()
        onDismiss()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // টপ বার: সূরা ও আয়াত তথ্য + বন্ধ বাটন
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Brand.EmeraldDark.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = info.ref.page.toBn(),
                                fontFamily = fonts.kalpurush,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Brand.EmeraldDark
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = info.surahName,
                                fontFamily = fonts.kalpurush,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "আয়াত নং: ${info.ayahRef.toBn()}",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = {
                        QuranAudioPlayer.stopWordAudio()
                        onDismiss()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // বড় আরবি শব্দ কার্ড + শব্দ উচ্চারণ বাটন
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Brand.Obsidian,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // আরবি শব্দ এবং সাথে সাউন্ড আইকন
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = info.arabic,
                                fontFamily = fonts.qariana,
                                fontSize = 40.sp,
                                color = Brand.Gold,
                                textAlign = TextAlign.Center,
                                lineHeight = 48.sp
                            )

                            Spacer(Modifier.width(14.dp))

                            // 🔊 সাউন্ড বাটন
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .scale(if (isThisWordPlaying) pulseScale else 1f)
                                    .clip(CircleShape)
                                    .background(
                                        if (isThisWordPlaying) Color(0xFF0F6B56)
                                        else Brand.Gold.copy(alpha = 0.2f)
                                    )
                                    .border(
                                        width = if (isThisWordPlaying) 2.dp else 1.dp,
                                        color = if (isThisWordPlaying) Brand.Gold else Brand.Gold.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        if (isThisWordPlaying) {
                                            QuranAudioPlayer.stopWordAudio()
                                        } else {
                                            QuranAudioPlayer.playWordPronunciation(
                                                surah = info.surahNumber,
                                                ayah = info.ayahNumber,
                                                wordIndex = info.wordIndexInAyah,
                                                wordId = info.ref.id
                                            )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isThisWordLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Brand.Gold,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "শব্দের উচ্চারণ শুনুন",
                                        tint = if (isThisWordPlaying) Color.White else Brand.Gold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // অ্যাকশন পিল বাটন: শব্দ উচ্চারণ ও সম্পূর্ণ আয়াতের তেলাওয়াত
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    QuranAudioPlayer.playWordPronunciation(
                                        surah = info.surahNumber,
                                        ayah = info.ayahNumber,
                                        wordIndex = info.wordIndexInAyah,
                                        wordId = info.ref.id
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isThisWordPlaying) Color(0xFF0F6B56) else Color(0xFF1E293B)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isThisWordPlaying) Color.White else Brand.Gold
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = if (isThisWordPlaying) "উচ্চারণ চলছে..." else "শব্দ শুনুন",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isThisWordPlaying) Color.White else Brand.Gold
                                )
                            }

                            Button(
                                onClick = {
                                    QuranAudioPlayer.playAyah(
                                        surah = info.surahNumber,
                                        ayah = info.ayahNumber,
                                        surahNameBn = info.surahName
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFB45309).copy(alpha = 0.85f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "▶ পূর্ণ আয়াত",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        if (info.translit.isNotBlank()) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "উচ্চারণ: ${info.translit}",
                                fontFamily = fonts.kalpurush,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (info.meaning.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "অর্থ: ${info.meaning}",
                                fontFamily = fonts.kalpurush,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Brand.Parchment,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // তাজবীদ নিয়মাবলী (যদি প্রযোজ্য হয়)
                if (tajweedRules.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "তাজবীদ বিশ্লেষণ:",
                        fontFamily = fonts.kalpurush,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tajweedRules.forEach { rule ->
                            val badgeColor = Color(rule.colorArgb)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeColor.copy(alpha = 0.12f))
                                    .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(badgeColor)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "${rule.nameBn}: ${rule.descBn}",
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
                Spacer(Modifier.height(14.dp))

                // শব্দ বুকমার্ক অপশন (৩টি ক্যাটাগরি)
                Text(
                    text = "শব্দ চিহ্নিত করুন (ওয়ার্ড বুকমার্ক):",
                    fontFamily = fonts.kalpurush,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WordMarkType.entries.forEach { markType ->
                        val isSelected = currentMark == markType
                        val activeBg = when (markType) {
                            WordMarkType.STUCK -> Color(0xFFEF4444)
                            WordMarkType.TAJWEED -> Color(0xFFF59E0B)
                            WordMarkType.REVISION -> Color(0xFF10B981)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) activeBg else activeBg.copy(alpha = 0.08f))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) activeBg else activeBg.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSetMark(if (isSelected) null else markType) }
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = markType.emoji, fontSize = 18.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = markType.label,
                                    fontFamily = fonts.kalpurush,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
