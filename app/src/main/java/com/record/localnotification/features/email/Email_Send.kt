package com.record.localnotification.features.email

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailSendViewScreen() {

    val senderEmail = remember { mutableStateOf(TextFieldValue()) }
    val emailSubject = remember { mutableStateOf("Donation completed.") }
    val emailBody = remember { mutableStateOf(TextFieldValue()) }

    val context = LocalContext.current

    // List to store selected image URIs and their descriptions
    var selectedImages by remember { mutableStateOf(listOf<Pair<Uri, String>>()) }

    // Function to create the email body combining text and image descriptions
    fun createEmailBody(): String {
        val textPart = emailBody.value.text
        val imageDescriptions = selectedImages.joinToString("\n") { it.second }
        return "$textPart\n\n$imageDescriptions"
    }

    Column(
        modifier = Modifier.fillMaxSize().fillMaxHeight().fillMaxWidth().padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = senderEmail.value,
            onValueChange = { senderEmail.value = it },
            label = { Text(text = "Email address") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = emailSubject.value,
            onValueChange = { emailSubject.value = it },
            label = { Text(text = "Subject") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = emailBody.value,
            onValueChange = { emailBody.value = it },
            label = { Text(text = "Descriptions") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            maxLines = Int.MAX_VALUE
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Pick image with description
        val galleryLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
                selectedImages = it.map { uri -> uri to "" }.toMutableList()
            }

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(10.dp)
            ) {
                Text(text = "Pick Image")
            }
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(selectedImages) { (uri) ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                            .height(100.dp).fillMaxWidth()
                            .clickable { /*selectedImages = selectedImages.filter { it.first != uri }*/ }
                    )
                }
            }
        }

        Button(
            onClick = {
                val intentEmail = Intent(Intent.ACTION_SEND_MULTIPLE)
                val emailAddress = arrayOf(senderEmail.value.text)// add the multipart email
                intentEmail.putExtra(Intent.EXTRA_EMAIL, emailAddress) // email
                intentEmail.putExtra(Intent.EXTRA_SUBJECT, emailSubject.value) // subject
                intentEmail.putExtra(Intent.EXTRA_TEXT, createEmailBody()) // descriptions
                intentEmail.type = "message/rfc822"

                // Add image attachments to the email
                val imageUris = selectedImages.map { it.first }
                intentEmail.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(imageUris))
                context.startActivity(Intent.createChooser(intentEmail, "Choose an Email client: "))
            }
        ) {
            Text(
                text = "Send Email",
                modifier = Modifier.padding(10.dp),
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}

// image from gallery
//@Composable
//fun AppContent() {
//
//    var selectImages by remember { mutableStateOf(listOf<Uri>()) }
//
//    val galleryLauncher =
//        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
//            selectImages = it
//        }
//
//    Column(
//        Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Button(
//            onClick = { galleryLauncher.launch("image/*") },
//            modifier = Modifier
//                .wrapContentSize()
//                .padding(10.dp)
//        ) {
//            Text(text = "Pick Image From Gallery")
//        }
//
//        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
//            items(selectImages) { uri ->
//                Image(
//                    painter = rememberAsyncImagePainter(uri),
//                    contentScale = ContentScale.FillWidth,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .padding(16.dp, 8.dp)
//                        .size(100.dp)
//                        .clickable {
//
//                        }
//                )
//            }
//        }
//    }
//}


//        val bitmap = remember { mutableStateOf<Bitmap?>(null) } // Change the type to Bitmap?
//        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
//            bitmap.value = it // Remove the cast to Nothing?
//        }
//        Column(
//            modifier = Modifier.padding(16.dp),
//            content = {
//                Button(
//                    onClick = {
//                        launcher.launch()
//                    },
//                    content = {
//                        Text(text = "Capture Image From Camera")
//                    }
//                )
//                Spacer(modifier = Modifier.padding(16.dp))
//                bitmap.value?.let { data ->
//                    Image(
//                        bitmap = data,
//                        contentDescription = null,
//                        modifier = Modifier.size(400.dp)
//                    )
//                }
//            }
//        )


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EmailSendViewScreen() {
//    val senderEmail = remember { mutableStateOf(TextFieldValue()) }
//    val emailSubject = remember { mutableStateOf("Donation completed.") }
//    val emailBody = remember { mutableStateOf(TextFieldValue()) }
//
//    val ctx = LocalContext.current
//
//    // List to store selected image URIs and their descriptions
//    var selectedImages by remember { mutableStateOf(listOf<Pair<Uri, String>>()) }
//
//    // Function to create the email body combining text and image descriptions
//    fun createEmailBody(): String {
//        val textPart = emailBody.value.text
//        val imageDescriptions = selectedImages.joinToString("\n") { it.second }
//        return "$textPart\n\n$imageDescriptions"
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .fillMaxHeight()
//            .fillMaxWidth()
//            .padding(5.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        TextField(
//            value = senderEmail.value,
//            onValueChange = { senderEmail.value = it },
//            label = { Text(text = "Email address") },
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
//            singleLine = true,
//        )
//        Spacer(modifier = Modifier.height(10.dp))
//        TextField(
//            value = emailSubject.value,
//            onValueChange = { emailSubject.value = it },
//            label = { Text(text = "Subject") },
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
//            singleLine = true,
//        )
//        Spacer(modifier = Modifier.height(10.dp))
//        TextField(
//            value = emailBody.value,
//            onValueChange = { emailBody.value = it },
//            label = { Text(text = "Descriptions") },
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
//            maxLines = Int.MAX_VALUE
//        )
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // Pick image with description
//        val galleryLauncher =
//            rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
//                selectedImages = it.map { uri -> uri to "" }.toMutableList()
//            }
//
//        Column(
//            Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Button(
//                onClick = { galleryLauncher.launch("image/*") },
//                modifier = Modifier
//                    .wrapContentSize()
//                    .padding(10.dp)
//            ) {
//                Text(text = "Pick Image")
//            }
//            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
//                items(selectedImages) { (uri) ->
//                    Image(
//                        painter = rememberAsyncImagePainter(uri),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .padding(16.dp, 8.dp)
//                            .size(100.dp)
//                            .clickable {
//                                selectedImages = selectedImages.filter { it.first != uri }
//                            }
//                    )
//                }
//            }
//        }
//
//        Button(
//            onClick = {
//                val intentEmail = Intent(Intent.ACTION_SEND_MULTIPLE)
//                val emailAddress = arrayOf(senderEmail.value.text)
//                intentEmail.putExtra(Intent.EXTRA_EMAIL, emailAddress)
//                intentEmail.putExtra(Intent.EXTRA_SUBJECT, emailSubject.value)
//                intentEmail.putExtra(Intent.EXTRA_TEXT, createEmailBody())
//                intentEmail.type = "message/rfc822"
//
//                // Add image attachments to the email
//                val imageUris = selectedImages.map { it.first }
//                intentEmail.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(imageUris))
//                ctx.startActivity(Intent.createChooser(intentEmail, "Choose an Email client: "))
//            }
//        ) {
//            Text(
//                text = "Send Email",
//                modifier = Modifier.padding(10.dp),
//                color = Color.White,
//                fontSize = 15.sp
//            )
//        }
//    }
//}