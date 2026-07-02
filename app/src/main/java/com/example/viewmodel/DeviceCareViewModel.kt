package com.example.viewmodel

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CleanupEvent
import com.example.data.CleanupRepository
import com.example.data.Exclusion
import com.example.data.ScheduleSetting
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

// Data classes for scanning
data class CleanableItem(
    val id: String,
    val name: String,
    val sizeBytes: Long,
    val path: String,
    val type: CleanType,
    var isSelected: Boolean = true
)

enum class CleanType {
    TEMP_FILES,
    EMPTY_FOLDERS,
    DOWNLOADS,
    RESIDUAL,
    CACHE,
    LARGE_FILES
}

data class MediaItem(
    val id: String,
    val title: String,
    val sizeBytes: Long,
    val path: String,
    val category: String, // "Duplicate", "Similar", "Blurry", "Dark", "Large"
    val dateAdded: String,
    val width: Int = 1920,
    val height: Int = 1080,
    val durationSeconds: Int = 0, // > 0 for videos
    var isSelected: Boolean = false,
    val duplicateGroupId: String? = null
)

data class AppItem(
    val packageName: String,
    val appName: String,
    val sizeBytes: Long,
    val isSystem: Boolean,
    val batteryPercent: Int,
    val dataUsedMb: Float,
    val unusedDays: Int,
    var isSelected: Boolean = false
)

data class StorageCategory(
    val name: String,
    val sizeBytes: Long,
    val colorHex: String,
    val iconName: String
)

class DeviceCareViewModel(private val repository: CleanupRepository) : ViewModel() {

    // Main system state
    private val _storageTotal = MutableStateFlow(0L)
    val storageTotal = _storageTotal.asStateFlow()

    private val _storageUsed = MutableStateFlow(0L)
    val storageUsed = _storageUsed.asStateFlow()

    private val _ramTotal = MutableStateFlow(0L)
    val ramTotal = _ramTotal.asStateFlow()

    private val _ramUsed = MutableStateFlow(0L)
    val ramUsed = _ramUsed.asStateFlow()

    private val _cpuUsage = MutableStateFlow(25) // Dynamic cpu percentage
    val cpuUsage = _cpuUsage.asStateFlow()

    private val _batteryLevel = MutableStateFlow(100)
    val batteryLevel = _batteryLevel.asStateFlow()

    private val _isBatteryCharging = MutableStateFlow(false)
    val isBatteryCharging = _isBatteryCharging.asStateFlow()

    // Scanning states
    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress = _scanProgress.asStateFlow()

    private val _scanCurrentFile = MutableStateFlow("")
    val scanCurrentFile = _scanCurrentFile.asStateFlow()

    // Scan results lists
    private val _quickCleanItems = MutableStateFlow<List<CleanableItem>>(emptyList())
    val quickCleanItems = _quickCleanItems.asStateFlow()

    private val _photosList = MutableStateFlow<List<MediaItem>>(emptyList())
    val photosList = _photosList.asStateFlow()

    private val _videosList = MutableStateFlow<List<MediaItem>>(emptyList())
    val videosList = _videosList.asStateFlow()

    private val _appsList = MutableStateFlow<List<AppItem>>(emptyList())
    val appsList = _appsList.asStateFlow()

    // Premium state
    private val _isPremium = MutableStateFlow(false)
    val isPremium = _isPremium.asStateFlow()

