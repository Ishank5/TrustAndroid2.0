package com.example.trustandroid20

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

suspend fun loadImage(context: Context, url: String): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()
                .get()
            bitmap.asImageBitmap()
        } catch (e: Exception) {
            Log.e("ImageLoad", "Error loading image", e)
            null
        }
    }
}

fun loadImageSync(context: Context, url: String): Bitmap? {
    return try {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .submit()
            .get()
    } catch (e: Exception) {
        Log.e("ImageLoad", "Error loading image", e)
        null
    } as Bitmap?
}


fun getAppIconUrl(packageName: String): String {
    return "https://logo.clearbit.com/$packageName.com" // Adjust the domain as necessary
}

@Composable
fun ShowAllBannedAppsScreen(bannedApps: List<Pair<String, String>>) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(top = 32.dp)
        .background(Color(0xFF2C3E50)) // Dark military green-blue background
    ) {
        Text(
            text = "All Banned Apps",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFFFDFEFE) // Light grey-white color
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        if (bannedApps.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "No banned apps found.", color = Color(0xFFFDFEFE)) // Light grey-white color
            }
        } else {
            LazyColumn {
                items(bannedApps) { app ->
                    BannedAppDisplayItem(app.first, app.second)
                }
            }
        }
    }
}

@Composable
fun BannedAppDisplayItem(packageName: String, appName: String) {
    val context = LocalContext.current

    val appIconUrl = getAppIconUrl(packageName)
    val appIcon = remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(appIconUrl) {
        val bitmap = loadImage(context, appIconUrl)
        appIcon.value = bitmap
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors( // Use colors parameter
            containerColor = Color(0xFF34495E) // Dark blue-grey
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            appIcon.value?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = appName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFFDFEFE)) // Light grey-white color
                Text(text = "Package: $packageName", fontSize = 14.sp, color = Color(0xFFFDFEFE)) // Light grey-white color
            }
        }
    }
}

fun fetchAllBannedAppsFromFirestore(onComplete: (List<Pair<String, String>>) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val collectionRef = firestore.collection("bannedApps")

    collectionRef.get().addOnSuccessListener { querySnapshot ->
        val bannedApps = querySnapshot.documents.mapNotNull {
            val packageName = it.getString("packageName")
            val appName = it.getString("appName")
            if (packageName != null && appName != null) {
                packageName to appName
            } else {
                null
            }
        }
        onComplete(bannedApps)
    }.addOnFailureListener { exception ->
        Log.e("Firestore", "Error fetching banned apps", exception)
        onComplete(emptyList())
    }
}


fun handleFeature3(context: Context, userName: String) {
    checkForBannedApps(context, userName) { bannedApps ->
        if (bannedApps.isNotEmpty()) {
            generateAndSharePdf(context, userName, bannedApps)
        } else {
            Toast.makeText(context, "No banned apps found.", Toast.LENGTH_LONG).show()
        }
    }
}

fun generateAndSharePdf(context: Context, userName: String, bannedApps: List<PackageInfo>) {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)

    val canvas = page.canvas
    val paint = android.graphics.Paint()

    // Title
    paint.textSize = 18f
    paint.isFakeBoldText = true
    paint.textAlign = android.graphics.Paint.Align.CENTER
    canvas.drawText("Banned Apps Report", (pageInfo.pageWidth / 2).toFloat(), 40f, paint)

    // Content
    paint.textSize = 12f
    paint.isFakeBoldText = false
    paint.textAlign = android.graphics.Paint.Align.LEFT

    var yPosition = 70f
    val xStartPosition = 40f

    bannedApps.forEachIndexed { index, app ->
        val appName = app.applicationInfo.loadLabel(context.packageManager).toString()
        val appPackage = app.packageName
        val appVersion = app.versionName
        val appInstallDate = DateFormat.format("yyyy-MM-dd", app.firstInstallTime).toString()

        canvas.drawText("${index + 1}. App Name: $appName", xStartPosition, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Package: $appPackage", xStartPosition, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Version: $appVersion", xStartPosition, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Installed: $appInstallDate", xStartPosition, yPosition, paint)
        yPosition += 30f
    }

    document.finishPage(page)

    // Save PDF to a temp file
    val fileName = "banned_apps_report_$userName.pdf"
    val tempFile = File(context.cacheDir, fileName)

    try {
        document.writeTo(FileOutputStream(tempFile))
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
        sharePdfIntent(context, uri)
    } catch (e: IOException) {
        Log.e("PDF", "Error writing PDF file", e)
        Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_LONG).show()
    } finally {
        document.close()
    }
}
fun sharePdfIntent(context: Context, uri: Uri) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share PDF via"))
}


