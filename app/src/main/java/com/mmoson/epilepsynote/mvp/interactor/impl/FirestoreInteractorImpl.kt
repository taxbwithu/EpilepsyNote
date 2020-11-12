package com.mmoson9.epilepsynote.mvp.interactor.impl

import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*

class FirestoreInteractorImpl(
    val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val usersRef: CollectionReference = db.collection("users")
) : FirestoreInteractor {

    override fun initUser(returnID: FirestoreInteractor.onUserIDRequest){
        val docRef = usersRef.document()
        val temporaryID = docRef.id
        returnID.onUserIDReturn(temporaryID)
    }

    override fun addSeizure(
        userID: String,
        seizure: Seizure,
        countSet: String,
        returnID: FirestoreInteractor.onUserIDRequest,
        docAdded: FirestoreInteractor.onActionDone
    ) {
        if (userID.isEmpty()) {
            docAdded.onActionDone(false)
        } else {
            for (x in 0..countSet.toInt() - 1) {
                usersRef.document(userID).collection("seizures").add(seizure)
            }
            docAdded.onActionDone(true)
        }
    }

    override fun getSeizures(
        userID: String,
        onSeizureListRequest: FirestoreInteractor.onSeizureListRequest
    ) {
        val seizureList = ArrayList<Seizure>()
        usersRef.document(userID).collection("seizures")
            .orderBy("timestamp", Query.Direction.DESCENDING).get()
            .addOnSuccessListener(OnSuccessfullyRecievedSeizures(seizureList, onSeizureListRequest))
    }

    override fun getSingleSeizure(
        userID: String,
        seizureID: String,
        onSeizureRequest: FirestoreInteractor.onSeizureRequest
    ) {
        usersRef.document(userID).collection("seizures").document(seizureID).get()
            .addOnSuccessListener(OnSuccesfullyReceivedSingleSeizure(onSeizureRequest))
    }

    override fun updateSeizure(
        userID: String,
        seizureID: String,
        seizure: Seizure,
        docAdded: FirestoreInteractor.onActionDone
    ) {
        usersRef.document(userID).collection("seizures").document(seizureID).set(seizure)
        docAdded.onActionDone(true)
    }

    override fun deleteSeizure(
        userID: String,
        seizureID: String,
        docDeleted: FirestoreInteractor.onActionDone
    ) {
        usersRef.document(userID).collection("seizures").document(seizureID).delete()
        docDeleted.onActionDone(false)
    }

    override fun deleteUser(userID: String, userDeleted: FirestoreInteractor.onActionDone) {
        usersRef.document(userID).delete()
        userDeleted.onActionDone(true)
    }

    inner class OnSuccessfullyRecievedSeizures(
        val seizureList: ArrayList<Seizure>,
        val onSeizureListRequest: FirestoreInteractor.onSeizureListRequest
    ) : OnSuccessListener<QuerySnapshot> {
        override fun onSuccess(result: QuerySnapshot) {
            for (document in result) {
                seizureList.add(document.toObject(Seizure::class.java)
                    .apply {
                        this.id = document.id
                    })
            }
            onSeizureListRequest.onSeizureListReturn(seizureList)
        }
    }

    inner class OnSuccesfullyReceivedSingleSeizure(
        val onSeizureRequest: FirestoreInteractor.onSeizureRequest
    ) : OnSuccessListener<DocumentSnapshot>{
        override fun onSuccess(p0: DocumentSnapshot) {
            onSeizureRequest.onSeizureReturn(p0.toObject(Seizure::class.java)!!)
        }
    }
}