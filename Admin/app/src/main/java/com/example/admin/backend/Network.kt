package com.example.admin.backend

import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Network {

    private fun getByteUrl(urlParam: String): ByteArray {
        val url = URL(urlParam)
        val httpUrlConnection = url.openConnection() as HttpURLConnection
        val input = httpUrlConnection.inputStream
        val output = ByteArrayOutputStream()
        try {
            if (httpUrlConnection.responseCode != HttpURLConnection.HTTP_OK) {
                throw NetworkErrorException("")
            }
            val buffer = ByteArray(1024)
            var readBytes = 0
            while (
                run {
                    readBytes = input.read(buffer)
                    readBytes
                } > 0) {
                output.write(buffer, 0, readBytes)
            }
            output.close()
            return output.toByteArray()
        } finally {
            httpUrlConnection.disconnect()
        }
    }

    private fun getStringUrl(url: String): String {
        return String(getByteUrl(url))
    }

    /**
     * this fun will return json object,
     *   be sure to take network permission,
     *   use this fun in separated thread
     */
    fun getJsonBody(context: Context, url: String): JSONObject {
        if (checkConnection(context)) return JSONObject(getStringUrl(url))
        else throw FailureNetworkConnection("No network connection")
    }

    companion object {
        fun checkConnection(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }

    class FailureNetworkConnection(message: String) : Exception(message)
}