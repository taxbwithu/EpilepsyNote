package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import android.util.Log
import com.mmoson9.epilepsynote.mvp.interactor.SharedPrefsInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.*
import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.view.ExportView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExportPresenterImpl(
    val interactorDB: FirestoreInteractor = FirestoreInteractorImpl()
) :
    ExportPresenter(), SharedPrefsInteractor.onIDRequest, SharedPrefsInteractor.onSettingsRequest,
    FirestoreInteractor.onSeizureListRequest {

    private var view: ExportView? = null
    lateinit var interactorSP: SharedPrefsInteractor
    lateinit var context: Context
    lateinit var userID: String
    lateinit var requiredSerieInc: String
    lateinit var requiredSerieMin: String
    var init = false
    var startDate: Date = Date("")
    var endDate: Date = Date("")
    var seizureList = ArrayList<Seizure>()
    var showAllRecords: Boolean = false
    val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
    lateinit var c: Calendar

    override fun attachView(view: ExportView) {
        this.view = view
    }

    override fun checkUserID(context: Context) {
        this.context = context
        interactorSP = SharedPrefsInteractorImpl(context)
        interactorSP.checkUserID(this)
    }

    override fun onSharedPrefsIDReturn(userID: String) {
        this.userID = userID
        interactorSP.getCurrentSettings(this)
    }

    override fun onSettingsReturn(seizureSerie: String, serieNumber: String) {
        this.requiredSerieMin = seizureSerie
        this.requiredSerieInc = serieNumber
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

    override fun exportData(startDate: Date, endDate: Date) {
        this.startDate = startDate
        this.endDate = endDate
        if (userID.isNotEmpty()){
            interactorDB.getSeizures(userID, this)
        }
        else{
            view?.noUserID()
        }
    }

    override fun onSeizureListReturn(seizures: ArrayList<Seizure>) {
        val tempList = ArrayList<Seizure>()
        var allSeizures = 0
        var smallSeizures = 0
        var mediumSeizures = 0
        var bigSeizures = 0
        var allSeries = 0
        var serieInc = 1
        var prevSeizure = java.util.Date()
        if (showAllRecords) {
            val tempStartDate = Date("")
            val tempEndDate = Date("")
            tempEndDate.setFromString(seizures[0].date)
            endDate = tempEndDate
            tempStartDate.setFromString(seizures.last().date)
            startDate = tempStartDate
            view?.dateSetter(this.startDate, this.endDate, false)
            showAllRecords = false
        } else {
            if (seizures.isNotEmpty()) {
                for (seiz in seizures) {
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
                seizureList = tempList

                for (seiz in seizureList) {
                    var currSeizure = java.util.Date(formatter.parse(seiz.date).time)
                    allSeizures++
                    if (seiz.size == "Mały") smallSeizures++
                    else if (seiz.size == "Średni") mediumSeizures++
                    else if (seiz.size == "Duży") bigSeizures++
                    if (seiz != seizureList[0] && currSeizure.after(prevSeizure)) {
                        serieInc++
                    } else {
                        if (serieInc >= requiredSerieInc.toInt()) {
                            allSeries++
                        }
                        serieInc = 1
                    }
                    prevSeizure =
                        java.util.Date(formatter.parse(seiz.date).time - (requiredSerieMin.toLong() * 60 * 1000))
                }
                if (serieInc >= requiredSerieInc.toInt()) {
                    allSeries++
                }
            }
            view?.statsSetter(allSeizures, smallSeizures, mediumSeizures, bigSeizures, allSeries)
            view?.createPDFFile(seizureList)
        }
    }

    override fun showAllRecords() {
        showAllRecords = true
        if(userID.isNotEmpty()){
            interactorDB.getSeizures(userID,this)
        }
    }
}