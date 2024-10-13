package com.example.trustandroid20.ui

import android.content.pm.PackageInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.trustandroid20.Globalvariable

import com.example.trustandroid20.R
import com.example.trustandroid20.handleFeature2
import com.example.trustandroid20.handleFeature3

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
                    onClick = { handleFeature2(context, Globalvariable.username) },
                    modifier = Modifier.weight(1f).padding(8.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FeatureBox(
                    featureName = "Share Now", icon = shareIcon,
                    onClick = { handleFeature3(context, Globalvariable.username) },
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