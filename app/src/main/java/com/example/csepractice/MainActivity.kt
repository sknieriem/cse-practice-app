package com.example.csepractice

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csepractice.ui.theme.CSEPracticeAppTheme
import com.example.csepractice.viewmodel.PracticeViewModel
import com.example.csepractice.viewmodel.Session
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Paint
import android.graphics.Color as AndroidColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CSEPracticeAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PracticeScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PracticeScreen(modifier: Modifier = Modifier, viewModel: PracticeViewModel = viewModel()) {
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
    val score by viewModel.score.collectAsState()
    val sessions by viewModel.sessions.collectAsState(emptyList<Session>())
    val context = LocalContext.current // For Toast debug
    val visible = remember { mutableStateOf(false) }
    val showChart = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        visible.value = true
    }

    AnimatedVisibility(visible = visible.value, enter = fadeIn()) {
        if (questions.isEmpty()) {
            Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Text("Loading questions...", modifier = Modifier.padding(top = 16.dp))
            }
        } else if (score > 0) {
            val scrollState = rememberScrollState()
            Column(
                modifier = modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Your score: $score%", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.resetForNewSession() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("Start New Practice")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("Progress History:", style = MaterialTheme.typography.titleMedium)
                // Nicer table display
                Column {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("Date", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                        Text("Score", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    }
                    sessions.forEach { session ->
                        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(session.date))
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(formattedDate, modifier = Modifier.weight(2f))
                            Text("${session.score}%", modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showChart.value = !showChart.value },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (showChart.value) "Hide Chart" else "View Chart")
                }
                if (showChart.value && sessions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SimpleLineChart(sessions = sessions)
                }
            }
        } else {
            val currentQuestion = questions[currentIndex]
            Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(currentQuestion.text, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        currentQuestion.options.forEachIndexed { optIndex, option ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedAnswers[currentIndex] == optIndex,
                                    onClick = { viewModel.selectAnswer(currentIndex, optIndex) }
                                )
                                Text(option)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = { viewModel.previousQuestion() }, enabled = currentIndex > 0) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Previous")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { viewModel.nextQuestion() }, enabled = currentIndex < questions.size - 1) {
                        Text("Next")
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (currentIndex == questions.size - 1) {
                    val isLastAnswered = selectedAnswers.containsKey(currentIndex)
                    Button(
                        onClick = {
                            Toast.makeText(context, "Submit pressed!", Toast.LENGTH_SHORT).show() // Debug Toast
                            viewModel.calculateScore()
                        },
                        enabled = isLastAnswered,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Submit")
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleLineChart(sessions: List<Session>, modifier: Modifier = Modifier) {
    if (sessions.isEmpty()) return

    // Sort sessions by date just in case
    val sortedSessions = sessions.sortedBy { it.date }

    val minScore = 0f
    val maxScore = 100f
    val minDate = sortedSessions.first().date
    val maxDate = sortedSessions.last().date
    val dateRange = if (maxDate > minDate) maxDate - minDate else 1L

    Card(
        modifier = modifier.fillMaxWidth().height(300.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Score Progress Chart", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val padding = 40f // For axes and labels

                // Draw x-axis (dates)
                drawLine(
                    color = Color.Gray,
                    start = Offset(padding, height - padding),
                    end = Offset(width - padding / 2, height - padding),
                    strokeWidth = 2f
                )

                // Draw y-axis (scores)
                drawLine(
                    color = Color.Gray,
                    start = Offset(padding, height - padding),
                    end = Offset(padding, padding / 2),
                    strokeWidth = 2f
                )

                // Y-axis labels
                val ySteps = 5
                for (i in 0..ySteps) {
                    val y = height - padding - (height - padding * 2) * (i.toFloat() / ySteps)
                    val scoreLabel = (minScore + (maxScore - minScore) * (i.toFloat() / ySteps)).toInt().toString()
                    drawText(scoreLabel, padding - 30f, y + 5f) // Adjusted x for better positioning
                }

                // X-axis labels (simplified: show first, middle, last)
                val xSteps = sortedSessions.size.coerceAtMost(5).coerceAtLeast(1)
                for (i in 0 until xSteps) {
                    val index = if (xSteps > 1) (sortedSessions.size - 1) * i / (xSteps - 1) else 0
                    val x = padding + (width - padding * 2) * (i.toFloat() / (xSteps - 1).coerceAtLeast(1))
                    val date = Date(sortedSessions[index].date)
                    val formattedDate = SimpleDateFormat("MM-dd", Locale.getDefault()).format(date)
                    drawText(formattedDate, x - 20f, height - padding + 30f)
                }

                // Draw line
                val path = Path()
                sortedSessions.forEachIndexed { index, session ->
                    val x = padding + (width - padding * 2) * ((session.date - minDate).toFloat() / dateRange)
                    val y = height - padding - (height - padding * 2) * ((session.score.toFloat() - minScore) / (maxScore - minScore))
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                    // Draw points
                    drawCircle(Color.Blue, radius = 6f, center = Offset(x, y))
                }
                drawPath(path, Color.Blue, style = Stroke(width = 4f))
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawText(text: String, x: Float, y: Float) {
    val paint = Paint().apply {
        color = AndroidColor.BLACK
        textSize = 24f
        isAntiAlias = true
    }
    drawIntoCanvas {
        it.nativeCanvas.drawText(text, x, y, paint)
    }
}