fun handleFeature2(context: Context, userName: String) {
    checkForBannedApps(context, userName) { bannedApps ->
        generatePdf(context, userName, bannedApps)
    }
}

fun generatePdf(context: Context, userName: String, bannedApps: List<PackageInfo>) {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = document.startPage(pageInfo)

    val canvas = page.canvas
    val paint = android.graphics.Paint()

    // Set up paint for title
    paint.textSize = 18f
    paint.isFakeBoldText = true
    paint.textAlign = android.graphics.Paint.Align.CENTER
    canvas.drawText("Banned Apps Report", (pageInfo.pageWidth / 2).toFloat(), 40f, paint)

    // Set up paint for content
    paint.textSize = 12f
    paint.isFakeBoldText = false
    paint.textAlign = android.graphics.Paint.Align.LEFT

    var yPosition = 70f
    val xStartPosition = 40f

    bannedApps.forEachIndexed { index, app ->
        val appName = app.applicationInfo.loadLabel(context.packageManager).toString()
        val appPackage = app.packageName
        val appVersion = app.versionName
        val appInstallDate = DateFormat.format("yyyy-MM-dd", app.firstInstallTime).toString()

        // Fetch app icon bitmap
        val appIconBitmap = loadImageSync(context, getAppIconUrl(appPackage))

        // Draw app icon if available
        appIconBitmap?.let {
            canvas.drawBitmap(it, xStartPosition, yPosition, paint)
        }

        canvas.drawText("${index + 1}. App Name: $appName", xStartPosition + 60, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Package: $appPackage", xStartPosition + 60, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Version: $appVersion", xStartPosition + 60, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Installed: $appInstallDate", xStartPosition + 60, yPosition, paint)
        yPosition += 30f
    }

    document.finishPage(page)

    val directoryPath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
    val filePath = "$directoryPath/banned_apps_report_$userName.pdf"
    val file = File(filePath)
    try {
        document.writeTo(FileOutputStream(file))
        Toast.makeText(context, "PDF generated at $filePath", Toast.LENGTH_LONG).show()
    } catch (e: IOException) {
        Log.e("PDF", "Error writing PDF file", e)
        Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_LONG).show()
    } finally {
        document.close()
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userName: String, onUserNameChange: (String) -> Unit, onScanButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF2C3E50)), // Dark military green-blue background
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to TrustAndroid",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFDFEFE), // Light grey-white color
                fontSize = 36.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

//        OutlinedTextField(
//            value = userName,
//            onValueChange = onUserNameChange,
//            label = { Text("Enter your name", color = Color(0xFFFDFEFE)) },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = Color(0xFFF39C12),
//                unfocusedBorderColor = Color(0xFFFDFEFE),
//                cursorColor = Color(0xFFFDFEFE),
//
//                focusedLabelColor = Color(0xFFF39C12),
//                unfocusedLabelColor = Color(0xFFFDFEFE)
//            ),
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 24.dp)
//                .padding(bottom = 16.dp)
//        )


        Button(
            onClick = onScanButtonClick,
            colors = ButtonDefaults.buttonColors(Color(0xFF28B463)), // Military green
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp)
        ) {
            Text(
                text = "Start Scanning",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color(0xFFFDFEFE) // Light grey-white color
            )
        }

        Text(
            text = "Your security is our priority.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFFFDFEFE), // Light grey-white color
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun BannedAppItem(app: PackageInfo, onDeleteClick: (String) -> Unit) {
    val context = LocalContext.current
    val appName = app.applicationInfo.loadLabel(context.packageManager).toString()

    val appIconUrl = getAppIconUrl(app.packageName)
    val appIcon = remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(appIconUrl) {
        val bitmap = loadImage(context, appIconUrl)
        appIcon.value = bitmap
    }

    val appVersion = app.versionName
    val appPackage = app.packageName
    val appInstallDate = DateFormat.format("yyyy-MM-dd", app.firstInstallTime).toString()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors( // Use colors parameter
            containerColor = Color(0xFF34495E) // Dark blue-grey
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        )
    {
            appIcon.value?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = appName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFFDFEFE)) // Light grey-white color
                Text(text = "Package: $appPackage", fontSize = 14.sp, color = Color(0xFFFDFEFE)) // Light grey-white color
                Text(text = "Version: $appVersion", fontSize = 14.sp, color = Color(0xFFFDFEFE)) // Light grey-white color
                Text(text = "Installed: $appInstallDate", fontSize = 14.sp, color = Color(0xFFFDFEFE)) // Light grey-white color
            }

            IconButton(onClick = { onDeleteClick(appPackage) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete App", tint = Color(0xFFE74C3C)) // Military red
            }
        }
    }
}

