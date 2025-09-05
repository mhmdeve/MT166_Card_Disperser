package com.mt.mtapi

import android.content.res.Configuration
import android.util.DisplayMetrics
import java.util.Locale


object LanguageUtil {
    fun set(isEnglish: Boolean) {
        val configuration: Configuration = mtDevice.Companion.ctx!!.resources.configuration
        val displayMetrics: DisplayMetrics? =
            mtDevice.Companion.ctx!!.resources.displayMetrics
        if (isEnglish) {
            configuration.locale = Locale.ENGLISH
        } else {
            configuration.locale = Locale.SIMPLIFIED_CHINESE
        }
        mtDevice.Companion.ctx!!.resources.updateConfiguration(configuration, displayMetrics)
    }
}
