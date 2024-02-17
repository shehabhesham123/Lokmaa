package com.example.lokma.pojo.listener

import com.example.lokma.pojo.model.Stack

interface ArrowBackListener {
    // this fun return to previous fragment
    fun setOnClickOnArrowBack()

    // this fun return to specific fragment
    fun setOnClickOnArrowBack(parentFragment: Stack.FragmentName)
}