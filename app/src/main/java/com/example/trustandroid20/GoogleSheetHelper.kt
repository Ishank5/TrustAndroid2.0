package com.example.trustandroid20


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GoogleSheetsHelper(private val webAppUrl: String) {

    // Function to send data to Google Sheets via POST request
    suspend fun sendDataToSheet(userName: String, appName: String, appPackage: String, appVersion: String, appInstallDate: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(webAppUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; utf-8")
                connection.doOutput = true

                val jsonInputString = JSONObject()
                    .put("userName", userName)
                    .put("appName", appName)
                    .put("appPackage", appPackage)
                    .put("appVersion", appVersion)
                    .put("appInstallDate", appInstallDate)
                    .toString()

                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(jsonInputString)
                    writer.flush()
                }

                val responseCode = connection.responseCode
                Log.d("GoogleSheetsHelper", "POST Response Code :: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    Log.d("GoogleSheetsHelper", "Data successfully sent to Google Sheets")
                    true
                } else {
                    Log.e("GoogleSheetsHelper", "Failed to send data to Google Sheets")
                    false
                }
            } catch (e: Exception) {
                Log.e("GoogleSheetsHelper", "Error in sendDataToSheet", e)
                false
            }
        }
    }

    // Optional: Function to retrieve data from Google Sheets via GET request
    suspend fun getDataFromSheet(): List<List<Any>>? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(webAppUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                Log.d("GoogleSheetsHelper", "GET Response Code :: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    // Parse the JSON response as needed
                    // For simplicity, returning null here
                    // Implement parsing based on your needs
                    null
                } else {
                    Log.e("GoogleSheetsHelper", "Failed to retrieve data from Google Sheets")
                    null
                }
            } catch (e: Exception) {
                Log.e("GoogleSheetsHelper", "Error in getDataFromSheet", e)
                null
            }
        }
    }
}