package com.example.csepractice.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.csepractice.utils.ListConverters  // We'll add this next

@Database(entities = [Question::class, PracticeSession::class], version = 1, exportSchema = false)
@TypeConverters(ListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cse_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}