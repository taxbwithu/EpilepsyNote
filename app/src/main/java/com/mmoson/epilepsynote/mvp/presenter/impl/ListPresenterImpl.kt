package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import android.util.Log
import com.mmoson9.epilepsynote.mvp.interactor.SharedPrefsInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.*
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.view.ListView
import java.util.*
import kotlin.collections.ArrayList

class ListPresenterImpl(
    val interactorDB: FirestoreInteractor = FirestoreInteractorImpl()
) :
    ListPresenter(), SharedPrefsInteractor.onIDRequest, FirestoreInteractor.onSeizureListRequest {

    private var view: ListView? = null
    lateinit var interactorSP: SharedPrefsInteractor
    lateinit var context: Context
    lateinit var userID: String
    var init = false
    var startDate: Date = Date("")
    var endDate: Date = Date("")
    var seizureList = ArrayList<Seizure>()
    var showAllRecords: Boolean = false
    lateinit var c: Calendar

    override fun attachView(view: ListView) {
        this.view = view
    }

    override fun checkUserID(context: Context) {
        this.context = context
        interactorSP = SharedPrefsInteractorImpl(context)
        interactorSP.checkUserID(this)
    }

    override fun onSharedPrefsIDReturn(userID: String) {
        this.userID = userID
        if (userID.isBlank()) {
            view?.noResults()
        } else {
            interactorDB.getSeizures(userID, this)
        }

    }

    override fun onSeizureListReturn(seizures: ArrayList<Seizure>) {
        if (seizures.isEmpty()) {
            view?.noResults()
        } else {
            this.seizureList = seizures
            filterDates(startDate, endDate)
        }
    }

    override fun setDate(day: Int, month: Int, year: Int, sDate: Boolean) {
        c = Calendar.getInstance()
        if (day == 50) {
            startDate.setCurrent(1)
            endDate.setCurrent(0)
            init = true
        } else {
            c.set(Calendar.DAY_OF_MONTH, day)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.YEAR, year)
            val mDayName =
                c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            if (sDate) {
                startDate = Date(mDayName, day.toString(), month.toString(), year.toString())
                showAllRecords = false
            } else {
                endDate = Date(mDayName, day.toString(), month.toString(), year.toString())
                init = false
                showAllRecords = false
            }
        }
        view?.dateSetter(startDate, endDate, init)
    }

    override fun filterDates(startDate: Date, endDate: Date) {
        val tempList = ArrayList<Seizure>()
        for (seiz in seizureList) {
            var compDate = Date("")
            compDate.setFromString(seiz.date)
            if (init) {
                if (!compDate.dateType.before(startDate.dateType)) {
                    tempList.add(seiz)
                }
            } else {
                if (!compDate.dateType.before(startDate.dateType) && (!compDate.dateType.after(
                        endDate.dateType
                    ))
                ) {
                    tempList.add(seiz)
                }
            }
        }
        if (!showAllRecords) seizureList = tempList
        else {
            val tempDate = Date("")
            tempDate.setFromString(seizureList[0].date)
            this.endDate = tempDate
            tempDate.setFromString(seizureList.last().date)
            this.startDate = tempDate
        }
        view?.setAdapter(seizureList)
    }

    override fun showAllRecords() {
        showAllRecords = true
    }
}