package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.view.SeizureEditView

abstract class SeizureEditPresenter {
    abstract fun attachView(view: SeizureEditView)

    abstract fun checkUserID(context: Context)

    abstract fun getData(seizureID: String)

    abstract fun setDate(day: Int, month: Int, year: Int)

    abstract fun setTime(hour: Int, minute: Int)

    abstract fun updateData(userID: String, size: String, description: String)

    abstract fun deleteData(userID: String)
}