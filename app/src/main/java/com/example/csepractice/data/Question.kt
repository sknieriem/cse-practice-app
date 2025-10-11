package com.example.csepractice.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,  // e.g., "Numerical Ability"
    val text: String,
    val options: List<String>,  // List of 5 options
    val correctIndex: Int,  // 0-4
    val language: String = "en"  // en, fil, ceb
)