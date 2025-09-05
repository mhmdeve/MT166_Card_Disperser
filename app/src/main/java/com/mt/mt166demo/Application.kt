package com.mt.mt166demo

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.mt.mtapi.mtDevice
import com.mt.mtapi.mtDevice.RDATA
import java.util.Vector


object Application {
    var resData: RDATA = RDATA()
    var cr: mtDevice = mtDevice(MainActivity.Companion.instance)
    val APDU1: APDUCmdItem = APDUCmdItem("APDU1", byteArrayOf(0, -124, 0, 0, 8))
    val APDU2: APDUCmdItem =
        APDUCmdItem("APDU2", byteArrayOf(0, -92, 4, 0, 7, -96, 0, 0, 3, 51, 1, 1))
    val APDU3: APDUCmdItem = APDUCmdItem(
        "APDU3",
        byteArrayOf(Byte.Companion.MIN_VALUE, -88, 0, 0, 11, -125, 9, 0, 0, 0, 0, 0, 0, 0, 1, 86)
    )
    val APDU4: APDUCmdItem = APDUCmdItem("APDU4", byteArrayOf(0, -78, 1, 12, 0))
    val APDU5: APDUCmdItem = APDUCmdItem("APDU5", byteArrayOf(0, -78, 2, 12, 0))

    fun showError(strMsg: String?, context: Context?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(MainActivity.Companion.instance?.getString(R.string.strError))
        builder.setMessage(strMsg)
        builder.setPositiveButton(
            MainActivity.Companion.instance?.getString(R.string.ok),
            null as DialogInterface.OnClickListener?
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.show()
    }

    fun showInfo(strMsg: String?, strTitle: String?, context: Context?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(strTitle)
        builder.setMessage(strMsg)
        builder.setPositiveButton(
            MainActivity.Companion.instance?.getString(R.string.ok),
            null as DialogInterface.OnClickListener?
        )
        builder.setIcon(android.R.drawable.ic_dialog_info)
        builder.show()
    }

    val predCmdItems: Vector<APDUCmdItem?>
        get() {
            val cmds = Vector<APDUCmdItem?>()
            cmds.add(APDU1)
            cmds.add(APDU2)
            cmds.add(APDU3)
            cmds.add(APDU4)
            cmds.add(APDU5)
            return cmds
        }
}
