package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface CleanupDao {
    @Query("SELECT * FROM cleanup_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<CleanupEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CleanupEvent)

    @Query("DELETE FROM cleanup_events")
    suspend fun clearHistory()
}

@Dao
interface ExclusionDao {
    @Query("SELECT * FROM exclusions ORDER BY addedTimestamp DESC")
    fun getAllExclusions(): Flow<List<Exclusion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExclusion(exclusion: Exclusion)

    @Query("DELETE FROM exclusions WHERE id = :id")
    suspend fun deleteExclusion(id: Int)
    
    @Query("DELETE FROM exclusions WHERE identifier = :identifier")
    suspend fun deleteExclusionByIdentifier(identifier: String)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_settings WHERE id = 1 LIMIT 1")
    fun getSettingFlow(): Flow<ScheduleSetting?>

    @Query("SELECT * FROM schedule_settings WHERE id = 1 LIMIT 1")
    suspend fun getSetting(): ScheduleSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: ScheduleSetting)
}

@Database(
    entities = [CleanupEvent::class, Exclusion::class, ScheduleSetting::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cleanupDao(): CleanupDao
    abstract fun exclusionDao(): ExclusionDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "infinity_device_care_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class CleanupRepository(private val db: AppDatabase) {
    val allEvents: Flow<List<CleanupEvent>> = db.cleanupDao().getAllEvents()
    val allExclusions: Flow<List<Exclusion>> = db.exclusionDao().getAllExclusions()
    val scheduleSetting: Flow<ScheduleSetting?> = db.scheduleDao().getSettingFlow()

    suspend fun insertEvent(event: CleanupEvent) {
        db.cleanupDao().insertEvent(event)
    }

    suspend fun clearHistory() {
        db.cleanupDao().clearHistory()
    }

    suspend fun addExclusion(exclusion: Exclusion) {
        db.exclusionDao().insertExclusion(exclusion)
    }

    suspend fun deleteExclusion(id: Int) {
        db.exclusionDao().deleteExclusion(id)
    }
    
    suspend fun deleteExclusionByIdentifier(identifier: String) {
        db.exclusionDao().deleteExclusionByIdentifier(identifier)
    }

    suspend fun getScheduleSetting(): ScheduleSetting {
        return db.scheduleDao().getSetting() ?: ScheduleSetting()
    }

    suspend fun saveScheduleSetting(setting: ScheduleSetting) {
        db.scheduleDao().saveSetting(setting)
    }
}
