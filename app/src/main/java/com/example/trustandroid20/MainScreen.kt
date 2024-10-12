package com.example.trustandroid20


import android.content.pm.PackageInfo

import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun HomeScreenUI(onFeatureClick: (String) -> Unit, bannedApps: List<PackageInfo>, onContactClick: () -> Unit, onHowToUseClick: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E3B55),  // Dark Blue-Grey
                        Color(0xFF1C2833),  // Darker Blue-Grey
                        Color(0xFF0B1A30)   // Very Dark Blue-Grey
                    )
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "Welcome to TrustAndroid",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = Color(0xFFD1E8E2) // Light Grey
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .padding(16.dp)
        )

        var userName by remember { mutableStateOf("") }
        val scannerIcon = ImageVector.vectorResource(id = R.drawable.scanner_foreground)
        val downloadIcon = ImageVector.vectorResource(id = R.drawable.ic_download_foreground)
        val shareIcon = ImageVector.vectorResource(id = R.drawable.share_foreground)
        val showIcon = ImageVector.vectorResource(id = R.drawable.show_foreground)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureBox(
                    featureName = "Scan Now", icon = scannerIcon,
                    onClick = { onFeatureClick("Scanning") },
                    modifier = Modifier.weight(1f).padding(8.dp)
                )
                FeatureBox(
                    featureName = "Export Now", icon =downloadIcon,
                    onClick = { handleFeature2(context, userName) },
                    modifier = Modifier.weight(1f).padding(8.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureBox(
                    featureName = "Share Now", icon = shareIcon,
                    onClick = { handleFeature3(context, userName) },
                    modifier = Modifier.weight(1f).padding(8.dp)
                )
                FeatureBox(
                    featureName = "Banned Apps", icon = showIcon,
                    onClick = { onFeatureClick("Feature 4") },
                    modifier = Modifier.weight(1f).padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "We together will fight, as we are Kharga Corps",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFFD1E8E2), // Light Grey
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Contact Developers",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF728C8D), // Army Green
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.Underline
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { onContactClick() }
                    .padding(top = 16.dp)
            )

            Text(
                text = "How to use the app?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF728C8D), // Army Green
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.Underline
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { onHowToUseClick()}
                    .padding(top = 16.dp)
            )
        }
    }
}
@Composable
fun FeatureBox(featureName: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(2.dp, Color(0xFF728C8D)) // Army Green
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color(0xFF1C2833)) // Dark Grey
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = featureName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD1E8E2), // Light Grey
                        fontSize = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFD1E8E2), // Light Grey
                    modifier = Modifier.size(144.dp)
                )
            }
        }
    }
}
@Composable
fun DeveloperDetailsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF0B1A30)), // Very Dark Blue-Grey
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Developer 1:Ansh Bajaj \nEmail: abajaj1_be23@thapar.edu \nContact: 8685988991",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFFD1E8E2), // Light Grey
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Developer 2:Ishank Goyal \nEmail: igoyal_be23@thapar.edu \nContact: 8708667212",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFFD1E8E2), // Light Grey
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Composable
fun HowToUseScreen() {
    AndroidView(
        factory = {
            WebView(it).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("WebView", "Page loaded: $url")
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        Log.e("WebView", "Error loading page: ${error?.description}")
                    }
                }
                loadData(
                    """
                    <html>
                    <body>
                    <iframe width="100%" height="100%" src="https://youtu.be/OAnfbJ2ij8I?si=tQLumr8y-rwClbkW" frameborder="0" allowfullscreen></iframe>
                    </body>
                    </html>
                    """.trimIndent(),
                    "text/html",
                    "utf-8"
                )
            }
        },
        modifier = Modifier.fillMaxSize()
        //ansh
    )
}