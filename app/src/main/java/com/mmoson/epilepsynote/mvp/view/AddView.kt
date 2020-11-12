package com.mmoson9.epilepsynote.mvp.view

import com.mmoson9.epilepsynote.mvp.interactor.impl.Date

interface AddView {
    fun dateSetter(pickedDate: Date)

    fun timeSetter(hour: String, minutes: String)

    fun userIDSetter(userID: String)

    fun documentAdded()

    fun documentNotAdded()
}