package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cleanup_events")
data class CleanupEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,          // e.g. "Quick Clean", "Photos", "Videos", "Boost", "Battery"
    val sizeBytes: Long,       // Space saved in bytes
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "exclusions")
data class Exclusion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val identifier: String,    // Package name for apps, absolute path for files
    val isApp: Boolean,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "schedule_settings")
data class ScheduleSetting(
    @PrimaryKey val id: Int = 1, // Only 1 configuration row
    val isEnabled: Boolean = false,
    val frequency: String = "Weekly", // "Daily", "Weekly", "Monthly"
    val lastRunTimestamp: Long = 0L,
    val nextRunTimestamp: Long = 0L,
    val cleanTempFiles: Boolean = true,
    val cleanEmptyFolders: Boolean = true,
    val cleanLargeFiles: Boolean = false
)
