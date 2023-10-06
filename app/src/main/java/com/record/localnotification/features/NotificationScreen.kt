package com.record.localnotification.features

import android.Manifest
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.record.localnotification.features.NotificationInstance.showNotification
import java.time.LocalDateTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationViewScreen() {

    // current date and time
    val calendar = Calendar.getInstance()
    val current = LocalDateTime.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.SECOND)
    )

    val context = LocalContext.current
    // get notification permission
    var hasNotificationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var notificationMessage by remember { mutableStateOf("") }
        TextField(
            value = notificationMessage,
            onValueChange = { notificationMessage = it },
            placeholder = { Text(text = "Enter the notification message") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        )
        Button(
            onClick = {
                // check the notification permission
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                if (hasNotificationPermission && notificationMessage.isNotEmpty()) {
                    showNotification(context = context, description = notificationMessage)
                }
            }
        ) {
            Text(text = "Notification")
        }

        var currentMinutes = 43
        LaunchedEffect(key1 = currentMinutes, key2 = current.minute, block = {
            if (currentMinutes == current.minute){
                Toast.makeText(context, "Show the notification", Toast.LENGTH_SHORT).show()
                showNotification(context = context, description = notificationMessage)
                currentMinutes = 0
            }
        })

        TimerApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerApp() {
    // Create mutable state for user input hours and minutes
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }

    // Calculate the total remaining time in seconds
    var remainingTime by remember { mutableStateOf((hours * 3600) + (minutes * 60)) }

    val countDownTimer = rememberUpdatedState(
        object : CountDownTimer((remainingTime * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                // Handle timer completion here
                remainingTime = 0
            }
        }
    )

    DisposableEffect(key1 = "key") {
        countDownTimer.value.start()
        onDispose {
            countDownTimer.value.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Input fields for hours and minutes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = hours.toString(),
                onValueChange = { newValue ->
                    hours = newValue.toIntOrNull() ?: 0
                },
                label = { Text("Hours") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            TextField(
                value = minutes.toString(),
                onValueChange = { newValue ->
                    minutes = newValue.toIntOrNull() ?: 0
                },
                label = { Text("Minutes") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }

        Text(
            text = if (remainingTime == 0) {
                "Time is complete"
            } else {
                "Remaining Time: ${formatTime(remainingTime)}"
            },
            fontSize = 24.sp
        )
    }
}

// Helper function to format time in seconds as "hh:mm:ss"
fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}