package com.example.trustandroid20


import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge


import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api

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
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Brush
import com.example.trustandroid20.ui.theme.TrustAndroid20Theme

@Suppress("OVERRIDE_DEPRECATION")
class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val REQUEST_CODE_ENABLE_ADMIN = 1

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize EncryptedSharedPreferences
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            "user_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        enableEdgeToEdge()

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            Log.e("FirebaseError", "Failed to initialize Firebase", e)
        }

        // Check if the username is 'temp'
        val username = sharedPreferences.getString("username", "")

        Globalvariable.username = username ?: ""////////////////////////////////////////////

        if (username == "temp") {
            // Navigate to the login screen
            setContent {
                TrustAndroid20Theme {
                    LoginScreen(authViewModel = AuthViewModel(), sharedPreferences = sharedPreferences) { email ->
                        sharedPreferences.edit().putString("username", email).commit() // Use commit() for immediate save
                    }
                }
            }
        } else {
            // Check if user is already logged in
            if (sharedPreferences.getBoolean("is_logged_in", false)) {
                // Navigate to the main screen directly
                setContent {
                    TrustAndroid20Theme {
                        TrustAndroid()
                    }
                }
            } else {
                requestDeviceAdmin()
                setContent {
                    TrustAndroid20Theme {
                        TrustAndroid()
                    }
                }
            }
        }
    }


    private fun requestDeviceAdmin() {
        val deviceAdminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponent)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Your explanation here")
        }
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Device Admin Enabled", Toast.LENGTH_SHORT).show()
                // Start the foreground service
                val serviceIntent = Intent(this, MyForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
            } else {
                Toast.makeText(this, "Device Admin Enabling Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
@Composable
fun TrustAndroid() {
    val navController = rememberNavController()
    val bannedAppsList by remember { mutableStateOf<List<PackageInfo>>(emptyList()) }
    var allBannedApps by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
    val startDestination = if (isLoggedIn) "firstScreen/${sharedPreferences.getString("email", "")}" else "loginScreen"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("loginScreen") {
            LoginScreen(authViewModel = AuthViewModel(), sharedPreferences = sharedPreferences) { email ->
                navController.navigate("firstScreen/$email") {
                    popUpTo("loginScreen") { inclusive = true }
                }
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
    sharedPreferences: SharedPreferences,
    onSignInSuccess: (String) -> Unit // Pass email on success
) {
    val result = authViewModel.authResult.observeAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFDFEFE), // Light grey-white color
            modifier = Modifier.padding(bottom = 24.dp)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color(0xFF2C3E50)) }, // Light grey-white color
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFDFEFE), // Light grey-white color
                unfocusedIndicatorColor = Color(0xFFFDFEFE) // Light grey-white color
            )
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color(0xFF2C3E50)) }, // Light grey-white color
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFDFEFE), // Light grey-white color
                unfocusedIndicatorColor = Color(0xFFFDFEFE) // Light grey-white color
            )
        )

        Button(
            onClick = {
                if (isInternetAvailable(context)) {
                    sharedPreferences.edit().putString("username", email).commit() // Immediate save
                    Globalvariable.username = email
                    authViewModel.login(email, password)
                } else {
                    Toast.makeText(context, "No internet connection. Please try again.", Toast.LENGTH_LONG).show()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF28B463)), // Military green
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Login",
                fontSize = 16.sp,
                color = Color(0xFFFDFEFE) // Light grey-white color
            )
        }

        LaunchedEffect(result.value) {
            when (result.value) {
                is Result.Success<*> -> {
                    with(sharedPreferences.edit()) {
                        putString("email", email)
                        putString("password", password)
                        putBoolean("is_logged_in", true)
                        apply()
                    }
                    onSignInSuccess(email) // Pass email on success
                }

                is Result.Error -> {
                    Toast.makeText(
                        context,
                        "Incorrect credentials, please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {}
            }
        }
    }
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}