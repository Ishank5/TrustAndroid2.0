//package com.example.trustandroid20
//
//import android.content.Context
//import android.content.pm.ApplicationInfo
//import com.google.firebase.firestore.FirebaseFirestore
//
//class AppRepository(private val firestore: FirebaseFirestore) {
//
//    suspend fun fetchBannedApps(): List<String> {
//        // Fetch the list of banned apps from Firestore
//        val bannedApps = mutableListOf<String>()
//        firestore.collection("bannedApps")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    bannedApps.add(document.getString("packageName") ?: "")
//                }
//            }
//        return bannedApps
//    }
//
//    fun getInstalledApps(context: Context): List<ApplicationInfo> {
//        return context.packageManager.getInstalledApplications(0) // Get installed apps
//    }
//}
