// app/src/main/java/com/example/csepractice/data/QuestionDao.kt
package com.example.csepractice.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :count")
    fun getRandomQuestions(count: Int): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE category = :category ORDER BY RANDOM() LIMIT :count")
    fun getRandomQuestionsByCategory(category: String, count: Int): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE category IN (:categories) ORDER BY RANDOM() LIMIT :count")
    fun getRandomQuestionsByCategories(categories: List<String>, count: Int): Flow<List<Question>>

    @Insert
    suspend fun insertQuestions(questions: List<Question>)

    @Insert
    suspend fun insertSession(session: PracticeSession)

    @Query("SELECT * FROM practice_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<PracticeSession>>

    @Query("DELETE FROM practice_sessions")
    suspend fun clearAllSessions()
}