@Composable
fun BannedAppsList(bannedApps: List<PackageInfo>, onDeleteClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 32.dp)) {
        Text(
            text = "Banned Apps Found",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF34495E) // Light grey-white color
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        if (bannedApps.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "No banned apps found.", color = Color(0xFFFDFEFE)) // Light grey-white color
            }
        } else {
            LazyColumn {
                items(bannedApps) { app ->
                    BannedAppItem(app) { packageName ->
                        onDeleteClick(packageName)
                    }
                }
            }
        }
    }
}
fun deleteApp(context: Context, packageName: String) {
    val intent = Intent(Intent.ACTION_DELETE).apply {
        data = Uri.parse("package:$packageName")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

fun checkInstalledApps(context: Context, bannedPackages: List<String>): List<PackageInfo> {
    val packageManager = context.packageManager
    return packageManager.getInstalledPackages(PackageManager.GET_META_DATA).filter { packageInfo ->
        bannedPackages.contains(packageInfo.packageName)
    }
}

fun fetchBannedAppsFromFirestore(onComplete: (List<String>) -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    val collectionRef = firestore.collection("bannedApps")

    collectionRef.get().addOnSuccessListener { querySnapshot ->
        val bannedPackages = querySnapshot.documents.mapNotNull { it.getString("packageName") }
        onComplete(bannedPackages)
    }.addOnFailureListener { exception ->
        Log.e("Firestore", "Error fetching banned apps", exception)
        onComplete(emptyList())
    }
}

fun checkForBannedApps(context: Context, userName: String, onResult: (List<PackageInfo>) -> Unit) {
    fetchBannedAppsFromFirestore { bannedPackages ->
        val bannedApps = checkInstalledApps(context, bannedPackages)
        uploadBannedAppsToFirestore(context, userName, bannedApps)
        onResult(bannedApps)
    }
}

fun getDeviceId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

fun uploadBannedAppsToFirestore(context: Context, userName: String, bannedApps: List<PackageInfo>) {
    val firestore = FirebaseFirestore.getInstance()
    val deviceId = getDeviceId(context)
    val deviceCollection = firestore.collection("devices").document(deviceId)

    deviceCollection.set(mapOf("userName" to userName))

    val bannedAppsCollection = deviceCollection.collection("bannedApps")
    bannedApps.forEach { app ->
        val appName = app.applicationInfo.loadLabel(context.packageManager).toString()
        val appVersion = app.versionName
        val appPackage = app.packageName
        val appInstallDate = DateFormat.format("yyyy-MM-dd", app.firstInstallTime).toString()

        val appData = mapOf(
            "appName" to appName,
            "appVersion" to appVersion,
            "appPackage" to appPackage,
            "appInstallDate" to appInstallDate
        )
        bannedAppsCollection.document(appPackage).set(appData)
    }

    // Initialize GoogleSheetsHelper with your Web App URL
    val webAppUrl = "https://script.google.com/macros/s/AKfycbzEtRG0qIIjGbemCt35pQe5csSfloS2ELTmQbXM1dJbBL06SxSqNEbcJj5EzCk_sBhoSA/exec" // Replace with your actual Web App URL
    val googleSheetsHelper = GoogleSheetsHelper(webAppUrl)

    // Launch a coroutine to send data to Google Sheets
    CoroutineScope(Dispatchers.Main).launch {
        bannedApps.forEach { app ->
            val appName = app.applicationInfo.loadLabel(context.packageManager).toString()
            val appVersion = app.versionName
            val appPackage = app.packageName
            val appInstallDate = DateFormat.format("yyyy-MM-dd", app.firstInstallTime).toString()
            val success = googleSheetsHelper.sendDataToSheet(userName, appName, appPackage, appVersion, appInstallDate)
            if (success) {
                Log.d("UploadToSheets", "Successfully uploaded $appName to Google Sheets")
            } else {
                Log.e("UploadToSheets", "Failed to upload $appName to Google Sheets")
            }
        }
    }
}

