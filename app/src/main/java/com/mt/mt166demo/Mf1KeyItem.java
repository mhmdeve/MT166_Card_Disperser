package com.mt.mt166demo;

/* loaded from: classes2.dex */
class Mf1KeyItem {
    private boolean mIsKeyA;
    private String mName;

    protected Mf1KeyItem(String name, boolean isKeyA) {
        this.mName = name;
        this.mIsKeyA = isKeyA;
    }

    public String getName() {
        return this.mName;
    }

    public boolean isKeyA() {
        return this.mIsKeyA;
    }

    public String toString() {
        return this.mName;
    }
}
