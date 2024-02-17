package com.example.lokma.firebase

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.lokma.pojo.constant.Constant
import com.example.lokma.pojo.model.Validation.Companion.isOnline
import com.google.firebase.storage.FirebaseStorage

class Storage(private val context: Context) {

    private val firebaseStorage = FirebaseStorage.getInstance()

    fun upload(
        path: String,
        imageUri: Uri?,
        whatDoAfterEnd: (Uri) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            imageUri?.let {
                val imageName = System.currentTimeMillis().toString()
                val ref = FirebaseStorage.getInstance().reference.child("$path$imageName")
                ref.putFile(imageUri)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            whatDoAfterEnd(it)
                        }
                    }
                    .addOnFailureListener {
                        whatDoIfFailure("An error occurred while uploading data")
                    }
            }
        }
        else whatDoIfFailure("you are not connected to the Internet")
    }

    fun delete(
        imageUrl: Uri,
        whatDoAfterEnd: (String) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            firebaseStorage.getReferenceFromUrl(imageUrl.toString()).delete()
                .addOnSuccessListener {
                    whatDoAfterEnd("The data has been deleted successfully")
                }
                .addOnFailureListener {
                    whatDoIfFailure("An error occurred while deleting data")
                }
        }
        else whatDoIfFailure("you are not connected to the Internet")
    }

    fun update(
        pastImageUrl: Uri,
        newImageUri: Uri,
        path: String,
        whatDoAfterEnd: (Uri) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            delete(pastImageUrl,
                {
                    upload(path, newImageUri, {
                        whatDoAfterEnd(it)
                    }, {
                        whatDoIfFailure(it)
                    })
                }, {
                    whatDoIfFailure(it)
                })
        }
        else whatDoIfFailure("you are not connected to the Internet")
    }

}
