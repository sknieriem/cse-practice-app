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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class PracticeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuestionRepository(application)

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val selectedAnswers: StateFlow<Map<Int, Int>> = _selectedAnswers.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _selectedCategories = MutableStateFlow<List<String>>(emptyList())
    val selectedCategories: StateFlow<List<String>> = _selectedCategories.asStateFlow()

    val sessions: Flow<List<PracticeSession>> = repository.getAllSessions()

    init {
        viewModelScope.launch {
            repository.seedQuestionsIfEmpty()
        }
    }

    fun toggleCategory(category: String) {
        val current = _selectedCategories.value.toMutableList()
        if (current.contains(category)) {
            current.remove(category)
        } else {
            current.add(category)
        }
        _selectedCategories.value = current
    }

    fun startPractice() {
        val categories = _selectedCategories.value
        if (categories.isEmpty()) {
            loadAllQuestions()
        } else {
            loadQuestionsForCategories(categories)
        }
    }

    private fun loadQuestionsForCategories(categories: List<String>) {
        viewModelScope.launch {
            val questionsFlow = repository.getRandomQuestionsByCategories(categories, 10)
            questionsFlow.collect { loadedQuestions ->
                _questions.value = loadedQuestions
            }
        }
    }

    private fun loadAllQuestions() {
        viewModelScope.launch {
            val questionsFlow = repository.getRandomQuestions(10)
            questionsFlow.collect { loadedQuestions ->
                _questions.value = loadedQuestions
            }
        }
    }

    fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        _selectedAnswers.value = _selectedAnswers.value.toMutableMap().apply {
            this[questionIndex] = optionIndex
        }
    }

    fun nextQuestion() {
        if (_currentIndex.value < _questions.value.size - 1) {
            _currentIndex.value += 1
        }
    }

    fun previousQuestion() {
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
        }
    }

    fun calculateScore() {
        val total = _questions.value.size
        var correct = 0
        _questions.value.forEachIndexed { index, question ->
            val selected = _selectedAnswers.value[index]
            val correctIndex = "ABCD".indexOf(question.correctAnswer)
            if (selected == correctIndex) {
                correct++
            }
        }
        _score.value = (correct * 100) / total
        viewModelScope.launch {
            repository.insertSession(PracticeSession(score = _score.value, correctCount = correct, totalQuestions = total))
        }
    }

    fun resetForNewSession() {
        _questions.value = emptyList()
        _currentIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _score.value = 0
        _selectedCategories.value = emptyList()
    }
}