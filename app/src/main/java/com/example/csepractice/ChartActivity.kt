package com.example.csepractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.csepractice.ui.theme.CSEPracticeAppTheme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.example.csepractice.data.PracticeSession
import com.example.csepractice.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import android.content.Context
import android.graphics.Color

class ChartActivity : ComponentActivity() {
    private val repository by lazy { QuestionRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        setContent {
            CSEPracticeAppTheme(darkTheme = isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScoreChartScreen(modifier = Modifier.padding(innerPadding), sessions = repository.getAllSessions())
                }
            }
        }
    }
}

@Composable
fun ScoreChartScreen(modifier: Modifier = Modifier, sessions: Flow<List<PracticeSession>>) {
    val sessionList by sessions.collectAsState(emptyList())
    val context = LocalContext.current

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            LineChart(ctx).apply {
                description.text = "Scores Over Time"
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
            }
        },
        update = { chart ->
            val entries = sessionList.mapIndexed { index, session ->
                Entry(index.toFloat(), session.score.toFloat())
            }
            val dataSet = LineDataSet(entries, "Scores")
            dataSet.color = Color.BLUE
            dataSet.valueTextColor = Color.BLACK
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}