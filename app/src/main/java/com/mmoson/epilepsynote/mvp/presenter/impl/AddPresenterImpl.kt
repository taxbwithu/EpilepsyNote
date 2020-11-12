package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.interactor.SharedPrefsInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.*
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.view.AddView
import java.text.SimpleDateFormat
import java.util.*

class AddPresenterImpl(val interactorDB: FirestoreInteractor = FirestoreInteractorImpl()) :
    AddPresenter(), SharedPrefsInteractor.onIDRequest, FirestoreInteractor.onUserIDRequest,
    FirestoreInteractor.onActionDone {

    private var view: AddView? = null
    lateinit var interactorSP: SharedPrefsInteractor
    lateinit var context: Context
    lateinit var c: Calendar
    lateinit var pickedDate: Date
    lateinit var mHour: String
    lateinit var mMinute: String

    override fun attachView(view: AddView) {
        this.view = view
    }

    override fun checkUserID(context: Context) {
        this.context = context
        interactorSP = SharedPrefsInteractorImpl(context)
        interactorSP.checkUserID(this)
    }

    override fun onSharedPrefsIDReturn(userID: String) {
        if (userID.isEmpty()){
            interactorDB.initUser(this)
        }
        view?.userIDSetter(userID)
    }

    override fun onUserIDReturn(userID: String?) {
        if (!userID.isNullOrEmpty()) {
            view?.userIDSetter(userID)
        }
        interactorSP.setUserID(userID)
    }

    override fun onActionDone(docExists: Boolean) {
            if (docExists){
                view?.documentAdded()
            }
            else view?.documentNotAdded()
    }

    override fun setDate(day: Int, month: Int, year: Int) {
        var date = Date("", "", "", "")
        c = Calendar.getInstance()
        if (day == 50) {
            date.setCurrent(0)
        } else {
            c.set(Calendar.DAY_OF_MONTH, day)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.YEAR, year)
            var mDayName =
                c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            date = Date(mDayName, day.toString(), month.toString(), year.toString())
        }
        this.pickedDate = date
        view?.dateSetter(date)
    }

    override fun setTime(hour: Int, minute: Int) {
        var mHour = hour
        var mMinute = minute

        if (hour == 30) {
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)
        } else {
            c.set(Calendar.HOUR_OF_DAY, hour)
            c.set(Calendar.MINUTE, minute)
        }
        this.mHour = sizeCheck(mHour.toString())
        this.mMinute = sizeCheck(mMinute.toString())
        view?.timeSetter(this.mHour, this.mMinute)
    }

    override fun addData(
        userID: String,
        size: String,
        descr: String,
        countSet: String
    ) {
        val dateTime = mHour + ":" + mMinute + " " + pickedDate.printPart()
        val dateFormat = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).parse(dateTime)
        val timeStamp = dateFormat?.time.toString()
        val seizure = Seizure("", dateTime, size, descr, timeStamp)
        interactorDB.addSeizure(userID, seizure, countSet, this, this)
    }

    fun sizeCheck(value: String): String {
        if (value.length == 1) {
            return "0" + value
        } else return value
    }

    override fun detachView() {
        this.view = null
    }
}