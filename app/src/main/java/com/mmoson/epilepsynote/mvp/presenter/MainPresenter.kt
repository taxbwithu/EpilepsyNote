package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.view.MainView

abstract class MainPresenter {
    abstract fun attachView(view: MainView)

    abstract fun checkUserID(context: Context)
}