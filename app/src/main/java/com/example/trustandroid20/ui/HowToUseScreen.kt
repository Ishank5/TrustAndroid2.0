package com.example.trustandroid20


import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView



@Composable
fun HowToUseScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        //.padding(16.dp) // Optional: Add some padding if needed
        verticalArrangement = Arrangement.Center, // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        // Spacer(modifier = Modifier.height(150.dp)) // Optional spacer to push the WebView down

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
                    loadUrl("file:///android_asset/how_to_use_video.html")
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f) // Make the WebView take up 80% of the width
                .fillMaxHeight(0.8f) // Make the WebView take up 80% of the height
        )
    }
}
