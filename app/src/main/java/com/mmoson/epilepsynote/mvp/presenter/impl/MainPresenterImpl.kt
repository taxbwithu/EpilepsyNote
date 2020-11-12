package com.mmoson9.epilepsynote.mvp.presenter.impl

import android.content.Context
import android.util.Log
import com.mmoson9.epilepsynote.mvp.interactor.SharedPrefsInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.FirestoreInteractor
import com.mmoson9.epilepsynote.mvp.interactor.impl.FirestoreInteractorImpl
import com.mmoson9.epilepsynote.mvp.interactor.impl.Seizure
import com.mmoson9.epilepsynote.mvp.interactor.impl.SharedPrefsInteractorImpl
import com.mmoson9.epilepsynote.mvp.view.MainView
import java.text.SimpleDateFormat
import java.util.*

class MainPresenterImpl(val interactorDB: FirestoreInteractor = FirestoreInteractorImpl()) :
    MainPresenter(), SharedPrefsInteractor.onIDRequest, FirestoreInteractor.onSeizureListRequest,
    SharedPrefsInteractor.onSettingsRequest {

    private var view: MainView? = null
    lateinit var interactorSP: SharedPrefsInteractor
    lateinit var context: Context
    lateinit var userID: String
    lateinit var requiredSerieInc: String
    lateinit var requiredSerieMin: String
    val formatter = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())

    override fun attachView(view: MainView) {
        this.view = view
    }

    override fun checkUserID(context: Context) {
        this.context = context
        interactorSP = SharedPrefsInteractorImpl(context)
        interactorSP.checkUserID(this)
    }

    override fun onSharedPrefsIDReturn(userID: String) {
        Log.d("TAG", "userID: " + userID)
        view?.userIDSetter(userID)
        this.userID = userID
        if (userID.isNotEmpty()) {
            interactorSP.getCurrentSettings(this)
        }
    }

    override fun onSettingsReturn(seizureSerie: String, serieNumber: String) {
        this.requiredSerieInc = serieNumber
        this.requiredSerieMin = seizureSerie
        interactorDB.getSeizures(userID, this)
    }

    override fun onSeizureListReturn(seizures: ArrayList<Seizure>) {
        if (seizures.isNotEmpty()) {
            val currentDate = Date()
            val weekAgo = Date(currentDate.time - (1000 * 60 * 60 * 24 * 8))
            var tempSeiz =
                Date(formatter.parse(seizures[0].date).time - (requiredSerieMin.toLong() * 60 * 1000))
            var firstSerie: Date = formatter.parse(seizures[0].date)
            var firstSerieFound = false
            var firstSeizure = true
            var seizInc = 0
            var firstSerieInt = 1
            var weekSerieInt = 0
            for (seiz in seizures) {
                val tempDate = formatter.parse(seiz.date)
                var days = ((currentDate.time - tempDate!!.time) / (1000 * 60 * 60 * 24)).toInt()

                if (tempDate.before(currentDate)) {
                    if (firstSeizure){
                        view?.setLastSeizure(days)
                        firstSeizure = false
                    } else {
                        if (tempDate.after(tempSeiz)) {
                            if (firstSerieInt == 1) firstSerie = formatter.parse(seiz.date)!!
                            firstSerieInt++
                        } else firstSerieInt = 1
                        if (firstSerieInt >= requiredSerieInc.toInt() && !firstSerieFound) {
                            days =
                                ((currentDate.time - firstSerie.time) / (1000 * 60 * 60 * 24)).toInt()
                            view?.setLastSerie(days)
                            firstSerieFound = true
                        }
                        if (firstSerieInt == requiredSerieInc.toInt() && firstSerie.after(weekAgo)) weekSerieInt++
                    }
                    view?.setWeeklySeries(weekSerieInt)
                    tempSeiz =
                        Date(formatter.parse(seiz.date)!!.time - (requiredSerieMin.toLong() * 60 * 1000))
                    if (tempDate.after(weekAgo)) seizInc++
                }
            }
            view?.setWeeklySeizures(seizInc)
        }
    }
}