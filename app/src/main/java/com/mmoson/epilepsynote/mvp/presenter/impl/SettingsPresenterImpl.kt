package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.interactor.SharedPrefsInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.FirestoreInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.FirestoreInteractorImpl
import com.mmoson9.epilepsynote.mvp.interactor.impl.SharedPrefsInteractorImpl
import com.mmoson9.epilepsynote.mvp.view.SettingsView

class SettingsPresenterImpl (val interactorDB: FirestoreInteractor = FirestoreInteractorImpl()) :
    SettingsPresenter(), SharedPrefsInteractor.onSettingsRequest,
    SharedPrefsInteractor.onActionDone, SharedPrefsInteractor.onIDRequest, FirestoreInteractor.onActionDone {

    private var view: SettingsView? = null
    lateinit var interactorSP: SharedPrefsInteractor
    lateinit var context: Context

    override fun attachView(view: SettingsView) {
        this.view = view
    }

    override fun currentSettings(context: Context) {
        this.context = context
        interactorSP = SharedPrefsInteractorImpl(context)
        interactorSP.getCurrentSettings(this)
    }

    override fun checkUserID(context: Context) {
        interactorSP.checkUserID(this)
    }

    override fun deleteUser(userID: String) {
        interactorSP.setUserID("")
        interactorDB.deleteUser(userID, this)
    }

    override fun onSettingsReturn(seizureSerie: String, serieNumber: String) {
        view?.displaySettings(seizureSerie, serieNumber)
    }

    override fun changeSettings(seizureSerie: String, serieNumber: String) {
        interactorSP.changeSettings(seizureSerie, serieNumber, this)
    }

    override fun onActionDone() {
        view?.settingsChanged()
    }

    override fun onSharedPrefsIDReturn(userID: String) {
        view?.setUserID(userID)
    }

    override fun onActionDone(docExists: Boolean) {
        view?.userDeleted()
    }
}