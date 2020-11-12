package com.mmoson9.epilepsynote.mvp.interactor

interface SharedPrefsInteractor {

    interface onIDRequest {
        fun onSharedPrefsIDReturn(userID: String)
    }

    interface onActionDone {
        fun onActionDone()
    }

    interface onSettingsRequest {
        fun onSettingsReturn(seizureSerie: String, serieNumber: String)
    }

    fun accessSharedPrefs()

    fun checkUserID(returnID: onIDRequest)

    fun setUserID(userID: String?)

    fun getCurrentSettings(returnSettings: onSettingsRequest)

    fun changeSettings(seizureSerie: String, serieNumber: String, onActionDone: onActionDone)
}