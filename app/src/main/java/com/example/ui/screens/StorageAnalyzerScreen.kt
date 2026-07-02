package com.example.ui.screens

import android.text.format.Formatter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CosmicBackground
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.InfinityTeal
import com.example.ui.theme.OnCosmicBackground
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.DeviceCareViewModel
import com.example.viewmodel.StorageCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageAnalyzerScreen(
    viewModel: DeviceCareViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storageTotal by viewModel.storageTotal.collectAsState()
    val storageUsed by viewModel.storageUsed.collectAsState()

    var isLoaded by remember { mutableStateOf(false) }
    
    val categories = remember(storageUsed) {
        viewModel.getStorageCategories()
    }

    LaunchedEffect(Unit) {
        viewModel.refreshSystemSpecs(context)
        isLoaded = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isLoaded) 1f else 0f,
        animationSpec = tween(1200),
        label = "graph"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storage Analyzer", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CosmicBackground)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Main segment chart card
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().testTag("segmented_chart_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = InfinityTeal,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Disk Usage Breakdown",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnCosmicBackground
                            )
                        }
                        
                        val formattedUsed = Formatter.formatShortFileSize(context, storageUsed)
                        val formattedTotal = Formatter.formatShortFileSize(context, storageTotal)
                        Text(
                            text = "$formattedUsed / $formattedTotal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = InfinityTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Segmented Bar Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            var currentOffset = 0f
                            val totalUsed = categories.sumOf { it.sizeBytes }
                            
                            if (totalUsed > 0L) {
                                for (cat in categories) {
                                    val percentage = cat.sizeBytes.toFloat() / totalUsed.toFloat()
                                    val barWidth = size.width * percentage * animatedProgress
                                    
                                    drawRect(
                                        color = Color(android.graphics.Color.parseColor(cat.colorHex)),
                                        topLeft = Offset(currentOffset, 0f),
                                        size = Size(barWidth, size.height)
                                    )
                                    currentOffset += barWidth
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Legenda specs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total System Space: ${Formatter.formatShortFileSize(context, storageTotal)}", fontSize = 10.sp, color = TextSecondary)
                        val freeBytes = storageTotal - storageUsed
                        Text(text = "Free Space: ${Formatter.formatShortFileSize(context, freeBytes)}", fontSize = 10.sp, color = InfinityTeal, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Categories LazyColumn list
            Text(
                text = "Media & File Categories",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = OnCosmicBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(categories) { category ->
                    CategoryItemRow(category = category, totalUsed = storageUsed)
                }
            }
        }
    }
}

@Composable
fun CategoryItemRow(
    category: StorageCategory,
    totalUsed: Long
) {
    val context = LocalContext.current
    val categoryColor = Color(android.graphics.Color.parseColor(category.colorHex))
    val percentage = if (totalUsed > 0L) (category.sizeBytes.toFloat() / totalUsed.toFloat()) else 0f
    
    val vectorIcon = when (category.name) {
        "Images" -> Icons.Default.Image
        "Videos" -> Icons.Default.Movie
        "Audio" -> Icons.Default.AudioFile
        "Documents" -> Icons.Default.TextSnippet
        "APK Files" -> Icons.Default.InstallMobile
        "Downloads" -> Icons.Default.Download
        else -> Icons.Default.Folder
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon with solid color circle border
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, categoryColor.copy(alpha = 0.5f), CircleShape)
                    .background(categoryColor.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = vectorIcon,
                    contentDescription = category.name,
                    tint = categoryColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground
                    )
                    Text(
                        text = Formatter.formatShortFileSize(context, category.sizeBytes),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))

                // Track Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percentage)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(categoryColor)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${(percentage * 100).toInt()}% of allocated storage",
                    fontSize = 9.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
