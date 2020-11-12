package com.mmoson9.epilepsynote.mvp.interactor.impl

interface FirestoreInteractor {

    interface onUserIDRequest {
        fun onUserIDReturn(userID: String?)
    }

    interface onSeizureListRequest {
        fun onSeizureListReturn(seizures: ArrayList<Seizure>)
    }

    interface onSeizureRequest {
        fun onSeizureReturn(seizure: Seizure)
    }

    interface onActionDone {
        fun onActionDone(docExists: Boolean)
    }

    fun initUser(returnID: onUserIDRequest)

    fun addSeizure(
        userID: String,
        seizure: Seizure,
        countSet: String,
        returnID: onUserIDRequest,
        docAdded: onActionDone
    )

    fun getSeizures(userID: String, onSeizureListRequest: onSeizureListRequest)

    fun getSingleSeizure(userID: String, seizureID: String, onSeizureRequest: onSeizureRequest)

    fun updateSeizure(userID: String, seizureID: String, seizure: Seizure, docAdded: onActionDone)

    fun deleteSeizure(userID: String, seizureID: String, docDeleted: onActionDone)

    fun deleteUser(userID: String, userDeleted : onActionDone)
}