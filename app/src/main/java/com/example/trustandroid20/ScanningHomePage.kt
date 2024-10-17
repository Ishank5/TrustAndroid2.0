package com.example.trustandroid20

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
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
    }
}


fun getAppIconUrl(packageName: String): String {
    return "https://logo.clearbit.com/$packageName.com" // Adjust the domain as necessary
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
    canvas.drawText("Banned Apps Report $userName", (pageInfo.pageWidth / 2).toFloat(), 40f, paint)

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
    canvas.drawText("Banned Apps Report $userName", (pageInfo.pageWidth / 2).toFloat(), 40f, paint)
    canvas.drawText("$userName", (pageInfo.pageWidth / 2).toFloat(), 40f, paint)

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

//fun getDeviceId(context: Context): String {
//    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//}

fun uploadBannedAppsToFirestore(context: Context, userName: String, bannedApps: List<PackageInfo>) {
    val firestore = FirebaseFirestore.getInstance()
    //val deviceId = getDeviceId(context)
    val deviceCollection = firestore.collection("devices").document(userName)

    //deviceCollection.set(mapOf("userName" to userName))

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


}

