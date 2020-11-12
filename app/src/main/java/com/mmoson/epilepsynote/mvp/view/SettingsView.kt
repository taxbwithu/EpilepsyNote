package com.mmoson9.epilepsynote.mvp.view

interface SettingsView {
    fun settingsChanged()

    fun displaySettings(seizureSerie: String, serieNumber : String)

    fun setUserID(userID : String)

    fun userDeleted()
}