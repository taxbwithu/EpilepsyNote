package com.mmoson9.epilepsynote.mvp.view

import com.mmoson9.epilepsynote.mvp.interactor.impl.Date

interface SeizureEditView {
    fun dateSetter(pickedDate: Date)

    fun timeSetter(hour: String, minutes: String)

    fun pickerListeners(epSize: String, description: String?)

    fun userIDSetter(userID: String)

    fun documentUpdated()

    fun documentDeleted()
}