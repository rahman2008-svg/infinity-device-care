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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CosmicBackground
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.GlowBlue
import com.example.ui.theme.GlowGreen
import com.example.ui.theme.GlowYellow
import com.example.ui.theme.InfinityPurple
import com.example.ui.theme.InfinityTeal
import com.example.ui.theme.OnCosmicBackground
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.DeviceCareViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceScreen(
    viewModel: DeviceCareViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val ramTotal by viewModel.ramTotal.collectAsState()
    val ramUsed by viewModel.ramUsed.collectAsState()

    var isBoosting by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var ramSavedBytes by remember { mutableStateOf(0L) }
    var scaleAnimTrigger by remember { mutableStateOf(false) }

    val activeProcesses = remember {
        listOf(
            MemoryProcess("Core system logs updater", "142 MB", Icons.Default.Android, GlowBlue),
            MemoryProcess("Unused telemetry service", "320 MB", Icons.Default.Android, InfinityPurple),
            MemoryProcess("Dynamic cache loader", "184 MB", Icons.Default.Android, InfinityTeal),
            MemoryProcess("Secondary widget renderers", "98 MB", Icons.Default.Android, TextSecondary)
        )
    }

    LaunchedEffect(Unit) {
        viewModel.refreshSystemSpecs(context)
        scaleAnimTrigger = true
    }

    val runPerformanceBoost = {
        coroutineScope.launch {
            isBoosting = true
            delay(1800)
            viewModel.performRAMBoost { savedBytes ->
                ramSavedBytes = savedBytes
                isBoosting = false
                showSuccess = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Performance", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                        text = "Device Performance Boosted!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val savedStr = Formatter.formatShortFileSize(context, ramSavedBytes)
                    Text(
                        text = "$savedStr RAM Reclaimed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InfinityTeal,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Orphaned background tasks and unlinked services successfully flushed. System speed capacity optimized.",
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
                            .testTag("boost_success_done")
                    ) {
                        Text("Back to Dashboard", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            } else if (isBoosting) {
                // Booster Loader
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = InfinityTeal, strokeWidth = 5.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Boosting RAM capabilities...",
                        fontSize = 14.sp,
                        color = OnCosmicBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Terminating redundant cached processes...",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            } else {
                // Speedometer list view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Speedometer Dial Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().testTag("ram_dial_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val percentage = if (ramTotal > 0L) (ramUsed.toFloat() / ramTotal.toFloat()) else 0f
                            val animatedPercentage by animateFloatAsState(
                                targetValue = if (scaleAnimTrigger) percentage else 0f,
                                animationSpec = tween(1500),
                                label = "speed_dial"
                            )

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(160.dp)
                            ) {
                                Canvas(modifier = Modifier.size(140.dp)) {
                                    // Base gray track
                                    drawArc(
                                        color = Color.White.copy(alpha = 0.05f),
                                        startAngle = 135f,
                                        sweepAngle = 270f,
                                        useCenter = false,
                                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                    // Active colorful track
                                    drawArc(
                                        brush = Brush.sweepGradient(
                                            colors = listOf(InfinityTeal, InfinityPurple, InfinityTeal)
                                        ),
                                        startAngle = 135f,
                                        sweepAngle = 270f * animatedPercentage,
                                        useCenter = false,
                                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                                
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${(animatedPercentage * 100).toInt()}%",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OnCosmicBackground
                                    )
                                    Text(
                                        text = "RAM LOAD",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val usedGb = ramUsed / (1024.0 * 1024.0 * 1024.0)
                            val totalGb = ramTotal / (1024.0 * 1024.0 * 1024.0)
                            Text(
                                text = "RAM Usage: ${String.format("%.1f", usedGb)} GB / ${String.format("%.1f", totalGb)} GB",
                                fontSize = 12.sp,
                                color = OnCosmicBackground,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Running processes
                    Text(
                        text = "Active RAM Allocations",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(activeProcesses.size) { index ->
                            val process = activeProcesses[index]
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(CosmicBackground)
                                    ) {
                                        Icon(
                                            imageVector = process.icon,
                                            contentDescription = null,
                                            tint = process.tint,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = process.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OnCosmicBackground
                                        )
                                        Text(
                                            text = "Running in cache background",
                                            fontSize = 9.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Text(
                                        text = process.memorySize,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InfinityTeal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rocket Boost button
                    Button(
                        onClick = { runPerformanceBoost() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = InfinityTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("performance_boost_action_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Boost RAM Speed", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

data class MemoryProcess(
    val name: String,
    val memorySize: String,
    val icon: ImageVector,
    val tint: Color
)
