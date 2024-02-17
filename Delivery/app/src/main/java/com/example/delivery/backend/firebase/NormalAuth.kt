package com.example.delivery.backend.firebase

import android.content.Context
import com.example.delivery.backend.Network.Companion.checkConnection
import com.google.firebase.auth.FirebaseUser

class NormalAuth(private val context: Context) : Authentication() {

    fun signUp(
        email: String,
        password: String,
        onSuccess: (currentUser: FirebaseUser?) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                onSuccess(it.user)
            }.addOnFailureListener {
                onFailure("${it.message}")
            }
        } else onFailure("you are not connected to the Internet")
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: (currentUser: FirebaseUser?) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                onSuccess(it.user)
            }.addOnFailureListener {
                onFailure("${it.message}")
            }
        } else onFailure("you are not connected to the Internet")
    }

    fun signOut() {
        if (checkConnection(context)) auth.signOut()
    }

    fun getCurrentUser(): String? {
        val email = auth.currentUser?.email
        var username = ""
        email?.let {
            for (i in it) {
                if (i != '@') username += i
                else return username
            }
        }
        return null
    }

    fun updatePassword(
        currentUser: FirebaseUser,
        newPassword: String,
        onSuccess: (msg: String) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            currentUser.updatePassword(newPassword).addOnSuccessListener {
                onSuccess("Update successfully")
            }.addOnFailureListener {
                onFailure("${it.message}")
            }
        } else onFailure("you are not connected to the Internet")
    }

}