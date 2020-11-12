package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import com.mmoson9.epilepsynote.mvp.interactor.SharedPrefsInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.FirestoreInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.FirestoreInteractorImpl
import com.mmoson9.epilepsynote.mvp.interactor.impl.Seizure
import com.mmoson9.epilepsynote.mvp.interactor.impl.SharedPrefsInteractorImpl
import com.mmoson9.epilepsynote.mvp.view.CalendarView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarPresenterImpl(
    val interactorDB: FirestoreInteractor = FirestoreInteractorImpl()
) :
    CalendarPresenter(), SharedPrefsInteractor.onIDRequest,
    FirestoreInteractor.onSeizureListRequest {

    private var view: CalendarView? = null
    lateinit var interactorSP: SharedPrefsInteractor
    lateinit var context: Context
    lateinit var userID: String
    var pickedDate: Date? = Date()
    var seizureList = ArrayList<Seizure>()
    var init = true
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun attachView(view: CalendarView) {
        this.view = view
    }

    override fun checkUserID(context: Context) {
        this.context = context
        init = true
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
            pickedDate = formatter.parse(formatter.format(pickedDate))
            filterDate(pickedDate)
        }
    }

    override fun filterDate(pickedDate: Date?) {
        val tempList = ArrayList<Seizure>()
        for (seiz in seizureList) {
            val tempDate = formatter.parse(seiz.date.substring(6))
            if (init) {
                view?.setEvent(tempDate.time)
            }
            if (tempDate.time == pickedDate?.time) {
                tempList.add(seiz)
            }
        }
        init = false
        view?.setAdapter(tempList)
    }
}