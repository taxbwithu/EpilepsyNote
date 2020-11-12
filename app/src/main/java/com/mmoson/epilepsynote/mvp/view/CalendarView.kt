package com.mmoson9.epilepsynote.mvp.view

import com.github.sundeepk.compactcalendarview.domain.Event
import com.mmoson9.epilepsynote.mvp.interactor.impl.Seizure

interface CalendarView {
    fun setAdapter(seizures: ArrayList<Seizure>)

    fun noResults()

    fun disconnected()

    fun dateSetter(dateEvent: Event)

    fun setEvent(eventTime: Long)
}