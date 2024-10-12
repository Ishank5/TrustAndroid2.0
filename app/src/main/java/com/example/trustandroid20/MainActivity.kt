package com.example.trustandroid20

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.trustandroid20.ui.theme.TrustAndroid20Theme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//hello baby
        enableEdgeToEdge()

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            Log.e("FirebaseError", "Failed to initialize Firebase", e)
        }

        setContent {
            TrustAndroid20Theme {
                TrustAndroid()
            }
        }
    }
}

@Composable
fun TrustAndroid() {
    val navController = rememberNavController()
    var bannedAppsList by remember { mutableStateOf<List<PackageInfo>>(emptyList()) }
    var allBannedApps by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    NavHost(navController = navController, startDestination = "firstScreen") {

        composable("firstScreen") {
            HomeScreenUI ({ feature ->
                when (feature) {
                    "Scanning" -> navController.navigate("secondScreen")
                    "Feature3" -> navController.navigate("feature3Screen")
                    "Feature 4" -> {
                        fetchAllBannedAppsFromFirestore {
                            allBannedApps = it
                            navController.navigate("showAllScreen")
                        }
                    }
                    "ContactDevelopers" -> navController.navigate("contactDevelopersScreen")
                }
            }, bannedAppsList, onContactClick = {
                navController.navigate("contactDevelopersScreen")
            }, onHowToUseClick = {
                navController.navigate("howToUseScreen")
            })
        }

        composable("secondScreen") {
            var showBannedApps by remember { mutableStateOf(false) }
            var bannedAppsList by remember { mutableStateOf<List<PackageInfo>>(emptyList()) }
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current

            if (showBannedApps) {
                BannedAppsList(bannedAppsList) { packageName ->
                    deleteApp(context, packageName)
                }
            } else {
                var userName by remember { mutableStateOf("") }
                HomeScreen(
                    userName = userName,
                    onUserNameChange = { userName = it },
                    onScanButtonClick = {
                        coroutineScope.launch {
                            checkForBannedApps(context, userName) {
                                bannedAppsList = it
                                showBannedApps = true
                            }
                        }
                    }
                )
            }
        }

        composable("showAllScreen") {
            ShowAllBannedAppsScreen(allBannedApps)
        }

        composable("contactDevelopersScreen") {
            DeveloperDetailsScreen()
        }

        composable("howToUseScreen") {
            HowToUseScreen()
        }
    }
}