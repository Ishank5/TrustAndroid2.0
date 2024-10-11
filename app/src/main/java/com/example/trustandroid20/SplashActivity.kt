package com.example.trustandroid20

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trustandroid20.ui.theme.TrustAndroid20Theme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrustAndroid20Theme {
                SplashScreen()
            }
        }

        // Navigate to MainActivity after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}

@Composable
fun SplashScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF041815)), // Army Green Background
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF1C2833)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Designed and Developed By",
                fontSize = 24.sp,
                color = Color.White
            )
            Text(
                text = "2 Corps Zone Work Shop, Patiala and ",
                fontSize = 24.sp,
                color = Color.White
            )
            Text(
                text = "Thapar Institute of Engineering and",
                fontSize = 24.sp,
                color = Color.White
            )
            Text(
                text = "Technology",
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    TrustAndroid20Theme {
        SplashScreen()
    }
}