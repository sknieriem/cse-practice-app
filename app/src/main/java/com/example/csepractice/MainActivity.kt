//package com.example.csepractice
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.RadioButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.automirrored.filled.ArrowForward
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.Refresh
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.csepractice.ui.theme.CSEPracticeAppTheme
//import com.example.csepractice.viewmodel.PracticeViewModel
//import kotlinx.coroutines.delay
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.material3.Text
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.ui.graphics.Color
//import android.content.Intent
//import androidx.compose.runtime.remember
//import androidx.compose.ui.unit.sp
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            CSEPracticeAppTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    PracticeScreen(Modifier.padding(innerPadding))
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun PracticeScreen(modifier: Modifier = Modifier, viewModel: PracticeViewModel = viewModel()) {
//    val questions by viewModel.questions.collectAsState()
//    val currentIndex by viewModel.currentIndex.collectAsState()
//    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
//    val score by viewModel.score.collectAsState()
//    val sessions by viewModel.sessions.collectAsState(emptyList())
//    val context = LocalContext.current  // For Toast debug
//
//    val visible = remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        delay(300)
//        visible.value = true
//    }
//
//    AnimatedVisibility(visible = visible.value, enter = fadeIn()) {
//        if (questions.isEmpty()) {
//            Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
//                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
//                Text("Loading questions...", modifier = Modifier.padding(top = 16.dp))
//            }
//        } else if (score > 0) {
//            val scrollState = rememberScrollState()
//            Column(
//                modifier = modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
//                verticalArrangement = Arrangement.Center
//            ) {
//                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
//                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text("Your score: $score%", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Button(onClick = { viewModel.resetForNewSession() }) {
//                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
//                            Text("Start New Practice")
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(32.dp))
//                Text("Progress History:", style = MaterialTheme.typography.titleMedium)
//                // Nicer table display
//                Column {
//                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
//                        Text("Date", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
//                        Text("Score", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
//                    }
//                    sessions.forEach { session ->
//                        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(session.date))
//                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
//                            Text(formattedDate, modifier = Modifier.weight(2f))
//                            Text("${session.score}%", modifier = Modifier.weight(1f))
//                        }
//                    }
//                }
//            }
//        } else {
//            val currentQuestion = questions[currentIndex]
//            Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
//                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        Text(currentQuestion.text, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
//                        currentQuestion.options.forEachIndexed { optIndex, option ->
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                RadioButton(
//                                    selected = selectedAnswers[currentIndex] == optIndex,
//                                    onClick = { viewModel.selectAnswer(currentIndex, optIndex) }
//                                )
//                                Text(option)
//                            }
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(32.dp))
//                Row(
//                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    Button(onClick = { viewModel.previousQuestion() }, enabled = currentIndex > 0) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
//                        Text("Previous")
//                    }
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Button(onClick = { viewModel.nextQuestion() }, enabled = currentIndex < questions.size - 1) {
//                        Text("Next")
//                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
//                    }
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//                if (currentIndex == questions.size - 1) {
//                    val isLastAnswered = selectedAnswers.containsKey(currentIndex)
//                    Button(
//                        onClick = {
//                            Toast.makeText(context, "Submit pressed!", Toast.LENGTH_SHORT).show()  // Debug Toast
//                            viewModel.calculateScore()
//                        },
//                        enabled = isLastAnswered,
//                        modifier = Modifier.align(Alignment.CenterHorizontally)
//                    ) {
//                        Text("Submit")
//                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
//                    }
//                }
//            }
//        }
//    }
//}

package com.example.csepractice

import android.content.Context // Add this import
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csepractice.ui.theme.CSEPracticeAppTheme
import com.example.csepractice.viewmodel.PracticeViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.wrapContentHeight
import android.content.Intent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)  // Default to light mode
        setContent {
            CSEPracticeAppTheme(darkTheme = isDarkMode) {
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
    val sessions by viewModel.sessions.collectAsState(emptyList())
    val context = LocalContext.current  // For Toast debug

    val visible = remember { mutableStateOf(false) }

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
                verticalArrangement = Arrangement.Center
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
                // Add Average Score
                Spacer(modifier = Modifier.height(16.dp))
                val averageScore = if (sessions.isNotEmpty()) {
                    sessions.map { it.score }.average().toInt()
                } else {
                    0
                }
                Text(
                    text = "Average Score: $averageScore%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                )
                // Add Chart Button
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val intent = Intent(context, ChartActivity::class.java)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text("View Progress Chart", color = Color.White)
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
                            Toast.makeText(context, "Submit pressed!", Toast.LENGTH_SHORT).show()  // Debug Toast
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