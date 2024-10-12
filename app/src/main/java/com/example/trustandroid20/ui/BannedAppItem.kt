package com.example.trustandroid20.ui

import android.content.pm.PackageInfo
import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustandroid20.getAppIconUrl
import com.example.trustandroid20.loadImage


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
