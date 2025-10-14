package com.example.csepractice.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Question::class, PracticeSession::class], version = 4, exportSchema = true)
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
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()  // For dev, removes data on mismatch
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val cursorQuestions = database.query("PRAGMA table_info(questions)")
                var hasCategory = false
                val nameIndex = cursorQuestions.getColumnIndex("name")
                if (cursorQuestions.moveToFirst()) {
                    do {
                        if (nameIndex >= 0 && cursorQuestions.getString(nameIndex) == "category") {
                            hasCategory = true
                            break
                        }
                    } while (cursorQuestions.moveToNext())
                }
                cursorQuestions.close()
                if (!hasCategory) {
                    database.execSQL("ALTER TABLE questions ADD COLUMN category TEXT NOT NULL DEFAULT ''")
                }

                val cursorSessions = database.query("PRAGMA table_info(practice_sessions)")
                var hasCategories = false
                val nameIndexSessions = cursorSessions.getColumnIndex("name")
                if (cursorSessions.moveToFirst()) {
                    do {
                        if (nameIndexSessions >= 0 && cursorSessions.getString(nameIndexSessions) == "categories") {
                            hasCategories = true
                            break
                        }
                    } while (cursorSessions.moveToNext())
                }
                cursorSessions.close()
                if (!hasCategories) {
                    database.execSQL("ALTER TABLE practice_sessions ADD COLUMN categories TEXT NOT NULL DEFAULT ''")
                }
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val cursorQuestions = database.query("PRAGMA table_info(questions)")
                var hasDifficulty = false
                val nameIndex = cursorQuestions.getColumnIndex("name")
                if (cursorQuestions.moveToFirst()) {
                    do {
                        if (nameIndex >= 0 && cursorQuestions.getString(nameIndex) == "difficulty") {
                            hasDifficulty = true
                            break
                        }
                    } while (cursorQuestions.moveToNext())
                }
                cursorQuestions.close()
                if (!hasDifficulty) {
                    database.execSQL("ALTER TABLE questions ADD COLUMN difficulty TEXT NOT NULL DEFAULT 'Medium'")
                }

                val cursorSessions = database.query("PRAGMA table_info(practice_sessions)")
                var hasTimeTaken = false
                val nameIndexSessions = cursorSessions.getColumnIndex("name")
                if (cursorSessions.moveToFirst()) {
                    do {
                        if (nameIndexSessions >= 0 && cursorSessions.getString(nameIndexSessions) == "timeTaken") {
                            hasTimeTaken = true
                            break
                        }
                    } while (cursorSessions.moveToNext())
                }
                cursorSessions.close()
                if (!hasTimeTaken) {
                    database.execSQL("ALTER TABLE practice_sessions ADD COLUMN timeTaken LONG NOT NULL DEFAULT 0")
                }
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Empty migration to force version bump and fallback if needed
            }
        }
    }
}