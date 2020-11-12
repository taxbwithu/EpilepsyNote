package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.view.SettingsView

abstract class SettingsPresenter {
    abstract fun attachView(view: SettingsView)

    abstract fun currentSettings(context: Context)

    abstract fun checkUserID(context : Context)

    abstract fun changeSettings(seizureSerie: String, serieNumber : String)

    abstract fun deleteUser(userID : String)
}