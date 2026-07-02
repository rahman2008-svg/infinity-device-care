package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScheduleSetting
import com.example.ui.theme.CosmicBackground
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.GlowGreen
import com.example.ui.theme.GlowYellow
import com.example.ui.theme.InfinityPink
import com.example.ui.theme.InfinityPurple
import com.example.ui.theme.InfinityTeal
import com.example.ui.theme.OnCosmicBackground
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.DeviceCareViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    viewModel: DeviceCareViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val isPremium by viewModel.isPremium.collectAsState()
    val scheduleSettingRaw by viewModel.scheduleSetting.collectAsState()

    var isUpgrading by remember { mutableStateOf(false) }
    var showCongratMessage by remember { mutableStateOf(false) }

    // Retrieve database settings
    val scheduleSetting = scheduleSettingRaw ?: ScheduleSetting()

    val runUpgradeFlow = {
        coroutineScope.launch {
            isUpgrading = true
            delay(1500)
            viewModel.togglePremium(true)
            isUpgrading = false
            showCongratMessage = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premium Controls", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
            if (showCongratMessage) {
                // Celebration panel
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
                                    brush = Brush.linearGradient(listOf(InfinityTeal, InfinityPurple)),
                                    shape = CircleShape
                                )
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Premium star",
                            tint = GlowYellow,
                            modifier = Modifier.size(70.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Congratulations!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Unlocked Infinity Plus Tier",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = InfinityTeal,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Automatic cleaning algorithms, customized schedules, and comprehensive deep storage analyzers are now unlocked.",
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
                        onClick = { showCongratMessage = false },
                        colors = ButtonDefaults.buttonColors(containerColor = InfinityTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                            .testTag("celebrate_unlock_done")
                    ) {
                        Text("Access Advanced Controls", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            } else if (isUpgrading) {
                // Purchase Loader
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = InfinityTeal, strokeWidth = 5.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Contacting Secure Payment Gateways...",
                        fontSize = 14.sp,
                        color = OnCosmicBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (!isPremium) {
                // PAYWALL View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Glow Star",
                            tint = InfinityTeal,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "INFINITY PLUS",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = OnCosmicBackground
                        )
                        Text(
                            text = "The ultimate phone optimization standard",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Features checklist
                        PremiumBenefitRow("Scheduled Auto Cleanup", "Runs sweeps in the background on trigger intervals.")
                        Spacer(modifier = Modifier.height(16.dp))
                        PremiumBenefitRow("Deep Memory Flushing", "Identifies hidden application cache subtrees.")
                        Spacer(modifier = Modifier.height(16.dp))
                        PremiumBenefitRow("Advanced Exclusions Settings", "Locks system configurations from scanner sweeps.")
                        Spacer(modifier = Modifier.height(16.dp))
                        PremiumBenefitRow("Ad-free Command Center", "Premium fluid interface with zero distractions.")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$1.99 / Month",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = InfinityTeal
                                )
                                Text(
                                    text = "Cancel anytime. Lifetime security warranty.",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { runUpgradeFlow() },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = InfinityPurple),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("upgrade_plus_button")
                        ) {
                            Text("Upgrade to Plus", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Simulate payment trial. Fully functional.",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                // PREMIUM CONTROLS VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Sparkle Status banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                        border = BorderStroke(1.dp, InfinityTeal.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Sparkle",
                                tint = GlowYellow,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Infinity Plus Enabled",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnCosmicBackground
                                )
                                Text(
                                    text = "Lifetime access to scheduled cleaning assets.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Automated Cleanup Settings",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnCosmicBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Scheduled Clean switch
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = InfinityTeal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Scheduled Cleaning",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OnCosmicBackground
                                        )
                                        Text(
                                            text = "Automatically optimize at intervals",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Switch(
                                    checked = scheduleSetting.isEnabled,
                                    onCheckedChange = { isChecked ->
                                        viewModel.updateSchedule(scheduleSetting.copy(isEnabled = isChecked))
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = InfinityTeal,
                                        checkedTrackColor = InfinityPurple
                                    ),
                                    modifier = Modifier.testTag("scheduler_toggle")
                                )
                            }

                            if (scheduleSetting.isEnabled) {
                                Spacer(modifier = Modifier.height(16.dp))

                                // Frequencies
                                Text(
                                    text = "Sweeping Frequency",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    FrequencyOption("Daily", scheduleSetting.frequency == "Daily") {
                                        viewModel.updateSchedule(scheduleSetting.copy(frequency = "Daily"))
                                    }
                                    FrequencyOption("Weekly", scheduleSetting.frequency == "Weekly") {
                                        viewModel.updateSchedule(scheduleSetting.copy(frequency = "Weekly"))
                                    }
                                    FrequencyOption("Monthly", scheduleSetting.frequency == "Monthly") {
                                        viewModel.updateSchedule(scheduleSetting.copy(frequency = "Monthly"))
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Targets checkbox toggles
                                Text(
                                    text = "Cleaning Targets",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                TargetToggleRow("Flush Cache & Logs", scheduleSetting.cleanTempFiles) {
                                    viewModel.updateSchedule(scheduleSetting.copy(cleanTempFiles = !scheduleSetting.cleanTempFiles))
                                }
                                TargetToggleRow("Remove Empty folders", scheduleSetting.cleanEmptyFolders) {
                                    viewModel.updateSchedule(scheduleSetting.copy(cleanEmptyFolders = !scheduleSetting.cleanEmptyFolders))
                                }
                                TargetToggleRow("Delete Large crash logs", scheduleSetting.cleanLargeFiles) {
                                    viewModel.updateSchedule(scheduleSetting.copy(cleanLargeFiles = !scheduleSetting.cleanLargeFiles))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Downgrade options for testing
                    Text(
                        text = "Reset Premium Account Status",
                        fontSize = 11.sp,
                        color = InfinityPink,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.togglePremium(false)
                            }
                            .padding(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun PremiumBenefitRow(
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(InfinityTeal.copy(alpha = 0.15f))
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = InfinityTeal,
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = OnCosmicBackground
            )
            Text(
                text = desc,
                fontSize = 10.sp,
                color = TextSecondary,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun FrequencyOption(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onSelect() }
            .padding(4.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = InfinityTeal)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) InfinityTeal else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TargetToggleRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = OnCosmicBackground)
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = InfinityTeal,
                checkedTrackColor = InfinityPurple
            ),
            modifier = Modifier.scale(0.8f) // Reduce size slightly for secondary options
        )
    }
}

// Simple extension helper to scale switches
private fun Modifier.scale(scale: Float): Modifier = this.then(
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout((placeable.width * scale).toInt(), (placeable.height * scale).toInt()) {
            placeable.placeRelativeWithLayer(0, 0) {
                scaleX = scale
                scaleY = scale
            }
        }
    }
)
