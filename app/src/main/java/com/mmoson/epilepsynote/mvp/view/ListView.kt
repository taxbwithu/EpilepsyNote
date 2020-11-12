package com.mmoson9.epilepsynote.mvp.view

import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.interactor.impl.Seizure

interface ListView {
    fun setAdapter(seizures: ArrayList<Seizure>)

    fun noResults()

    fun disconnected()

    fun dateSetter(startDate: Date, endDate: Date, init: Boolean)
}