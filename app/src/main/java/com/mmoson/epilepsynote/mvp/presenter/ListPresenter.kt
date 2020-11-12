package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.view.ListView

abstract class ListPresenter {
    abstract fun attachView(view: ListView)

    abstract fun checkUserID(context: Context)

    abstract fun setDate(day: Int, month: Int, year: Int, sDate: Boolean)

    abstract fun filterDates(startDate: Date, endDate: Date)

    abstract fun showAllRecords()
}