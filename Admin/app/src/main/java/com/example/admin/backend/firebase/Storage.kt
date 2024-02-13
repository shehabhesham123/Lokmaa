package com.example.admin.backend.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.admin.backend.Network.Companion.checkConnection
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Storage(private val context: Context) {
    private val storageInstance = FirebaseStorage.getInstance()
    private val storageRef = storageInstance.reference

    /**
     * this fun is used to upload image to firebase storage,
     * and you will receive image url and id in onSuccess lambda
     * you can use image url to download image by using Picasso
     *
     */
    fun upload(
        uri: Uri, folderPath: String,
        onSuccess: (url: String) -> Unit,
        onFailure: (msg: String) -> Unit,
        onProgress: (progress: Long) -> Unit
    ) {
        if (checkConnection(context)) {
            val mImageId = imageId()
            Log.i("shehab","id: $mImageId")
            storageRef.child(folderPath).child(mImageId.toString()).putFile(uri)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }
                }
                .addOnFailureListener {
                    onFailure("${it.message}")
                }
                .addOnProgressListener {
                    val process = (it.bytesTransferred / it.totalByteCount) * 100
                    onProgress(process)
                }
        } else onFailure("you are not connected to the Internet")
    }

    /**
     * this fun is used to upload image to firebase storage,
     * and you will receive image url in onSuccess lambda
     */
    fun upload(
        byteArray: ByteArray, folderPath: String,
        onSuccess: (url: String) -> Unit,
        onFailure: (msg: String) -> Unit,
        onProgress: (progress: Long) -> Unit
    ) {
        if (checkConnection(context)) {
            val mImageId = imageId()
            storageRef.child(folderPath).child(mImageId.toString()).putBytes(byteArray)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }
                }
                .addOnFailureListener {
                    onFailure("${it.message}")
                }
                .addOnProgressListener {
                    val process = (it.bytesTransferred / it.totalByteCount) * 100
                    onProgress(process)
                }
        } else onFailure("you are not connected to the Internet")
    }

    /** this fun is used to download image as bitmap and receive bitmap of image on onSuccess lambda
     */
    fun download(
        imageUrl: String,
        onSuccess: (image: Bitmap) -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        if (checkConnection(context)) {
            val file = File.createTempFile("image", "jpg")
            storageInstance.getReferenceFromUrl(imageUrl).getFile(file)
                .addOnSuccessListener {
                    onSuccess(BitmapFactory.decodeFile(file.absolutePath))
                }
                .addOnFailureListener {
                    onFailure("${it.message}")
                }
        } else onFailure("you are not connected to the Internet")
    }

    /**
     * this fun is used to delete image from firebase storage
     */
    fun delete(
        imageUrl: String,
        onSuccess: (msg: String) -> Unit,
        onFailure: (msg: String) -> Unit,
    ) {
        if (checkConnection(context)) {
            storageInstance.getReferenceFromUrl(imageUrl).delete()
                .addOnSuccessListener {
                    onSuccess("the image is deleted successfully")
                }
                .addOnFailureListener {
                    onFailure("${it.message}")
                }
        } else onFailure("you are not connected to the Internet")
    }

    /**
     * this fun is used to update image and return new image url on onSuccess lambda
     */
    fun update(
        oldImageUrl: String,
        newImageUri: Uri,
        onSuccess: (newUrl: String) -> Unit,
        onFailure: (msg: String) -> Unit,
        onProgress: (progress: Long) -> Unit
    ) {
        val ref = storageInstance.getReferenceFromUrl(oldImageUrl)
        val path = ref.path
        delete(oldImageUrl, {
            upload(newImageUri, path, {
                onSuccess(it)
            }, {
                onFailure(it)
            }, {
                onProgress(it)
            })
        }, {
            onFailure(it)
        })
    }

    companion object {
        fun imageId() = System.currentTimeMillis()
    }
}