package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.view.AddView

abstract class AddPresenter {
    abstract fun attachView(view: AddView)

    abstract fun checkUserID(context: Context)

    abstract fun setDate(day: Int, month: Int, year: Int)

    abstract fun setTime(hour: Int, minute: Int)

    abstract fun addData(userID: String, size: String, descr: String, countSet: String)

    abstract fun detachView()
}