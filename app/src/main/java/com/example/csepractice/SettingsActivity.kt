package com.example.csepractice

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.csepractice.repository.QuestionRepository
import com.example.csepractice.ui.theme.CSEPracticeAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast  // For feedback
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        setContent {
            CSEPracticeAppTheme(darkTheme = isDarkMode) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var isDarkMode by remember { mutableStateOf(prefs.getBoolean("dark_mode", false)) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dark Mode", modifier = Modifier.padding(end = 16.dp))
            Switch(
                checked = isDarkMode,
                onCheckedChange = { enabled ->
                    isDarkMode = enabled
                    with(prefs.edit()) {
                        putBoolean("dark_mode", enabled)
                        apply()
                    }
                    // Note: Restart the app to see changes (or recreate activities)
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            val repository = QuestionRepository(context)
            CoroutineScope(Dispatchers.IO).launch {
                repository.clearAllSessions()
            }
            Toast.makeText(context, "History reset!", Toast.LENGTH_SHORT).show()
        }) {
            Text("Reset Progress History")
        }
    }
}