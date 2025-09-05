package com.mt.mt166demo;

import androidx.annotation.NonNull;

/* loaded from: classes2.dex */
public class SerialPortItem {
    private String _fullPath;
    private String _name;

    protected SerialPortItem(String name, String fullPath) {
        this._name = name;
        this._fullPath = fullPath;
    }

    public String getName() {
        return this._name;
    }

    public String getFullPath() {
        return this._fullPath;
    }

    @NonNull
    public String toString() {
        return this._name;
    }
}