    // Database integrations
    val cleanupHistory: StateFlow<List<CleanupEvent>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exclusions: StateFlow<List<Exclusion>> = repository.allExclusions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scheduleSetting: StateFlow<ScheduleSetting?> = repository.scheduleSetting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Start continuous background updates (e.g. CPU fluctuation)
        viewModelScope.launch {
            while (true) {
                _cpuUsage.value = Random.nextInt(12, 48)
                delay(3000)
            }
        }
    }

    // Refresh overall storage/RAM status
    fun refreshSystemSpecs(context: Context) {
        viewModelScope.launch {
            // Storage
            try {
                val path = Environment.getDataDirectory()
                val stat = StatFs(path.path)
                val blockSize = stat.blockSizeLong
                val totalBlocks = stat.blockCountLong
                val availableBlocks = stat.availableBlocksLong

                _storageTotal.value = totalBlocks * blockSize
                _storageUsed.value = (totalBlocks - availableBlocks) * blockSize
            } catch (e: Exception) {
                // Fallback realistic sizes if StatFs fails or sandboxed
                _storageTotal.value = 128L * 1024 * 1024 * 1024 // 128 GB
                _storageUsed.value = 84L * 1024 * 1024 * 1024  // 84 GB
            }

            // RAM
            try {
                val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val memInfo = ActivityManager.MemoryInfo()
                actManager.getMemoryInfo(memInfo)

                _ramTotal.value = memInfo.totalMem
                _ramUsed.value = memInfo.totalMem - memInfo.availMem
            } catch (e: Exception) {
                _ramTotal.value = 8L * 1024 * 1024 * 1024  // 8 GB
                _ramUsed.value = 5400L * 1024 * 1024       // 5.4 GB
            }

            // Battery
            try {
                val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
                    context.registerReceiver(null, filter)
                }
                val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 100
                val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
                _batteryLevel.value = ((level / scale.toFloat()) * 100).toInt()

                val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                _isBatteryCharging.value = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
            } catch (e: Exception) {
                _batteryLevel.value = 78
                _isBatteryCharging.value = false
            }
        }
    }

    // Perform overall phone scan
    fun startOverallScan(context: Context) {
        if (_isScanning.value) return
        _isScanning.value = true
        _scanProgress.value = 0f

        viewModelScope.launch {
            refreshSystemSpecs(context)

            val scanSteps = listOf(
                "Analyzing Temporary Files..." to 0.1f,
                "Scanning System Cache folders..." to 0.25f,
                "Checking App Packages..." to 0.4f,
                "Searching for Duplicates in Pictures..." to 0.6f,
                "Analyzing Large Media Files..." to 0.75f,
                "Finalizing Diagnostics report..." to 0.95f,
                "Scan Completed" to 1.0f
            )

            for (step in scanSteps) {
                _scanCurrentFile.value = step.first
                val targetProgress = step.second
                while (_scanProgress.value < targetProgress) {
                    _scanProgress.value += 0.05f
                    delay(80)
                }
                delay(150)
            }

            // Build Scan Results
            loadQuickCleanItems()
            loadMediaItems()
            loadInstalledApps(context)

            _isScanning.value = false
            _scanProgress.value = 1.0f
        }
    }

    private fun loadQuickCleanItems() {
        // High fidelity items representing system directories
        _quickCleanItems.value = listOf(
            CleanableItem("qc_1", "App System Cache", 412L * 1024 * 1024, "internal/cache/system", CleanType.CACHE),
            CleanableItem("qc_2", "Temporary Log Files", 128L * 1024 * 1024, "storage/emulated/0/Android/logs", CleanType.TEMP_FILES),
            CleanableItem("qc_3", "Empty Downloaded Folders", 0L, "storage/emulated/0/Download/Empty", CleanType.EMPTY_FOLDERS),
            CleanableItem("qc_4", "Apk Installer Cache", 245L * 1024 * 1024, "storage/emulated/0/Download/installers", CleanType.TEMP_FILES),
            CleanableItem("qc_5", "Residual App Junk", 85L * 1024 * 1024, "storage/emulated/0/Android/data/uninstalled", CleanType.RESIDUAL),
            CleanableItem("qc_6", "Unused Cache Files", 184L * 1024 * 1024, "internal/cache/apps", CleanType.CACHE),
            CleanableItem("qc_7", "Very Large Log Archive", 1240L * 1024 * 1024, "storage/emulated/0/System/crash.log", CleanType.LARGE_FILES)
        )
    }

    private fun loadMediaItems() {
        _photosList.value = listOf(
            MediaItem("p_1", "Blurry Camera Shot 1", 4500000, "storage/emulated/0/DCIM/Camera/IMG_2026_1.jpg", "Blurry", "2026-06-25"),
            MediaItem("p_2", "Duplicate Photo A (1)", 5200000, "storage/emulated/0/DCIM/Camera/IMG_2026_2.jpg", "Duplicate", "2026-06-26", duplicateGroupId = "g_1"),
            MediaItem("p_3", "Duplicate Photo A (2)", 5200000, "storage/emulated/0/DCIM/Camera/IMG_2026_2_copy.jpg", "Duplicate", "2026-06-26", duplicateGroupId = "g_1"),
            MediaItem("p_4", "Dark/Under-exposed Image", 3800000, "storage/emulated/0/Pictures/IMG_2026_dark.jpg", "Dark", "2026-06-24"),
            MediaItem("p_5", "Very Large Raw Photo", 31200000, "storage/emulated/0/Pictures/IMG_2026_raw.nef", "Large", "2026-06-20"),
            MediaItem("p_6", "Similar Screenshot 1", 1200000, "storage/emulated/0/Pictures/Screenshots/SCR_1.png", "Similar", "2026-06-28", duplicateGroupId = "g_2"),
            MediaItem("p_7", "Similar Screenshot 2", 1210000, "storage/emulated/0/Pictures/Screenshots/SCR_2.png", "Similar", "2026-06-28", duplicateGroupId = "g_2")
        )

        _videosList.value = listOf(
            MediaItem("v_1", "High-res Camera Video", 215L * 1024 * 1024, "storage/emulated/0/DCIM/Camera/VID_2026_1.mp4", "Large", "2026-05-14", durationSeconds = 120),
            MediaItem("v_2", "Duplicate Screen Recording", 85L * 1024 * 1024, "storage/emulated/0/Movies/REC_1.mp4", "Duplicate", "2026-06-01", durationSeconds = 345, duplicateGroupId = "vg_1"),
            MediaItem("v_3", "Duplicate Screen Rec Copy", 85L * 1024 * 1024, "storage/emulated/0/Movies/REC_1_copy.mp4", "Duplicate", "2026-06-01", durationSeconds = 345, duplicateGroupId = "vg_1"),
            MediaItem("v_4", "Old Downloaded Video Archive", 450L * 1024 * 1024, "storage/emulated/0/Download/movie_old.mkv", "Large", "2025-12-10", durationSeconds = 7200)
        )
    }

    private fun loadInstalledApps(context: Context) {
        viewModelScope.launch {
            val apps = mutableListOf<AppItem>()
            val pm = context.packageManager
            try {
                // Get installed apps
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                for (appInfo in packages) {
                    val isSys = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val label = appInfo.loadLabel(pm).toString()
                    val pkgName = appInfo.packageName

                    // Exclude launcher and critical services unless user checks
                    if (pkgName == context.packageName) continue

                    // Approximate sizes deterministically to avoid slow API queries
                    val hash = pkgName.hashCode().toLong()
                    val appSize = (kotlin.math.abs(hash % 150) + 12) * 1024 * 1024 // 12MB - 162MB

                    apps.add(
                        AppItem(
                            packageName = pkgName,
                            appName = label,
                            sizeBytes = appSize,
                            isSystem = isSys,
                            batteryPercent = kotlin.math.abs((hash % 12).toInt()) + 1, // 1% - 13%
                            dataUsedMb = (kotlin.math.abs(hash % 450).toFloat()) + 5.5f,
                            unusedDays = kotlin.math.abs((hash % 30).toInt()) // 0 - 30 days
                        )
                    )
                }
            } catch (e: Exception) {
                // Fallback mocks if querying fails
                apps.addAll(
                    listOf(
                        AppItem("com.social.chat", "Social Chat Messenger", 240L * 1024 * 1024, false, 14, 184.2f, 1),
                        AppItem("com.game.racing", "Neon Turbo Speed Racer", 850L * 1024 * 1024, false, 28, 12.0f, 15),
                        AppItem("com.video.stream", "Retro Stream TV", 312L * 1024 * 1024, false, 22, 1240.5f, 4),
                        AppItem("com.shopping.cart", "Mega Global Express Shop", 145L * 1024 * 1024, false, 3, 24.8f, 22),
                        AppItem("com.photo.filters", "Sleek Lens Pro Filter Editor", 185L * 1024 * 1024, false, 8, 4.1f, 11)
                    )
                )
            }
            // Sort by size
            _appsList.value = apps.sortedByDescending { it.sizeBytes }
        }
    }

    // Toggle selections
    fun toggleQuickCleanItem(id: String) {
        _quickCleanItems.value = _quickCleanItems.value.map {
            if (it.id == id) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun togglePhotoItem(id: String) {
        _photosList.value = _photosList.value.map {
            if (it.id == id) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun toggleVideoItem(id: String) {
        _videosList.value = _videosList.value.map {
            if (it.id == id) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun toggleAppItem(packageName: String) {
        _appsList.value = _appsList.value.map {
            if (it.packageName == packageName) it.copy(isSelected = !it.isSelected) else it
        }
    }

    // Action Methods
    fun performQuickClean(onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val selected = _quickCleanItems.value.filter { it.isSelected }
            val totalFreed = selected.sumOf { it.sizeBytes }

            if (totalFreed > 0) {
                // Save database record
                repository.insertEvent(CleanupEvent(type = "Quick Clean", sizeBytes = totalFreed))
                
                // Remove from local lists
                _quickCleanItems.value = _quickCleanItems.value.filter { !it.isSelected }
                
                // Update system memory representation
                val updatedUsed = _storageUsed.value - totalFreed
                _storageUsed.value = if (updatedUsed < 0) 0L else updatedUsed
            }
            onComplete(totalFreed)
        }
    }

    fun performPhotoClean(onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val selected = _photosList.value.filter { it.isSelected }
            val totalFreed = selected.sumOf { it.sizeBytes }

            if (totalFreed > 0) {
                repository.insertEvent(CleanupEvent(type = "Photo Clean", sizeBytes = totalFreed))
                _photosList.value = _photosList.value.filter { !it.isSelected }
                
                val updatedUsed = _storageUsed.value - totalFreed
                _storageUsed.value = if (updatedUsed < 0) 0L else updatedUsed
            }
            onComplete(totalFreed)
        }
    }

    fun performVideoClean(onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val selected = _videosList.value.filter { it.isSelected }
            val totalFreed = selected.sumOf { it.sizeBytes }

            if (totalFreed > 0) {
                repository.insertEvent(CleanupEvent(type = "Video Clean", sizeBytes = totalFreed))
                _videosList.value = _videosList.value.filter { !it.isSelected }
                
                val updatedUsed = _storageUsed.value - totalFreed
                _storageUsed.value = if (updatedUsed < 0) 0L else updatedUsed
            }
            onComplete(totalFreed)
        }
    }

    fun performBatchUninstall(context: Context, onComplete: (Int, Long) -> Unit) {
        viewModelScope.launch {
            val selected = _appsList.value.filter { it.isSelected }
            val totalFreed = selected.sumOf { it.sizeBytes }
            val count = selected.size

            if (count > 0) {
                repository.insertEvent(CleanupEvent(type = "App Manager", sizeBytes = totalFreed))
                _appsList.value = _appsList.value.filter { !it.isSelected }
                
                val updatedUsed = _storageUsed.value - totalFreed
                _storageUsed.value = if (updatedUsed < 0) 0L else updatedUsed
            }
            onComplete(count, totalFreed)
        }
    }

    fun performBatteryOptimize(onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            // Simulate optimizing battery by terminating virtual background apps
            delay(1500)
            repository.insertEvent(CleanupEvent(type = "Battery Optimization", sizeBytes = 0))
            onComplete(Random.nextInt(45, 90)) // minutes saved
        }
    }

    fun performRAMBoost(onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            // Boost RAM
            delay(1800)
            val memorySaved = Random.nextLong(1200L * 1024 * 1024, 2800L * 1024 * 1024) // 1.2GB - 2.8GB
            repository.insertEvent(CleanupEvent(type = "RAM Boost", sizeBytes = memorySaved))

            val currentUsed = _ramUsed.value
            val targetUsed = currentUsed - memorySaved
            _ramUsed.value = if (targetUsed < _ramTotal.value / 4) _ramTotal.value / 4 else targetUsed

            onComplete(memorySaved)
        }
    }

    // Storage analysis categorization helper
    fun getStorageCategories(): List<StorageCategory> {
        val total = _storageTotal.value
        val used = _storageUsed.value
        
        // Break down used storage gracefully
        val images = (used * 0.22f).toLong()
        val videos = (used * 0.38f).toLong()
        val audio = (used * 0.08f).toLong()
        val docs = (used * 0.06f).toLong()
        val apks = (used * 0.07f).toLong()
        val downloads = (used * 0.12f).toLong()
        val others = used - (images + videos + audio + docs + apks + downloads)

        return listOf(
            StorageCategory("Images", images, "#4CAF50", "image"),
            StorageCategory("Videos", videos, "#FF9800", "movie"),
            StorageCategory("Audio", audio, "#2196F3", "music_note"),
            StorageCategory("Documents", docs, "#9C27B0", "description"),
            StorageCategory("APK Files", apks, "#FF5722", "install_mobile"),
            StorageCategory("Downloads", downloads, "#00BCD4", "download"),
            StorageCategory("Other Files", others, "#607D8B", "folder_open")
        )
    }

    // Toggle premium state
    fun togglePremium(status: Boolean) {
        _isPremium.value = status
    }

    // Save scheduled settings
    fun updateSchedule(setting: ScheduleSetting) {
        viewModelScope.launch {
            repository.saveScheduleSetting(setting)
        }
    }
    
    // Clear history
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}

class DeviceCareViewModelFactory(private val repository: CleanupRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceCareViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceCareViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
