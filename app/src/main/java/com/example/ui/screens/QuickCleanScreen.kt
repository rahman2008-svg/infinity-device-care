package com.example.ui.screens

import android.text.format.Formatter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FolderZip
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
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.ui.theme.GlowGreen
import com.example.ui.theme.InfinityPurple
import com.example.ui.theme.InfinityTeal
import com.example.ui.theme.OnCosmicBackground
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CleanableItem
import com.example.viewmodel.DeviceCareViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCleanScreen(
    viewModel: DeviceCareViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cleanItems by viewModel.quickCleanItems.collectAsState()

    var isCleaningInProgress by remember { mutableStateOf(false) }
    var cleaningStepText by remember { mutableStateOf("") }
    var cleaningProgress by remember { mutableStateOf(0f) }
    
    var showSuccessScreen by remember { mutableStateOf(false) }
    var cleanedSpaceBytes by remember { mutableStateOf(0L) }

    // Calc space ready for deletion
    val selectedItems = cleanItems.filter { it.isSelected }
    val totalSelectedSize = selectedItems.sumOf { it.sizeBytes }
    val formattedSelectedSize = Formatter.formatShortFileSize(context, totalSelectedSize)

    val runCleaningProcess = {
        coroutineScope.launch {
            isCleaningInProgress = true
            cleanedSpaceBytes = totalSelectedSize

            val stepStrings = listOf(
                "Locating secondary app logs..." to 0.15f,
                "Deleting orphaned temporary folders..." to 0.4f,
                "Flushing memory system caches..." to 0.7f,
                "Clearing redundant package installer assets..." to 0.9f,
                "Rebuilding space allocations..." to 1.0f
            )

            for (step in stepStrings) {
                cleaningStepText = step.first
                val targetProgress = step.second
                while (cleaningProgress < targetProgress) {
                    cleaningProgress += 0.05f
                    delay(50)
                }
                delay(100)
            }

            viewModel.performQuickClean { actualFreed ->
                isCleaningInProgress = false
                showSuccessScreen = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Clean", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
            if (showSuccessScreen) {
                // Success screen representation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(160.dp)
                    ) {
                        // Soft ring pulsation
                        Box(
                            modifier = Modifier
                                .size(140.dp)
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
                            modifier = Modifier.size(85.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "System Reclaimed!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val savedString = Formatter.formatShortFileSize(context, cleanedSpaceBytes)
                    Text(
                        text = "$savedString Freed Successfully",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InfinityTeal,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Your storage has been optimized for faster operation and responsive background allocations.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 12.dp),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = InfinityTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                            .testTag("success_done_button")
                    ) {
                        Text("Done", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            } else if (isCleaningInProgress) {
                // Active cleaning loader screen representation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { cleaningProgress },
                            color = InfinityTeal,
                            strokeWidth = 6.dp,
                            modifier = Modifier.size(120.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Sweep icon",
                            tint = InfinityPurple,
                            modifier = Modifier.size(45.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Optimizing System Storage",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = cleaningStepText,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    LinearProgressIndicator(
                        progress = { cleaningProgress },
                        color = InfinityTeal,
                        trackColor = CosmicSurface,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(6.dp)
                            .clip(CircleShape)
                    )
                }
            } else {
                // Diagnostic selection checklist screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Reclaim header summary
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clean Icon",
                                tint = InfinityTeal,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Ready to Reclaim",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = formattedSelectedSize,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = OnCosmicBackground
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (cleanItems.isEmpty()) {
                        Column(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Prised clean",
                                tint = GlowGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "System is absolutely pristine!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnCosmicBackground
                            )
                            Text(
                                text = "No junk, redundant cache, or installers detected.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // List items representing files
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            items(cleanItems) { item ->
                                CleanableItemRow(
                                    item = item,
                                    onToggle = { viewModel.toggleQuickCleanItem(item.id) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Button
                    Button(
                        onClick = { runCleaningProcess() },
                        enabled = totalSelectedSize > 0L,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = InfinityTeal,
                            contentColor = Color.Black,
                            disabledContainerColor = CosmicSurface,
                            disabledContentColor = TextSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("execute_clean_button")
                    ) {
                        Text(
                            text = if (totalSelectedSize > 0L) "Clean $formattedSelectedSize" else "Select Items",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CleanableItemRow(
    item: CleanableItem,
    onToggle: () -> Unit
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = InfinityTeal)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnCosmicBackground
                )
                Text(
                    text = item.path,
                    fontSize = 9.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Formatter.formatShortFileSize(context, item.sizeBytes),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.sizeBytes > 100L * 1024 * 1024) InfinityPurple else OnCosmicBackground
            )
        }
    }
}
