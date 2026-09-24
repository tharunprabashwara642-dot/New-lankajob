package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AdvertisementDao
import com.example.data.local.dao.JobDao
import com.example.data.local.dao.PaymentDao
import com.example.data.local.dao.SavedJobDao
import com.example.data.local.dao.UserProfileDao
import com.example.data.local.entity.AdvertisementEntity
import com.example.data.local.entity.JobEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.SavedJobEntity
import com.example.data.local.entity.UserProfileEntity

@Database(
    entities = [
        JobEntity::class,
        SavedJobEntity::class,
        UserProfileEntity::class,
        AdvertisementEntity::class,
        PaymentEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LankaJobsDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun savedJobDao(): SavedJobDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun advertisementDao(): AdvertisementDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: LankaJobsDatabase? = null

        fun getInstance(context: Context): LankaJobsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LankaJobsDatabase::class.java,
                    "lankajobs.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
