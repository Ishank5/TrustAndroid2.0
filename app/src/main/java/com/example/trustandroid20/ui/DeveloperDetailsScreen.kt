package com.example.trustandroid20.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
