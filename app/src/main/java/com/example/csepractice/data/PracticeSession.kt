package com.example.csepractice.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "practice_sessions")
data class PracticeSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val score: Int,  // e.g., 80 (percentage)
    val correctCount: Int,
    val totalQuestions: Int
)