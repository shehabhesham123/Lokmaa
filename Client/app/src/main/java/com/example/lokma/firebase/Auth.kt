package com.example.lokma.firebase

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.model.Validation.Companion.isOnline
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Auth(private val context: Context) {

    private val cloudPath = "Users/ProfileImage"
    private val firestorePath = "Users/"
    private val profileImageFieldKey = "Image"

    private val auth = FirebaseAuth.getInstance()
    private val storage = Storage(context)
    private val firestore = Firestore(context)

    fun createNewUser(
        user: User,
        whatDoAfterEnd: (FirebaseUser?) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener {
                    uploadUserData(user, it.user, whatDoAfterEnd, whatDoIfFailure)
                }
                .addOnFailureListener {
                    whatDoIfFailure("${it.message}")
                }
        }
        else whatDoIfFailure("you are not connected to the Internet")
    }

    // user email is key
    private fun uploadUserData(
        user: User,
        firebaseUser: FirebaseUser?,
        whatDoAfterEnd: (FirebaseUser?) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            if (user.imageUri == null || user.imageUri == Uri.parse("null")) {
                uploadUserDataToFirestore(user, firebaseUser, {
                    whatDoAfterEnd(it)
                }, {
                    whatDoIfFailure(it)
                })
            } else {
                // upload image then upload user data
                storage.upload(cloudPath, user.imageUri, { url ->
                    // add image url to data to upload it to firestore
                    user.data[profileImageFieldKey] = url.toString()
                    uploadUserDataToFirestore(user, firebaseUser, {
                        whatDoAfterEnd(it)
                    }, {
                        whatDoIfFailure(it)
                    })
                }, {
                    whatDoIfFailure(it)
                })
            }
        }
        else whatDoIfFailure("you are not connected to the Internet")
    }

    private fun uploadUserDataToFirestore(
        user: User,
        firebaseUser: FirebaseUser?,
        whatDoAfterEnd: (FirebaseUser?) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            firestore.upload(user.data, firestorePath + user.email, {
                // whatDoAfterEnd
                whatDoAfterEnd(firebaseUser)
            }, {
                whatDoIfFailure(it)
            })
        }
        else whatDoIfFailure("you are not connected to the Internet")
    }


    fun currentUser(): String? {
        val currentUser = auth.currentUser
        return currentUser?.email
    }

    fun signOut() {
        if (isOnline(context)) {
            auth.signOut()
        }
    }

    fun login(
        email: String,
        password: String,
        whatDoAfterEnd: (FirebaseUser?) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    whatDoAfterEnd(it.user)
                }.addOnFailureListener {
                    whatDoIfFailure("${it.message}")
                }
        }
        else whatDoIfFailure("you are not connected to the Internet")
    }

    fun loginWithGoogle() {
        /*val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.your_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()*/
    }


    // this class is used in createNewUser only
    data class User(
        val email: String,
        val password: String,
        val data: MutableMap<String, Any>,
        val imageUri: Uri?
    )


}