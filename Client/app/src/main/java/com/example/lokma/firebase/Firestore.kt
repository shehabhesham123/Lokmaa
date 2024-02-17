package com.example.lokma.firebase

import android.content.Context
import android.util.Log
import com.example.lokma.pojo.model.Validation.Companion.isOnline
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

class Firestore(private val context: Context) {
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    // call it to get unique id to use it as document id
    val uniqueId = System.currentTimeMillis().toString()

    // path ---> end by document name
    fun upload(
        data: Map<String, Any>,
        documentPath: String,
        whatDoAfterEnd: (String) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            firebaseFirestore.document(documentPath).set(data)
                .addOnSuccessListener {
                    whatDoAfterEnd("The data has been uploaded successfully")
                }
                .addOnFailureListener {
                    whatDoIfFailure("An error occurred while uploading data")
                }
                .addOnCompleteListener {

                }
        } else whatDoIfFailure("you are not connected to the Internet")
    }

    // path --> end with collection name
    fun download(
        collectionPath: String,
        whatDoAfterEnd: (List<Data>) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            val result = mutableListOf<Data>()
            firebaseFirestore.collection(collectionPath).get()
                .addOnSuccessListener {
                    for (i in it) {
                        result.add(Data(i.id, i.data))
                    }
                    whatDoAfterEnd(result)
                }
                .addOnFailureListener {
                    whatDoIfFailure("An error occurred while downloading data")
                }
        } else whatDoIfFailure("you are not connected to the Internet")
    }

    // path --> end with document name
    fun downloadOneDocument(
        documentPath: String,
        whatDoAfterEnd: (Data) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            firebaseFirestore.document(documentPath).get()
                .addOnSuccessListener {
                    whatDoAfterEnd(Data(it.id, it.data?.toMap()))
                }
                .addOnFailureListener {
                    whatDoIfFailure("An error occurred while downloading data")
                }
        } else whatDoIfFailure("you are not connected to the Internet")
    }

    // path --> end with document name
    fun update(
        data: Map<String, Any>,
        documentPath: String,
        whatDoAfterEnd: (String) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            firebaseFirestore.document(documentPath)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    whatDoAfterEnd("The data has been updated successfully")
                }
                .addOnFailureListener {
                    whatDoIfFailure("An error occurred while updating data")
                }
        } else whatDoIfFailure("you are not connected to the Internet")
    }

    // path --> end with document name
    fun delete(
        documentPath: String,
        whatDoAfterEnd: (String) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            firebaseFirestore.document(documentPath).delete()
                .addOnSuccessListener {
                    whatDoAfterEnd("The data has been deleted successfully")
                }
                .addOnFailureListener {
                    whatDoIfFailure("An error occurred while deleting data")
                }
        } else whatDoIfFailure("you are not connected to the Internet")
    }

    fun download(
        collectionPath: String,
        orderByField: String,
        limit: Long,
        whatDoAfterEnd: (List<Data>) -> Unit,
        whatDoIfFailure: (String) -> Unit
    ) {
        if (isOnline(context)) {
            firebaseFirestore.collection(collectionPath)
                .orderBy(orderByField, Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener {
                    val result = mutableListOf<Data>()
                    for (i in it) {
                        result.add(Data(i.id, i.data))
                    }
                    whatDoAfterEnd(result)
                }
                .addOnFailureListener {
                    whatDoIfFailure("An error occurred while downloading data")
                }
        } else {
            whatDoIfFailure("you are not connected to the Internet")
        }

    }

    data class Data(val id: String, val data: Map<String, Any>?)
}