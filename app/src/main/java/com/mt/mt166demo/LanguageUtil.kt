package com.mt.mt166demo

import android.content.res.Configuration
import android.util.DisplayMetrics
import java.util.Locale


object LanguageUtil {
    fun set(isEnglish: Boolean) {
        val configuration: Configuration =
            MainActivity.Companion.instance!!.getResources().configuration
        val displayMetrics: DisplayMetrics? =
            MainActivity.Companion.instance!!.getResources().displayMetrics
        if (isEnglish) {
            configuration.locale = Locale.ENGLISH
        } else {
            configuration.locale = Locale.SIMPLIFIED_CHINESE
        }
        MainActivity.Companion.instance!!.getResources()
            .updateConfiguration(configuration, displayMetrics)
    }
}
