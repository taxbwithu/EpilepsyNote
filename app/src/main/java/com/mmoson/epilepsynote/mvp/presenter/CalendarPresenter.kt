package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.view.CalendarView
import java.util.*

abstract class CalendarPresenter {
    abstract fun attachView(view: CalendarView)

    abstract fun checkUserID(context: Context)

    abstract fun filterDate(pickedDate: Date?)
}