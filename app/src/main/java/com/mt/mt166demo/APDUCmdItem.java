package com.mt.mt166demo;

/* loaded from: classes2.dex */
public class APDUCmdItem {
    byte[] mCmdData;
    String mName;

    protected APDUCmdItem(String name, byte[] cmdData) {
        this.mName = name;
        this.mCmdData = cmdData;
    }

    public String getName() {
        return this.mName;
    }

    public byte[] getCmdData() {
        return this.mCmdData;
    }

    public String toString() {
        return this.mName + "\t{ " + Utils.bytesToHexString(this.mCmdData) + " }";
    }
}
