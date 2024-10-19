package com.example.trustandroid20

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper

import androidx.core.app.NotificationCompat


class MyForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("App is running")
            .setContentText("Monitoring banned apps")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .build()
        startForeground(1, notification)
        checkForBannedAppsPeriodically()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun checkForBannedAppsPeriodically() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val sharedPreferences: SharedPreferences = getSharedPreferences(
                    "user_prefs",
                    Context.MODE_PRIVATE
                )
                var userName: String = sharedPreferences.getString("username", "temp") ?: "temp"
                if (Globalvariable.username.isNotEmpty()) {
                    userName = Globalvariable.username
                }

                checkForBannedApps(applicationContext, userName) {
                    // Handle the result if needed
                }
                handler.postDelayed(this, 1 * 10 * 1000) // Run every 10 seconds
            }
        }
        handler.post(runnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

