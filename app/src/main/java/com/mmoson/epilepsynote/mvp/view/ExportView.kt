package com.mmoson9.epilepsynote.mvp.view

import com.mmoson9.epilepsynote.mvp.interactor.impl.Date
import com.mmoson9.epilepsynote.mvp.interactor.impl.Seizure

interface ExportView {
    fun createPDFFile(seizures: ArrayList<Seizure>)

    fun dateSetter(startDate: Date, endDate: Date, init: Boolean)

    fun statsSetter(allSeizures : Int, smallSeizures : Int, mediumSeizures: Int, bigSeizures: Int, allSeries: Int)

    fun noUserID()
}