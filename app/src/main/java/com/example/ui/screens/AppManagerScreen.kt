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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.GlowYellow
import com.example.ui.theme.InfinityPurple
import com.example.ui.theme.InfinityTeal
import com.example.ui.theme.OnCosmicBackground
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppItem
import com.example.viewmodel.DeviceCareViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppManagerScreen(
    viewModel: DeviceCareViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apps by viewModel.appsList.collectAsState()

    var isUninstalling by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var spaceSaved by remember { mutableStateOf(0L) }
    var countSaved by remember { mutableStateOf(0) }

    val selectedApps = apps.filter { it.isSelected }
    val totalSelectedSize = selectedApps.sumOf { it.sizeBytes }
    val formattedSelectedSize = Formatter.formatShortFileSize(context, totalSelectedSize)

    val runUninstallBatch = {
        coroutineScope.launch {
            isUninstalling = true
            spaceSaved = totalSelectedSize
            countSaved = selectedApps.size
            delay(1800)
            viewModel.performBatchUninstall(context) { uninstalledCount, savedBytes ->
                isUninstalling = false
                showSuccess = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Manager", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                // Success View
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
                        text = "Apps Uninstalled!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val savedStr = Formatter.formatShortFileSize(context, spaceSaved)
                    Text(
                        text = "Removed $countSaved Apps • $savedStr Freed",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = InfinityTeal,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Selected storage-draining applications have been deleted. System background overhead reduced.",
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
                            .testTag("app_manager_success_done")
                    ) {
                        Text("Back to Dashboard", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            } else if (isUninstalling) {
                // Loader View
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = InfinityTeal, strokeWidth = 5.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Uninstalling selected applications...",
                        fontSize = 14.sp,
                        color = OnCosmicBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Reclaiming partitioned storage blocks...",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            } else {
                // Primary App Checklist Panel
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Notice Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = GlowYellow,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Apps idle for over 15 days are categorized below. Unloading them speeds up background RAM usage.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (apps.isEmpty()) {
                        Column(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = InfinityTeal)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading installed applications...", fontSize = 13.sp, color = TextSecondary)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            items(apps) { app ->
                                AppItemRow(
                                    app = app,
                                    onToggle = { viewModel.toggleAppItem(app.packageName) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reclaim bar
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
                                        text = "Selected ${selectedApps.size} Apps",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = formattedSelectedSize,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OnCosmicBackground
                                    )
                                }

                                Button(
                                    onClick = { runUninstallBatch() },
                                    enabled = selectedApps.isNotEmpty(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = InfinityTeal,
                                        contentColor = Color.Black,
                                        disabledContainerColor = CustomGrey,
                                        disabledContentColor = TextSecondary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .height(44.dp)
                                        .testTag("app_uninstall_action_button")
                                ) {
                                    Text("Uninstall Batch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
fun AppItemRow(
    app: AppItem,
    onToggle: () -> Unit
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        border = BorderStroke(
            width = 1.dp,
            color = if (app.isSelected) InfinityTeal else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CosmicBackground)
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    tint = if (app.isSystem) TextSecondary else InfinityTeal,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnCosmicBackground,
                    maxLines = 1
                )
                Text(
                    text = app.packageName,
                    fontSize = 8.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Battery consumption
                    Icon(
                        imageVector = Icons.Default.BatteryAlert,
                        contentDescription = "Battery",
                        tint = InfinityPurple,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${app.batteryPercent}% drain",
                        fontSize = 8.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Network Data
                    Icon(
                        imageVector = Icons.Default.DataUsage,
                        contentDescription = "Data",
                        tint = InfinityTeal,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${String.format("%.1f", app.dataUsedMb)} MB used",
                        fontSize = 8.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Idle: ${app.unusedDays}d",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (app.unusedDays > 15) InfinityPurple else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = Formatter.formatShortFileSize(context, app.sizeBytes),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = OnCosmicBackground
            )

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(
                checked = app.isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = InfinityTeal)
            )
        }
    }
}
