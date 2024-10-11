//package com.example.trustandroid20
//
//import android.content.Context
//import android.content.pm.ApplicationInfo
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.launch
//
//class AppViewModel(private val repository: AppRepository) : ViewModel() {
//    var installedBannedApps = MutableLiveData<List<ApplicationInfo>>(emptyList())
//
//    fun fetchBannedApps(context: Context) {
//        viewModelScope.launch {
//            val bannedList = repository.fetchBannedApps() // Fetch banned apps from Firestore
//            checkInstalledApps(context, bannedList)
//        }
//    }
//
//    private fun checkInstalledApps(context: Context, bannedList: List<String>) {
//        val installedApps = repository.getInstalledApps(context) // Get installed apps
//        installedBannedApps.value = installedApps.filter { app ->
//            bannedList.contains(app.packageName) // Check if installed app is banned
//        }
//    }
//}
