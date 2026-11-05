package com.karianaquran.reader.ui.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karianaquran.reader.service.QuranAudioPlayer
import com.karianaquran.reader.service.QuranAudioState
import com.karianaquran.reader.ui.components.toBn
import com.karianaquran.reader.ui.theme.Brand
import com.karianaquran.reader.ui.theme.LocalAppFonts

/**
 * প্রিমিয়াম ইসলামিক অডিও তিলাওয়াত বার
 * স্ক্রিনের নিচে সুন্দরভাবে ভেসে থাকে এবং আয়াতের তিলাওয়াত পরিচালনা করে
 */
@Composable
fun QuranAudioBar(
    state: QuranAudioState,
    modifier: Modifier = Modifier,
) {
    val fonts = LocalAppFonts.current

    AnimatedVisibility(
        visible = state.isPlayerVisible && state.currentItem != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        val item = state.currentItem ?: return@AnimatedVisibility

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            shape = RoundedCornerShape(18.dp),
            color = Brand.Obsidian.copy(alpha = 0.96f),
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Brand.Gold.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // বাম পাশে: ইকুয়ালাইজার আইকন ও আয়াতের নাম
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Brand.EmeraldDark.copy(alpha = 0.4f))
                                .border(1.dp, Brand.Gold.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Brand.Gold,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "তিলাওয়াত",
                                    tint = Brand.Gold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "তিলাওয়াত: শায়খ মিশারী আল-আফাসী",
                                fontFamily = fonts.kalpurush,
                                fontSize = 11.sp,
                                color = Brand.Gold.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${item.surahNameBn} • আয়াত: ${item.ayah.toBn()}",
                                fontFamily = fonts.kalpurush,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // স্পিড বাটন
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier
                            .clickable { QuranAudioPlayer.cycleSpeed() }
                            .padding(end = 4.dp)
                    ) {
                        Text(
                            text = "${state.speed}x",
                            fontFamily = fonts.kalpurush,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brand.Gold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }

                    // প্লেয়ার কন্ট্রোলস
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // পূর্ববর্তী আয়াত বাটন
                        IconButton(
                            onClick = { QuranAudioPlayer.playPrevious() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "পূর্ববর্তী আয়াত",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // প্লে / পজ বাটন
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Brand.EmeraldDark)
                                .border(1.2.dp, Brand.Gold, CircleShape)
                                .clickable { QuranAudioPlayer.togglePlayPause() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (state.isPlaying) "বিরতি" else "চালান",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // পরবর্তী আয়াত বাটন
                        IconButton(
                            onClick = { QuranAudioPlayer.playNext() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "পরবর্তী আয়াত",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // বন্ধ বাটন
                        IconButton(
                            onClick = { QuranAudioPlayer.hidePlayer() },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "বন্ধ করুন",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
