package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.view.ExportView

abstract class ExportPresenter {
    abstract fun attachView(view: ExportView)

    abstract fun checkUserID(context: Context)

    abstract fun setDate(day: Int, month: Int, year: Int, sDate: Boolean)

    abstract fun exportData(startDate: Date, endDate: Date)

    abstract fun showAllRecords()
}