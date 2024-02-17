package com.example.lokma.pojo.listener

import androidx.fragment.app.Fragment

interface MotivationalListener {
    fun setOnClickOnContinue(nextFragment: Fragment)
    fun setOnClickOnSignIn()
}