package com.example.trustandroid20

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences


class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Access existing SharedPreferences
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                "user_prefs",
                Context.MODE_PRIVATE
            )

            // Retrieve username from SharedPreferences
            val username = sharedPreferences.getString("username", "temp")
            Globalvariable.username = username.toString()

            // Start the service
            val serviceIntent = Intent(context, MyForegroundService::class.java)
            serviceIntent.putExtra("username", Globalvariable.username)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}