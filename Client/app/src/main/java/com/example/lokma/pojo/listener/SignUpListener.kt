package com.example.lokma.pojo.listener

import com.example.lokma.pojo.model.User

interface SignUpListener {
    fun setOnClickRegister(userEmail:String?)
    fun setOnClickSignIn()
}