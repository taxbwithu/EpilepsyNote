package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.interactor.SharedPrefsInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.*
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.view.SeizureEditView
import java.text.SimpleDateFormat
import java.util.*

class SeizureEditPresenterImpl(val interactorDB: FirestoreInteractor = FirestoreInteractorImpl()) :
    SeizureEditPresenter(), SharedPrefsInteractor.onIDRequest,
    FirestoreInteractor.onSeizureRequest, FirestoreInteractor.onActionDone {

    private var view: SeizureEditView? = null
    lateinit var interactorSP: SharedPrefsInteractor
    lateinit var seizureID: String
    lateinit var context: Context
    lateinit var c: Calendar
    lateinit var pickedDate: Date
    lateinit var mHour: String
    lateinit var mMinute: String

    override fun attachView(view: SeizureEditView) {
        this.view = view
    }

    override fun checkUserID(context: Context) {
        this.context = context
        interactorSP = SharedPrefsInteractorImpl(context)
        interactorSP.checkUserID(this)
    }

    override fun onSharedPrefsIDReturn(userID: String) {
        view?.userIDSetter(userID)
        interactorDB.getSingleSeizure(userID, seizureID, this)
    }

    override fun getData(seizureID: String) {
        this.seizureID = seizureID
    }

    override fun setDate(day: Int, month: Int, year: Int) {
        var date = Date("", "", "", "")
        c = Calendar.getInstance()

        c.set(Calendar.DAY_OF_MONTH, day)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.YEAR, year)

        val mDayName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        date = Date(mDayName, day.toString(), month.toString(), year.toString())

        this.pickedDate = date
        view?.dateSetter(date)
    }

    override fun setTime(hour: Int, minute: Int) {
        var mHour = hour
        var mMinute = minute

        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.MINUTE, minute)

        this.mHour = sizeCheck(mHour.toString())
        this.mMinute = sizeCheck(mMinute.toString())
        view?.timeSetter(this.mHour, this.mMinute)
    }

    override fun updateData(
        userID: String,
        size: String,
        description: String
    ) {
        val dateTime = mHour + ":" + mMinute + " " + pickedDate.printPart()
        val dateFormat = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).parse(dateTime)
        val timeStamp = dateFormat?.time.toString()
        val seizure = Seizure("", dateTime, size, description, timeStamp)
        interactorDB.updateSeizure(userID, seizureID, seizure, this)
    }

    override fun deleteData(userID: String) {
        interactorDB.deleteSeizure(userID, seizureID, this)
    }

    override fun onSeizureReturn(seizure: Seizure) {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
        c.time = sdf.parse(seizure.date)!!
        setDate(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR))
        setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
        view?.pickerListeners(seizure.size, seizure.description)
    }

    fun sizeCheck(value: String): String {
        if (value.length == 1) {
            return "0" + value
        } else return value
    }

    override fun onActionDone(docExists : Boolean) {
        if (!docExists) {
            view?.documentDeleted()
        } else {
            view?.documentUpdated()
        }

    }
}