package com.example.trustandroid20

import android.content.pm.PackageInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trustandroid20.ui.BannedAppsList
import com.example.trustandroid20.ui.DeveloperDetailsScreen
import com.example.trustandroid20.ui.HomeScreen
import com.example.trustandroid20.ui.HomeScreenUI
import com.example.trustandroid20.ui.ShowAllBannedAppsScreen
import com.example.trustandroid20.ui.theme.TrustAndroid20Theme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    val bannedAppsList by remember { mutableStateOf<List<PackageInfo>>(emptyList()) }
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
