package com.mt.mt166demo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.SpinnerAdapter
import java.util.Vector
import androidx.core.content.edit


class ConnDialog(var mContext: Context) : Dialog(mContext) {
    var mCloseListener: OnCloseListener? = null
    var mSpinBaudRates: Spinner? = null
    var mSpinPorts: Spinner? = null

    fun interface OnCloseListener {
        fun onClose(z: Boolean)
    }

    fun setCloseListener(listener: OnCloseListener?) {
        this.mCloseListener = listener
    }

    // android.app.Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conn)
        setTitle(MainActivity.Companion.instance!!.getString(R.string.deviceconnect))
        setCancelable(false)
        this.mSpinPorts = findViewById<View?>(R.id.spinPorts) as Spinner
        this.mSpinBaudRates = findViewById<View?>(R.id.spinBaudRates) as Spinner
        val finder = SerialPortFinder()
        val ports = finder.allPorts
        if (ports.isNotEmpty()) {
            val adapter1 = ArrayAdapter<SerialPortItem?>(
                this.mContext,
                R.layout.support_simple_spinner_dropdown_item,
                ports
            )
            this.mSpinPorts!!.adapter = adapter1 as SpinnerAdapter
        } else {
            val ports2 = Vector<SerialPortItem?>()
            ports2.add(SerialPortItem("COM0", "/dev/ttyS0"))
            ports2.add(SerialPortItem("COM1", "/dev/ttyS1"))
            ports2.add(SerialPortItem("COM2", "/dev/ttyS2"))
            ports2.add(SerialPortItem("COM3", "/dev/ttyS3"))
            ports2.add(SerialPortItem("COM4", "/dev/ttyS4"))
            val adapter12 = ArrayAdapter<SerialPortItem?>(
                this.mContext,
                R.layout.support_simple_spinner_dropdown_item,
                ports2
            )
            this.mSpinPorts!!.adapter = adapter12 as SpinnerAdapter
        }
        val baudRates = Vector<String?>()
        baudRates.add("9600")
        baudRates.add("19200")
        baudRates.add("38400")
        baudRates.add("57600")
        baudRates.add("115200")
        val adapter2 = ArrayAdapter<String?>(
            this.mContext,
            R.layout.support_simple_spinner_dropdown_item,
            baudRates
        )
        this.mSpinBaudRates!!.adapter = adapter2 as SpinnerAdapter
        val sp = this.mContext.getSharedPreferences("conn_pref", 0)
        val lastPortName: String = sp.getString("PortName", "")!!
        var i = 0
        while (true) {
            if (i >= this.mSpinPorts!!.count) {
                break
            }
            if (!(this.mSpinPorts!!.getItemAtPosition(i) as SerialPortItem).name
                    .equals(lastPortName, ignoreCase = true)
            ) {
                i++
            } else {
                this.mSpinPorts!!.setSelection(i)
                break
            }
        }
        var i2 = 0
        while (true) {
            if (i2 >= this.mSpinBaudRates!!.count) {
                break
            }
            if ((this.mSpinBaudRates!!.getItemAtPosition(i2) as String?)!!.toInt() != 9600) {
                i2++
            } else {
                this.mSpinBaudRates!!.setSelection(i2)
                break
            }
        }
        val okButton = findViewById<View?>(R.id.btnOK) as Button
        okButton.setOnClickListener {
            try {
                val port = this@ConnDialog.mSpinPorts!!.selectedItem as SerialPortItem
                val baudRate = this@ConnDialog.mSpinBaudRates!!.selectedItem as String
                Application.cr.MtConnect(port.fullPath, baudRate.toInt(), 0)
                this@ConnDialog.dismiss()
                val sp2 = this@ConnDialog.mContext.getSharedPreferences("conn_pref", 0)
                sp2.edit {
                    putString("PortName", port.name)
                    putInt("BaudRate", baudRate.toInt())
                }
                if (this@ConnDialog.mCloseListener != null) {
                    this@ConnDialog.mCloseListener!!.onClose(true)
                }
            } catch (e: Exception) {
                Application.showError(e.message, this@ConnDialog.mContext)
            }
        }
        val cancelButton = findViewById<View?>(R.id.btnCancel) as Button
        cancelButton.setOnClickListener {
            try {
                this@ConnDialog.dismiss()
                if (this@ConnDialog.mCloseListener != null) {
                    this@ConnDialog.mCloseListener!!.onClose(false)
                }
            } catch (e: Exception) {
                Application.showError(e.message, this@ConnDialog.mContext)
            }
        }
    }
}
