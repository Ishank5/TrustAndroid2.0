package com.example.trustandroid20.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustandroid20.R

@Composable
fun DeveloperDetailsScreen() {
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
            ), // Very Dark Blue-Grey
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.mipmap.anshika_foreground),
            contentDescription = "Ansh Bajaj",
            modifier = Modifier
                .size(150.dp)
                .padding(8.dp)
        )
        Text(
            text = "Developer 1: Ansh Bajaj\nEmail: abajaj1_be23@thapar.edu\nContact: 8685988991",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFFD1E8E2), // Light Grey
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.mipmap.isgl_foreground),
            contentDescription = "Ishank Goyal",
            modifier = Modifier
                .size(150.dp)
                .padding(8.dp)
        )
        Text(
            text = "Developer 2: Ishank Goyal\nEmail: igoyal_be23@thapar.edu\nContact: 8708667212",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFFD1E8E2), // Light Grey
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.mipmap.isgl_foreground),
            contentDescription = "New Developer",
            modifier = Modifier
                .size(150.dp)
                .padding(8.dp)
        )
        Text(
            text = "Developer 3: Ratn Govindam\nEmail: r_govindam_be22@thapar.edu\nContact: 9779791959",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFFD1E8E2), // Light Grey
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp).padding(bottom =32.dp)
        )
    }
}