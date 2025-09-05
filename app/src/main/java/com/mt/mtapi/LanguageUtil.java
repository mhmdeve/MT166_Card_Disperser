package com.mt.mtapi;

import android.content.res.Configuration;
import android.util.DisplayMetrics;
import java.util.Locale;

/* loaded from: classes3.dex */
public class LanguageUtil {
    public static void set(boolean isEnglish) {
        Configuration configuration = mtDevice.ctx.getResources().getConfiguration();
        DisplayMetrics displayMetrics = mtDevice.ctx.getResources().getDisplayMetrics();
        if (isEnglish) {
        } else {
            configuration.locale = Locale.SIMPLIFIED_CHINESE;
        }
        mtDevice.ctx.getResources().updateConfiguration(configuration, displayMetrics);
    }
}
