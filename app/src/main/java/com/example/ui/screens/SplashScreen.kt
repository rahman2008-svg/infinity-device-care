package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CosmicBackground
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CustomGrey
import com.example.ui.theme.InfinityPurple
import com.example.ui.theme.InfinityTeal
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSplashFinished by remember { mutableStateOf(false) }
    var hasAgreedTerms by remember { mutableStateOf(false) }
    
    // Check local preferences to skip setup once complete
    val sharedPrefs = remember { context.getSharedPreferences("infinity_care_prefs", Context.MODE_PRIVATE) }
    val isSetupComplete = remember { sharedPrefs.getBoolean("setup_complete", false) }

    // Launcher permissions
    var permissionPhotosGranted by remember { mutableStateOf(false) }
    var permissionFilesGranted by remember { mutableStateOf(false) }
    var permissionNotifyGranted by remember { mutableStateOf(false) }

    val filesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionPhotosGranted = perms[Manifest.permission.READ_MEDIA_IMAGES] == true || 
                                     perms[Manifest.permission.READ_MEDIA_VIDEO] == true
        } else {
            permissionPhotosGranted = perms[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        }
        permissionFilesGranted = perms[Manifest.permission.READ_EXTERNAL_STORAGE] == true || 
                                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
    }

    val notifyLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionNotifyGranted = isGranted
    }

    // Animation scale
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    LaunchedEffect(Unit) {
        if (isSetupComplete) {
            delay(1500)
            onNavigateToDashboard()
        } else {
            delay(2500)
            isSplashFinished = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (!isSplashFinished && !isSetupComplete) {
            // Splash Animation View
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(150.dp)
                ) {
                    // Glowing background circle
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .rotate(rotationAngle)
                            .border(
                                width = 3.dp,
                                brush = Brush.sweepGradient(
                                    colors = listOf(InfinityTeal, InfinityPurple, InfinityTeal)
                                ),
                                shape = RoundedCornerShape(40.dp)
                            )
                    )
                    
                    Image(
                        painter = painterResource(id = R.drawable.infinity_logo),
                        contentDescription = "Infinity Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(32.dp))
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "INFINITY",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = InfinityTeal
                )
                
                Text(
                    text = "DEVICE CARE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 6.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(48.dp))
                
                Text(
                    text = "Optimizing your phone with infinite speed...",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Terms and Permissions Setup Page
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.infinity_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Infinity Device Care",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = InfinityTeal
                        )
                        Text(
                            text = "Setup and Terms configuration",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Welcome note
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = InfinityTeal,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Welcome! Let's optimize and clean your phone for premium performance. Please accept our terms and enable storage accesses.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Terms box
                Text(
                    text = "Terms & Privacy Agreement",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            text = "Infinity Device Care is designed to help you analyze, optimize, and clean cached items, junk files, blurry photos, duplicates, and unused applications on your device.\n\n" +
                                   "By clicking 'Agree & Proceed', you grant permission to scan storage media directory trees and compile reports. Your files are scanned locally on your device; we strictly respect your privacy. No data is harvested or uploaded outside of your device.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hasAgreedTerms = !hasAgreedTerms },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hasAgreedTerms,
                        onCheckedChange = { hasAgreedTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = InfinityTeal),
                        modifier = Modifier.testTag("terms_checkbox")
                    )
                    Text(
                        text = "I accept the Terms and Privacy Policy",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Permissions Matrix
                Text(
                    text = "Permissions Authorization",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = "Grant permissions below to enable powerful analysis functions.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Row 1: Photos & Files
                PermissionRow(
                    title = "Files & Media Scanner",
                    desc = "Required to search for cache, logs, duplicates, and large files.",
                    isGranted = permissionPhotosGranted || permissionFilesGranted,
                    onClick = {
                        val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO
                            )
                        } else {
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }
                        filesLauncher.launch(list)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Row 2: Notifications
                PermissionRow(
                    title = "Smart Notifications",
                    desc = "Alerts you when temporary junk pile-up reaches critical sizes.",
                    isGranted = permissionNotifyGranted,
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notifyLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            permissionNotifyGranted = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Row 3: Accessibility
                PermissionRow(
                    title = "Advanced Accessibility Integration",
                    desc = "Allows Deep Cleaning routines to close persistent memory-draining processes automatically.",
                    isGranted = false, // Explained in UI, toggleable
                    isActionable = true,
                    actionText = "Configure",
                    onClick = {
                        // Explanatory note
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                // Continue Button
                Button(
                    onClick = {
                        if (hasAgreedTerms) {
                            sharedPrefs.edit().putBoolean("setup_complete", true).apply()
                            onNavigateToDashboard()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("get_started_button"),
                    enabled = hasAgreedTerms,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InfinityTeal,
                        contentColor = CosmicBackground,
                        disabledContainerColor = CustomGrey,
                        disabledContentColor = TextSecondary
                    )
                ) {
                    Text(
                        text = "Agree & Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionRow(
    title: String,
    desc: String,
    isGranted: Boolean,
    isActionable: Boolean = false,
    actionText: String = "Authorize",
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        border = BorderStroke(
            width = 1.dp,
            color = if (isGranted) InfinityTeal.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = InfinityTeal,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActionable) CustomGrey else InfinityPurple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (isActionable) actionText else "Grant",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
