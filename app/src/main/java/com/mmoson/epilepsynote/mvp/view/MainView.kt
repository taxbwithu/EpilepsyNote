package com.mmoson9.epilepsynote.mvp.view

interface MainView {
    fun userIDSetter(userID: String)

    fun setLastSeizure(days : Int)

    fun setWeeklySeizures(days: Int)

    fun setLastSerie(days: Int)

    fun setWeeklySeries(days: Int)
}