package com.mmoson9.epilepsynote.mvp.interactor.impl

import android.content.Context
import android.content.SharedPreferences
import com.mmoson9.epilepsynote.mvp.interactor.SharedPrefsInteractor

class SharedPrefsInteractorImpl(val context: Context) : SharedPrefsInteractor {

    val PREF_KEY = "EP_NOTE"
    var uID: String = ""
    lateinit var mPrefs: SharedPreferences

    override fun accessSharedPrefs() {
        mPrefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
    }

    override fun checkUserID(returnID: SharedPrefsInteractor.onIDRequest) {
        accessSharedPrefs()
        uID = mPrefs.getString("userID", "")!!
        returnID.onSharedPrefsIDReturn(uID)
    }

    override fun setUserID(userID: String?) {
        accessSharedPrefs()
        val editor = mPrefs.edit()
        editor.putString("userID", userID)
        editor.apply()
    }

    override fun getCurrentSettings(returnSettings: SharedPrefsInteractor.onSettingsRequest) {
        accessSharedPrefs()
        val seizureSerie = mPrefs.getString("seizureSerie", "15")!!
        val serieNumber = mPrefs.getString("serieNumber", "2")!!
        returnSettings.onSettingsReturn(seizureSerie, serieNumber)
    }

    override fun changeSettings(
        seizureSerie: String,
        serieNumber: String,
        onActionDone: SharedPrefsInteractor.onActionDone
    ) {
        accessSharedPrefs()
        val editor = mPrefs.edit()
        editor.putString("seizureSerie", seizureSerie)
        editor.putString("serieNumber", serieNumber)
        editor.apply()
        onActionDone.onActionDone()
    }
}