package com.record.localnotification.features

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.record.localnotification.features.NotificationInstance.showNotification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationViewScreen() {
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
        )
        Button(
            onClick = {
                // check the notification permission
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                // if the application has permission the show notification
                if (hasNotificationPermission && notificationMessage.isNotEmpty()) {
                    showNotification(
                        context = context,
                        description = notificationMessage
                    )
                }
            }
        ) {
            Text(text = "Notification")
        }
    }
}