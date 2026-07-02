package com.example.ui.screens

import android.text.format.Formatter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CosmicBackground
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CustomGrey
import com.example.ui.theme.GlowGreen
import com.example.ui.theme.InfinityPurple
import com.example.ui.theme.InfinityTeal
import com.example.ui.theme.OnCosmicBackground
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.DeviceCareViewModel
import com.example.viewmodel.MediaItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCleanerScreen(
    viewModel: DeviceCareViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val videos by viewModel.videosList.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isCleaning by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var sizeFreed by remember { mutableStateOf(0L) }

    val tabCategories = listOf("Large", "Old", "Duplicate")
    val currentCategory = tabCategories[selectedTabIndex]

    // Filtered items
    val filteredVideos = videos.filter { it.category == currentCategory }
    val selectedVideos = videos.filter { it.isSelected }
    val totalSelectedSize = selectedVideos.sumOf { it.sizeBytes }
    val formattedSelected = Formatter.formatShortFileSize(context, totalSelectedSize)

    val cleanVideos = {
        coroutineScope.launch {
            isCleaning = true
            sizeFreed = totalSelectedSize
            delay(1500)
            viewModel.performVideoClean {
                isCleaning = false
                showSuccess = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Cleaner", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = InfinityTeal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CosmicBackground,
                    titleContentColor = OnCosmicBackground
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CosmicBackground)
                .padding(innerPadding)
        ) {
            if (showSuccess) {
                // Success view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(150.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(listOf(InfinityTeal, GlowGreen)),
                                    shape = CircleShape
                                )
                        )
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = GlowGreen,
                            modifier = Modifier.size(70.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Videos Cleared!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val savedStr = Formatter.formatShortFileSize(context, sizeFreed)
                    Text(
                        text = "$savedStr Space Saved",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InfinityTeal,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Heavy videos and duplicate loops removed successfully. Disk bandwidth optimized.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 12.dp),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = InfinityTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                            .testTag("video_done_button")
                    ) {
                        Text("Back to Command Center", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            } else if (isCleaning) {
                // Cleaning Loader
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = InfinityTeal, strokeWidth = 5.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Reclaiming video storage...",
                        fontSize = 14.sp,
                        color = OnCosmicBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Video Selection Checklist
                Column(modifier = Modifier.fillMaxSize()) {
                    PrimaryTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = CosmicBackground,
                        contentColor = InfinityTeal,
                        modifier = Modifier.fillMaxWidth().testTag("video_categories_tabs")
                    ) {
                        tabCategories.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedTabIndex == index) InfinityTeal else TextSecondary
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (currentCategory) {
                                "Large" -> Icons.Default.VideoFile
                                "Old" -> Icons.Default.CalendarToday
                                else -> Icons.Default.CopyAll
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = InfinityPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$currentCategory Videos (${filteredVideos.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnCosmicBackground
                            )
                        }

                        if (filteredVideos.isNotEmpty()) {
                            Text(
                                text = "Select All",
                                fontSize = 11.sp,
                                color = InfinityTeal,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        filteredVideos.forEach { vid ->
                                            if (!vid.isSelected) viewModel.toggleVideoItem(vid.id)
                                        }
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }

                    if (filteredVideos.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Videos clear",
                                tint = GlowGreen,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No heavy videos found!",
                                fontSize = 13.sp,
                                color = OnCosmicBackground,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Scan update completed.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            items(filteredVideos) { video ->
                                VideoItemCard(
                                    video = video,
                                    onToggle = { viewModel.toggleVideoItem(video.id) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action summary
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Selected ${selectedVideos.size} videos",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = formattedSelected,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OnCosmicBackground
                                    )
                                }

                                Button(
                                    onClick = { cleanVideos() },
                                    enabled = totalSelectedSize > 0L,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = InfinityTeal,
                                        contentColor = Color.Black,
                                        disabledContainerColor = CustomGrey,
                                        disabledContentColor = TextSecondary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .height(44.dp)
                                        .testTag("video_clean_action_button")
                                ) {
                                    Text("Clean Selected", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoItemCard(
    video: MediaItem,
    onToggle: () -> Unit
) {
    val context = LocalContext.current
    val isDupGroup = video.duplicateGroupId != null
    val minutes = video.durationSeconds / 60
    val seconds = video.durationSeconds % 60
    val durationText = String.format("%02d:%02d", minutes, seconds)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        border = BorderStroke(
            width = 1.dp,
            color = if (video.isSelected) InfinityTeal else if (isDupGroup) InfinityPurple.copy(alpha = 0.4f) else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CosmicBackground)
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = "Video file",
                    tint = if (video.isSelected) InfinityTeal else InfinityPurple,
                    modifier = Modifier.align(Alignment.Center).size(24.dp)
                )
                Text(
                    text = durationText,
                    fontSize = 8.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(topStart = 4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnCosmicBackground,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${video.dateAdded} • MP4 File",
                    fontSize = 9.sp,
                    color = TextSecondary
                )
                Text(
                    text = video.path,
                    fontSize = 8.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = Formatter.formatShortFileSize(context, video.sizeBytes),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = OnCosmicBackground
            )

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(
                checked = video.isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = InfinityTeal)
            )
        }
    }
}
