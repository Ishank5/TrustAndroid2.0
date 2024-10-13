package com.example.trustandroid20

import android.content.pm.PackageInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    NavHost(navController = navController, startDestination = "loginScreen") {
        composable("loginScreen") {
            LoginScreen(authViewModel = AuthViewModel()) { email ->
                navController.navigate("firstScreen/$email")
            }
        }
        composable(
            route = "firstScreen/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            HomeScreenUI({ feature ->
                when (feature) {
                    "Scanning" -> navController.navigate("secondScreen/$email")
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
        composable(
            route = "secondScreen/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            var showBannedApps by remember { mutableStateOf(false) }
            var bannedAppsList by remember { mutableStateOf<List<PackageInfo>>(emptyList()) }
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current

            if (showBannedApps) {
                BannedAppsList(bannedAppsList) { packageName ->
                    deleteApp(context, packageName)
                }
            } else {
                HomeScreen(
                    userName = email,
                    onUserNameChange = { /* No-op, as email should not change */ },
                    onScanButtonClick = {
                        coroutineScope.launch {
                            checkForBannedApps(context, email) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onSignInSuccess: (String) -> Unit // Pass email on success
) {
    val result = authViewModel.authResult.observeAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
            )
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
            )
        )
        val context = LocalContext.current
        Button(
            onClick = {
                authViewModel.login(email, password)
                when (result.value) {
                    is Result.Success<*> -> {
                        onSignInSuccess(email) // Pass email on success
                    }
                    is Result.Error -> {
                        Toast.makeText(context, "Incorrect credentials, please try again.", Toast.LENGTH_LONG).show()
                    }
                    else -> { }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Login",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}