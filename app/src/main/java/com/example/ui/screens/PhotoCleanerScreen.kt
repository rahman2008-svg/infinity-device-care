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
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImagesearchRoller
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
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
fun PhotoCleanerScreen(
    viewModel: DeviceCareViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val photos by viewModel.photosList.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isCleaning by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var sizeFreed by remember { mutableStateOf(0L) }

    val tabCategories = listOf("Duplicate", "Similar", "Blurry", "Dark", "Large")
    val currentCategory = tabCategories[selectedTabIndex]

    // Filtered items
    val filteredPhotos = photos.filter { it.category == currentCategory }
    val selectedPhotos = photos.filter { it.isSelected }
    val totalSelectedSize = selectedPhotos.sumOf { it.sizeBytes }
    val formattedSelected = Formatter.formatShortFileSize(context, totalSelectedSize)

    val cleanPhotos = {
        coroutineScope.launch {
            isCleaning = true
            sizeFreed = totalSelectedSize
            delay(1500)
            viewModel.performPhotoClean {
                isCleaning = false
                showSuccess = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo Cleaner", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                // Photo Reclaim Success View
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
                        text = "Gallery Rejuvenated!",
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
                        text = "Redundant copy iterations and low-light duplicates removed. Visual focus optimized.",
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
                            .testTag("photo_done_button")
                    ) {
                        Text("Back to Command Center", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            } else if (isCleaning) {
                // Active removal loader
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = InfinityTeal, strokeWidth = 5.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Deleting selected images from storage...",
                        fontSize = 14.sp,
                        color = OnCosmicBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sweeping duplicates safely...",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            } else {
                // Main Photo Analysis Selection Panel
                Column(modifier = Modifier.fillMaxSize()) {
                    // Category Primary Tab bar
                    PrimaryTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = CosmicBackground,
                        contentColor = InfinityTeal,
                        modifier = Modifier.fillMaxWidth().testTag("photo_categories_tabs")
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

                    // Scanned category information panel
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
                                "Duplicate" -> Icons.Default.CopyAll
                                "Similar" -> Icons.Default.ImagesearchRoller
                                "Blurry" -> Icons.Default.BlurOn
                                "Dark" -> Icons.Default.DarkMode
                                else -> Icons.Default.PhotoSizeSelectLarge
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = InfinityPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$currentCategory Photos (${filteredPhotos.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnCosmicBackground
                            )
                        }

                        if (filteredPhotos.isNotEmpty()) {
                            Text(
                                text = "Select All",
                                fontSize = 11.sp,
                                color = InfinityTeal,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        filteredPhotos.forEach { photo ->
                                            if (!photo.isSelected) viewModel.togglePhotoItem(photo.id)
                                        }
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }

                    // Content checklist
                    if (filteredPhotos.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Clean Category",
                                tint = GlowGreen,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Category is absolutely clear!",
                                fontSize = 13.sp,
                                color = OnCosmicBackground,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Run standard 'Scan Now' to check for media updates.",
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
                            items(filteredPhotos) { photo ->
                                PhotoItemCard(
                                    photo = photo,
                                    onToggle = { viewModel.togglePhotoItem(photo.id) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reclaim trigger bar
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
                                        text = "Selected ${selectedPhotos.size} items",
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
                                    onClick = { cleanPhotos() },
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
                                        .testTag("photo_clean_action_button")
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
fun PhotoItemCard(
    photo: MediaItem,
    onToggle: () -> Unit
) {
    val context = LocalContext.current
    val isDupGroup = photo.duplicateGroupId != null
    
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        border = BorderStroke(
            width = 1.dp,
            color = if (photo.isSelected) InfinityTeal else if (isDupGroup) InfinityPurple.copy(alpha = 0.4f) else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Media Image Placeholder
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CosmicBackground)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Image preview",
                    tint = if (photo.isSelected) InfinityTeal else InfinityPurple,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = photo.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground,
                        maxLines = 1
                    )
                    if (isDupGroup) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(InfinityPurple.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DUP",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = InfinityPurple
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${photo.width}x${photo.height} • ${photo.dateAdded}",
                    fontSize = 9.sp,
                    color = TextSecondary
                )
                Text(
                    text = photo.path,
                    fontSize = 8.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = Formatter.formatShortFileSize(context, photo.sizeBytes),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = OnCosmicBackground
            )

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(
                checked = photo.isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = InfinityTeal)
            )
        }
    }
}
