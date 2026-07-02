package com.example.ui.screens

import android.text.format.Formatter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.theme.CosmicBackground
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.GlowBlue
import com.example.ui.theme.GlowGreen
import com.example.ui.theme.GlowYellow
import com.example.ui.theme.InfinityPink
import com.example.ui.theme.InfinityPurple
import com.example.ui.theme.InfinityTeal
import com.example.ui.theme.OnCosmicBackground
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.DeviceCareViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CommandFeature(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val color: Color,
    val route: String,
    val tag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DeviceCareViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storageTotal by viewModel.storageTotal.collectAsState()
    val storageUsed by viewModel.storageUsed.collectAsState()
    val ramTotal by viewModel.ramTotal.collectAsState()
    val ramUsed by viewModel.ramUsed.collectAsState()
    val cpuUsage by viewModel.cpuUsage.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()
    val isCharging by viewModel.isBatteryCharging.collectAsState()

    val isScanning by viewModel.isScanning.collectAsState()
    val scanProgress by viewModel.scanProgress.collectAsState()
    val scanCurrentFile by viewModel.scanCurrentFile.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val history by viewModel.cleanupHistory.collectAsState()

    var showHistorySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.refreshSystemSpecs(context)
    }

    val features = remember {
        listOf(
            CommandFeature("Quick Clean", "Temporary cache, trash logs", Icons.Default.Delete, InfinityTeal, Screen.QUICK_CLEAN, "feat_quick_clean"),
            CommandFeature("Photo Cleaner", "Duplicate & blurry cleanup", Icons.Default.Image, GlowBlue, Screen.PHOTO_CLEANER, "feat_photo_clean"),
            CommandFeature("Video Cleaner", "Bulky old media analyzer", Icons.Default.Movie, InfinityPurple, Screen.VIDEO_CLEANER, "feat_video_clean"),
            CommandFeature("App Manager", "Unused apps uninstaller", Icons.Default.Settings, GlowYellow, Screen.APP_MANAGER, "feat_app_manager"),
            CommandFeature("Storage Analyzer", "Visual storage distribution", Icons.Default.PieChart, InfinityPink, Screen.STORAGE_ANALYZER, "feat_storage_analysis"),
            CommandFeature("Battery Saver", "Optimization power profile", Icons.Default.BatteryChargingFull, GlowGreen, Screen.BATTERY_ANALYSIS, "feat_battery_analysis"),
            CommandFeature("Performance", "Boost active system RAM", Icons.Default.Speed, GlowBlue, Screen.PERFORMANCE_BOOST, "feat_performance_boost"),
            CommandFeature("Premium Panel", "Schedule automatic cleans", Icons.Default.AutoAwesome, InfinityPurple, Screen.PREMIUM, "feat_premium")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        // Custom Gradient Brand Badge: bg-gradient-to-br from-indigo-500 to-purple-600
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(InfinityTeal, InfinityPurple)
                                    )
                                )
                        ) {
                            Text(
                                text = "∞",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Infinity",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp,
                                color = OnCosmicBackground
                            )
                            Text(
                                text = "DEVICE CARE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = InfinityTeal
                            )
                        }
                    }
                },
                actions = {
                    // History Button
                    IconButton(onClick = { showHistorySheet = true }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Cleanup Logs",
                            tint = InfinityTeal
                        )
                    }
                    
                    // Premium Sparkle Badge
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPremium) InfinityPurple else Color.Black.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(CircleShape)
                            .clickable { onNavigate(Screen.PREMIUM) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Premium Badge",
                                tint = if (isPremium) GlowGreen else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPremium) "PLUS" else "FREE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPremium) GlowGreen else TextSecondary
                            )
                        }
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

            // Unified Scan Dashboard
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("storage_dashboard_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isScanning) {
                        // Scan loading animation view
                        CircularProgressIndicator(
                            progress = { scanProgress },
                            color = InfinityTeal,
                            strokeWidth = 6.dp,
                            modifier = Modifier.size(110.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Analyzing Storage & Resources...",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = InfinityTeal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = scanCurrentFile,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Storage Ring Indicator
                        val usedGb = storageUsed / (1024.0 * 1024.0 * 1024.0)
                        val totalGb = storageTotal / (1024.0 * 1024.0 * 1024.0)
                        val percentage = if (storageTotal > 0L) (storageUsed.toFloat() / storageTotal.toFloat()) else 0f
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(100.dp)
                            ) {
                                Canvas(modifier = Modifier.size(90.dp)) {
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.05f),
                                        style = Stroke(width = 8.dp.toPx())
                                    )
                                    drawArc(
                                        brush = Brush.sweepGradient(
                                            colors = listOf(InfinityPurple, InfinityTeal, InfinityPurple)
                                        ),
                                        startAngle = -90f,
                                        sweepAngle = 360f * percentage,
                                        useCenter = false,
                                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${(percentage * 100).toInt()}%",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OnCosmicBackground
                                    )
                                    Text(
                                        text = "USED",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Device Storage Status",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnCosmicBackground
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Used: ${String.format("%.1f", usedGb)} GB / ${String.format("%.1f", totalGb)} GB",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                val freeGb = (storageTotal - storageUsed) / (1024.0 * 1024.0 * 1024.0)
                                Text(
                                    text = "Free: ${String.format("%.1f", freeGb)} GB available",
                                    fontSize = 11.sp,
                                    color = InfinityTeal,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Direct Scan Button
                                Button(
                                    onClick = { viewModel.startOverallScan(context) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = InfinityTeal),
                                    modifier = Modifier.height(34.dp).testTag("start_scan_button")
                                ) {
                                    Text(
                                        text = "Scan Now",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Hardware Specs Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // RAM info
                        HardwareSpecItem(
                            icon = Icons.Default.Memory,
                            label = "RAM Speed",
                            value = "${String.format("%.1f", ramUsed / (1024.0 * 1024.0 * 1024.0))}GB / ${String.format("%.1f", ramTotal / (1024.0 * 1024.0 * 1024.0))}GB",
                            tint = InfinityPurple
                        )
                        // CPU info
                        HardwareSpecItem(
                            icon = Icons.Default.Speed,
                            label = "CPU Load",
                            value = "$cpuUsage% active",
                            tint = GlowBlue
                        )
                        // Battery info
                        HardwareSpecItem(
                            icon = Icons.Default.BatteryChargingFull,
                            label = "Battery",
                            value = "$batteryLevel%${if (isCharging) " (Charging)" else ""}",
                            tint = GlowGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main features title
            Text(
                text = "Command Center",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnCosmicBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Dynamic grid of 8 primary command modules
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(features) { feature ->
                    FeatureCard(
                        feature = feature,
                        onClick = { onNavigate(feature.route) }
                    )
                }
            }
        }

        // Modal logs spreadsheet sheet
        if (showHistorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showHistorySheet = false },
                sheetState = sheetState,
                containerColor = CosmicSurface,
                contentColor = OnCosmicBackground,
                modifier = Modifier.testTag("history_bottom_sheet")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .fillMaxHeight(0.7f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cleanup History Logs",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = InfinityTeal
                        )
                        
                        Text(
                            text = "Clear Logs",
                            fontSize = 12.sp,
                            color = InfinityPink,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable {
                                    viewModel.clearHistory()
                                }
                                .padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (history.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleanHands,
                                contentDescription = "Clean",
                                tint = TextSecondary,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your cleaning history is pristine!",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(history.size) { index ->
                                val event = history[index]
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CosmicBackground),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = event.type,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = OnCosmicBackground
                                            )
                                            val dateString = SimpleDateFormat("MMM dd yyyy, hh:mm a", Locale.getDefault())
                                                .format(Date(event.timestamp))
                                            Text(
                                                text = dateString,
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                        if (event.sizeBytes > 0L) {
                                            val formattedSize = Formatter.formatShortFileSize(context, event.sizeBytes)
                                            Text(
                                                text = "+$formattedSize Saved",
                                                fontSize = 13.sp,
                                                color = GlowGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Text(
                                                text = "Optimized",
                                                fontSize = 12.sp,
                                                color = InfinityTeal,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
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
fun HardwareSpecItem(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontSize = 10.sp,
            color = OnCosmicBackground,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun FeatureCard(
    feature: CommandFeature,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(feature.tag)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                tint = feature.color,
                modifier = Modifier.size(24.dp)
            )
            
            Column {
                Text(
                    text = feature.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnCosmicBackground
                )
                Text(
                    text = feature.desc,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
