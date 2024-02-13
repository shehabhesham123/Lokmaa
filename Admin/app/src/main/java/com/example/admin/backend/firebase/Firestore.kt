package com.example.admin.backend.firebase

import android.content.Context
import com.example.admin.backend.Network.Companion.checkConnection
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

class Firestore(private val context: Context) {

    private val firebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * if you pass path of document, you will receive list with one item in whatDoAfterEnd lambda
     *
     * and if you pass path of collection, you will receive list of items in whatDoAfterEnd lambda
     */
    fun download(
        path: String,
        onSuccess: (item: List<QueryDocumentSnapshot>) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            firebaseFirestore.collection(path).get()
                .addOnSuccessListener {
                    onSuccess(getDocuments(it))
                }
                .addOnFailureListener {
                    onFailure("An error occurred while downloading data")
                }
        } else onFailure("you are not connected to the Internet")
    }

    fun download(
        path: String,
        whereField: String,
        whereValue: Any,
        onSuccess: (item: List<QueryDocumentSnapshot>) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            firebaseFirestore.collection(path).whereEqualTo(whereField, whereValue).get()
                .addOnSuccessListener {
                    onSuccess(getDocuments(it))
                }
                .addOnFailureListener {
                    onFailure("An error occurred while downloading data")
                }
        } else onFailure("you are not connected to the Internet")
    }

    /**
     * this fun is used to upload obj, you must get id of document first by calling Firestore.id
     *
     * if uploading is success , you will receive document id in whatDoAfterEnd lambda
     *
     */
    fun upload(
        obj: Any,
        collectionPath: String,
        onSuccess: (id: String) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            val documentId = documentId()
            firebaseFirestore.document("$collectionPath/$documentId").set(obj)
                .addOnSuccessListener {
                    onSuccess(documentId)
                }
                .addOnFailureListener {
                    onFailure("An error occurred while uploading data")
                }
        } else onFailure("you are not connected to the Internet")
    }

    fun upload(
        obj: Any,
        collectionPath: String,
        documentId: String,
        onSuccess: (id: String) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            firebaseFirestore.document("$collectionPath/$documentId").set(obj)
                .addOnSuccessListener {
                    onSuccess(documentId)
                }
                .addOnFailureListener {
                    onFailure("An error occurred while uploading data")
                }
        } else onFailure("you are not connected to the Internet")
    }

    /**
     * this fun is used to upload more than on obj in same collection
     *
     * if the uploading is success, you will receive documents id in whatDoAfterEnd lambda
     */
    fun upload(
        objects: List<Any>,
        collectionPath: String,
        onSuccess: (ids: List<String>) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            val batch = firebaseFirestore.batch()
            val ids = mutableListOf<String>()
            for (i in objects.indices) {
                ids.add(documentId())
                val documentRef = firebaseFirestore.document("$collectionPath/${ids[i]}")
                batch.set(documentRef, i)
            }
            batch.commit()
                .addOnSuccessListener {
                    onSuccess(ids)
                }
                .addOnFailureListener {
                    onFailure("An error occurred while uploading data")
                }
        } else onFailure("you are not connected to the Internet")
    }

    /**
     *  this fun is used to register snapshot in document or collection,
     *  and return ListenerRegistration to able to cancel it after.
     *  and when something happened in document or collection will pass list of DocumentIsChanged object in whatDoAfterEnd lambda
     */
    fun addSnapshot(
        path: String,
        onSuccess: (documentsAreChanged: List<DocumentIsChanged>) -> Unit,
        onFailure: (msg: String) -> Unit
    ): ListenerRegistration? {
        var listenerRegistration: ListenerRegistration? = null

        if (checkConnection(context)) {
            listenerRegistration =
                firebaseFirestore.collection(path).addSnapshotListener { value, error ->
                    if (error == null) {
                        value?.let {
                            val list = mutableListOf<DocumentIsChanged>()
                            for (i in it.documentChanges) {
                                list.add(DocumentIsChanged(i.document, i.type))
                            }
                            onSuccess(list)
                        }
                    } else onFailure("An error occurred while set snapshot for data")
                }
        } else onFailure("you are not connected to the Internet")

        return listenerRegistration
    }

    /**
     * this fun is used to make pagination for documents in collection.
     * and will pass limit(number) of documents you need at a time.
     * and return last document to pass it again to start from it in the next time
     */
    fun pagination(
        path: String,
        limit: Long,
        lastDocumentSnapshotParam: DocumentSnapshot?,
        onSuccess: (items: List<Any>) -> Unit,
        onFailure: (msg: String) -> Unit
    ): DocumentSnapshot? {

        var lastDocumentSnapshot: DocumentSnapshot? = null
        if (checkConnection(context)) {
            if (lastDocumentSnapshotParam == null) {
                firebaseFirestore.collection(path).limit(limit).get()
                    .addOnSuccessListener {
                        onSuccess(getDocuments(it))
                        lastDocumentSnapshot = getLastDocument(it)
                    }
                    .addOnFailureListener {
                        onFailure("An error occurred while make pagination for data")
                    }
            } else {
                firebaseFirestore.collection(path).limit(limit)
                    .startAfter(lastDocumentSnapshotParam)
                    .get()
                    .addOnSuccessListener {
                        onSuccess(getDocuments(it))
                        lastDocumentSnapshot = getLastDocument(it)
                    }
                    .addOnFailureListener {
                        onFailure("An error occurred while make pagination for data")
                    }
            }
        } else onFailure("you are not connected to the Internet")

        return lastDocumentSnapshot
    }

    /**
     * this fun is used to update data in document
     */
    fun update(
        newData: Map<String, Any>,
        path: String,
        onSuccess: (msg: String) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            firebaseFirestore.document(path)
                .set(newData, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess("The data has been updated successfully")
                }
                .addOnFailureListener {
                    onFailure("An error occurred while updating data")
                }
        } else onFailure("you are not connected to the Internet")
    }

    fun update(
        newObj: Any,
        path: String,
        onSuccess: (msg: String) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            firebaseFirestore.document(path)
                .set(newObj, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess("The data has been updated successfully")
                }
                .addOnFailureListener {
                    onFailure("An error occurred while updating data")
                }
        } else onFailure("you are not connected to the Internet")
    }

    /**
     * this fun is used to delete a document
     */
    fun delete(
        path: String,
        onSuccess: (msg: String) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        if (checkConnection(context)) {
            firebaseFirestore.document(path).delete()
                .addOnSuccessListener {
                    onSuccess("The data has been deleted successfully")
                }
                .addOnFailureListener {
                    onFailure("An error occurred while deleting data")
                }
        } else onFailure("you are not connected to the Internet")
    }

    private fun getDocuments(querySnapshot: QuerySnapshot): List<QueryDocumentSnapshot> {
        val result = mutableListOf<QueryDocumentSnapshot>()
        for (i in querySnapshot) {
            result.add(i)
        }
        return result
    }

    private fun getLastDocument(querySnapshot: QuerySnapshot): DocumentSnapshot? {
        val size = querySnapshot.documents.size
        return if (size != 0) querySnapshot.documents[size - 1]
        else null
    }

    companion object {
        fun documentId() = System.currentTimeMillis().toString()
    }

    /**
     * this class is used in snapshot, the obj of this class will contain the obj(document) to which the change occurred
     */
    class DocumentIsChanged(val obj: QueryDocumentSnapshot, val whatHappened: DocumentChange.Type)
}
