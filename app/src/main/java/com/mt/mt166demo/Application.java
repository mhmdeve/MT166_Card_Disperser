package com.mt.mt166demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.mt.mtapi.mtDevice;
import java.util.Vector;

/* loaded from: classes2.dex */
public class Application {
    static mtDevice.RDATA resData = new mtDevice.RDATA();
    static mtDevice cr = new mtDevice(MainActivity.instance);
    static final APDUCmdItem APDU1 = new APDUCmdItem("APDU1", new byte[]{0, -124, 0, 0, 8});
    static final APDUCmdItem APDU2 = new APDUCmdItem("APDU2", new byte[]{0, -92, 4, 0, 7, -96, 0, 0, 3, 51, 1, 1});
    static final APDUCmdItem APDU3 = new APDUCmdItem("APDU3", new byte[]{Byte.MIN_VALUE, -88, 0, 0, 11, -125, 9, 0, 0, 0, 0, 0, 0, 0, 1, 86});
    static final APDUCmdItem APDU4 = new APDUCmdItem("APDU4", new byte[]{0, -78, 1, 12, 0});
    static final APDUCmdItem APDU5 = new APDUCmdItem("APDU5", new byte[]{0, -78, 2, 12, 0});

    public static void showError(String strMsg, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(MainActivity.instance.getString(R.string.strError));
        builder.setMessage(strMsg);
        builder.setPositiveButton(MainActivity.instance.getString(R.string.ok), (DialogInterface.OnClickListener) null);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
    }

    public static void showInfo(String strMsg, String strTitle, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(strTitle);
        builder.setMessage(strMsg);
        builder.setPositiveButton(MainActivity.instance.getString(R.string.ok), (DialogInterface.OnClickListener) null);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.show();
    }

    public static Vector<APDUCmdItem> getPredCmdItems() {
        Vector<APDUCmdItem> cmds = new Vector<>();
        cmds.add(APDU1);
        cmds.add(APDU2);
        cmds.add(APDU3);
        cmds.add(APDU4);
        cmds.add(APDU5);
        return cmds;
    }
}
