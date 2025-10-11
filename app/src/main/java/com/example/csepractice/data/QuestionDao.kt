package com.example.csepractice.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :count")
    fun getRandomQuestions(count: Int): Flow<List<Question>>

    @Insert
    suspend fun insertQuestions(questions: List<Question>)

    @Insert
    suspend fun insertSession(session: PracticeSession)

    @Query("SELECT * FROM practice_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<PracticeSession>>
}