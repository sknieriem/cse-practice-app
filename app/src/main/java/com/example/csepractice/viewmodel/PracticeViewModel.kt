package com.example.csepractice.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.csepractice.data.PracticeSession
import com.example.csepractice.data.Question
import com.example.csepractice.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PracticeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuestionRepository(application.applicationContext)

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _selectedAnswers = MutableStateFlow(mutableMapOf<Int, Int>())  // questionIndex -> selectedOptionIndex
    val selectedAnswers: StateFlow<Map<Int, Int>> = _selectedAnswers

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    val sessions: Flow<List<PracticeSession>> = repository.getAllSessions()

    init {
        viewModelScope.launch {
            repository.seedQuestionsIfEmpty()
            loadQuestions()
        }
    }

    private suspend fun loadQuestions() {
        repository.getRandomQuestions(10).collect { qs ->
            _questions.value = qs
        }
    }

    fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        _selectedAnswers.value = _selectedAnswers.value.toMutableMap().apply {
            put(questionIndex, optionIndex)
        }
    }

    fun nextQuestion() {
        if (_currentIndex.value < _questions.value.size - 1) {
            _currentIndex.value++
        }
    }

    fun previousQuestion() {
        if (_currentIndex.value > 0) {
            _currentIndex.value--
        }
    }

    fun calculateScore() {
        var correct = 0
        _questions.value.forEachIndexed { index, question ->
            if (_selectedAnswers.value[index] == question.correctIndex) correct++
        }
        _score.value = (correct * 100) / _questions.value.size
        viewModelScope.launch {
            repository.insertSession(PracticeSession(score = _score.value, correctCount = correct, totalQuestions = _questions.value.size))
        }
    }

    fun resetForNewSession() {
        _questions.value = emptyList()
        _currentIndex.value = 0
        _selectedAnswers.value = mutableMapOf()
        _score.value = 0
        viewModelScope.launch {
            loadQuestions()
        }
    }
}