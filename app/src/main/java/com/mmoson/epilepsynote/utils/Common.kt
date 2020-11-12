package com.mmoson9.epilepsynote.utils

import android.content.Context
import android.util.Log
import com.mmoson.epilepsynote.R
import java.io.File

object Common {
    fun getAppPath(context : Context) : String {
        val dir = File(context.externalMediaDirs[0].toString()
        +File.separator
        +context.resources.getString(R.string.app_name)
        +File.separator)
        if(!dir.exists()){
            dir.mkdir()
        }
        return dir.path
    }
}