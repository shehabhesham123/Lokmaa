package com.example.lokma.pojo.listener

import com.example.lokma.pojo.model.User

interface SignInListener {
    fun setOnClickOnForgetPassword(email:String)
    fun setOnClickOnLogin(userEmail:String?)
    fun setOnClickOnSignUp()
    fun setOnClickOnSignWithGoogle()
    fun setOnClickOnSignWithApple()
}