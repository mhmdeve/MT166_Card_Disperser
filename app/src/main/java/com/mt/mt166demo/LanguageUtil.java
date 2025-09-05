package com.mt.mt166demo;

import android.content.res.Configuration;
import android.util.DisplayMetrics;
import java.util.Locale;

/* loaded from: classes2.dex */
public class LanguageUtil {
    public static void set(boolean isEnglish) {
        Configuration configuration = MainActivity.instance.getResources().getConfiguration();
        DisplayMetrics displayMetrics = MainActivity.instance.getResources().getDisplayMetrics();
        if (isEnglish) {
            configuration.locale = Locale.ENGLISH;
        } else {
            configuration.locale = Locale.SIMPLIFIED_CHINESE;
        }
        MainActivity.instance.getResources().updateConfiguration(configuration, displayMetrics);
    }
}
