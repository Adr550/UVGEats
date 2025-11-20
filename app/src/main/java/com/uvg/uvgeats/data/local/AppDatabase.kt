package com.uvg.uvgeats.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [LocalFoodItem::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object {
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "uvg_eats_db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
