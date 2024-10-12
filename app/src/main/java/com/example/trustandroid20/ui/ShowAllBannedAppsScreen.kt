package com.example.trustandroid20.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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
