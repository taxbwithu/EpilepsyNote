package com.mmoson9.epilepsynote.mvp.interactor.impl

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

data class Date(var dayName: String?) {
    lateinit var day: String
    lateinit var month: String
    lateinit var year: String
    lateinit var dateType: Date

    constructor(dayName: String, day: String, month: String, year: String) : this(dayName) {
        if (year != "") {
            this.day = sizeCheck(day)
            this.month = sizeCheck(((month.toInt() + 1).toString()))
            this.year = year
            toDateType()
        }
    }

    fun sizeCheck(value: String): String {
        if (value.length == 1) {
            return "0" + value
        } else return value
    }

    fun printAll(): String {
        return dayName + ", " + day + "." + month + "." + year
    }

    fun printPart(): String {
        return day + "." + month + "." + year
    }

    fun setCurrent(decr: Int) {
        val c = Calendar.getInstance()
        this.year = (c.get(Calendar.YEAR) - decr).toString()
        this.month = sizeCheck((c.get(Calendar.MONTH) - decr + 1).toString())
        this.day = sizeCheck(c.get(Calendar.DAY_OF_MONTH).toString())
        this.dayName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        if (this.month == "00") {
            this.month = "12"
        }
        toDateType()
    }

    fun setFromString(date: String) {
        val tempDate = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).parse(date)
        val c = Calendar.getInstance()
        c.time = tempDate
        this.day = c.get(Calendar.DAY_OF_MONTH).toString()
        this.month = (c.get(Calendar.MONTH) + 1).toString()
        this.year = c.get(Calendar.YEAR).toString()
        this.dayName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        toDateType()
    }

    fun toDateType() {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        this.dateType = sdf.parse(printPart())
    }

    fun isBefore(extDateType: Date): Boolean {
        if (dateType.before(extDateType)) {
            Log.d("TAG", "isNotBefore")
            return true
        }
        return false
    }

    fun isAfter(extDateType: Date): Boolean {
        if (dateType.after(extDateType)) {
            Log.d("TAG", "isNotAfter")
            return true
        }
        return false
    }
}