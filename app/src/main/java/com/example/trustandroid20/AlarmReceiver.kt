//package com.example.trustandroid20
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//
//class AlarmReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        // Call the function checkForBannedApps here
//        val userName = intent.getStringExtra("userName") ?: return
//        checkForBannedApps(context, userName) {
//            // Handle result if needed
//        }
//    }
//}