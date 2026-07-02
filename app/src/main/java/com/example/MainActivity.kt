package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.CleanupRepository
import com.example.ui.Screen
import com.example.ui.screens.AppManagerScreen
import com.example.ui.screens.BatteryScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PerformanceScreen
import com.example.ui.screens.PhotoCleanerScreen
import com.example.ui.screens.PremiumScreen
import com.example.ui.screens.QuickCleanScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StorageAnalyzerScreen
import com.example.ui.screens.VideoCleanerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.DeviceCareViewModel
import com.example.viewmodel.DeviceCareViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize local persistent Room engine
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CleanupRepository(database)

        // 2. Build the master view model
        val factory = DeviceCareViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[DeviceCareViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // 3. Coordinate navigation routing across modules
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SPLASH
                    ) {
                        // Onboarding & Terms config
                        composable(Screen.SPLASH) {
                            SplashScreen(
                                onNavigateToDashboard = {
                                    navController.navigate(Screen.DASHBOARD) {
                                        popUpTo(Screen.SPLASH) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Central Command dashboard
                        composable(Screen.DASHBOARD) {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        // Quick sweeping junk clean panel
                        composable(Screen.QUICK_CLEAN) {
                            QuickCleanScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Photo duplicates and blurry analyzer
                        composable(Screen.PHOTO_CLEANER) {
                            PhotoCleanerScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Heavy media old video cleaner
                        composable(Screen.VIDEO_CLEANER) {
                            VideoCleanerScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Application analyzer and uninstaller
                        composable(Screen.APP_MANAGER) {
                            AppManagerScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Proportional disk division gauge
                        composable(Screen.STORAGE_ANALYZER) {
                            StorageAnalyzerScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Efficiency charger saver
                        composable(Screen.BATTERY_ANALYSIS) {
                            BatteryScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // System speed RAM booster
                        composable(Screen.PERFORMANCE_BOOST) {
                            PerformanceScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Premium paywall & advanced configuration
                        composable(Screen.PREMIUM) {
                            PremiumScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
