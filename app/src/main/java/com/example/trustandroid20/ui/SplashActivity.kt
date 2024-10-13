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
import androidx.compose.ui.graphics.painter.Painter
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
                text = "APP Scanner",
                fontSize = 22.sp,
                color = Color.White
            )
            Text(
                text = "Designed and Developed",
                fontSize = 22.sp,
                color = Color.White
            )
            Text(
                text = "By",
                fontSize = 22.sp,
                color = Color.White
            )
            Text(
                text = "2 Corps Zone Work Shop, Patiala",
                fontSize = 22.sp,
                color = Color.White
            )
            Text(
                text = " and ",
                fontSize = 22.sp,
                color = Color.White
            )
            Text(
                text = "Thapar Institute of Engg. & Tech, Patiala",
                fontSize = 22.sp,
                color = Color.White
            )
            Row {
                val image2: Painter = painterResource(id = R.mipmap.ic_launcher_foreground)
                Image(
                    painter = image2,
                    contentDescription = "Example Image",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                )
                val image: Painter = painterResource(id = R.mipmap.ic_tiet_foreground)
                Image(
                    painter = image,
                    contentDescription = "Example Image",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                )

            }
        }
    }
}

