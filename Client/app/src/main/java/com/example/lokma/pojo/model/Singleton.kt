package com.example.lokma.pojo.model

import android.content.Context
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Singleton(private val context: Context) {
    private val requestQueue = Volley.newRequestQueue(context)

    fun addRequest(stringRequest: StringRequest) {
        requestQueue.add(stringRequest)
    }

    companion object {
        fun getInstance(context: Context): Singleton {
            return Singleton(context)
        }
    }
}