package com.mt.mt166demo

import android.util.Log
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.LineNumberReader
import java.util.Vector


class SerialPortFinder {
    private var mDrivers: Vector<Driver?>? = null

    class Driver(name: String?, root: String) {
        private val mDeviceRoot: String
        var mDevices: Vector<File?>? = null
        private val mDriverName: String?

        init {
            Log.i("chw", "SerialPortFinder ---> Driver")
            this.mDriverName = name
            this.mDeviceRoot = root
        }

        val devices: Vector<File?>?
            get() {
                Log.i("chw", "SerialPortFinder ---> getDevices")
                if (this.mDevices == null) {
                    this.mDevices = Vector<File?>()
                    val dev = File("/dev")
                    val files = dev.listFiles()
                    if (files != null) {
                        for (i in files.indices) {
                            if (files[i]!!.absolutePath.startsWith(this.mDeviceRoot)) {
                                Log.d(
                                    TAG,
                                    "Found new device: " + files[i]
                                )
                                this.mDevices!!.add(files[i])
                            }
                        }
                    }
                }
                return this.mDevices
            }

        val name: String?
            get() {
                Log.i("chw", "SerialPortFinder ---> getName")
                return this.mDriverName
            }
    }

    @get:Throws(IOException::class)
    val drivers: Vector<Driver?>?
        get() {
            Log.i("chw", "SerialPortFinder ---> getDrivers")
            if (this.mDrivers == null) {
                this.mDrivers = Vector<Driver?>()
                val r =
                    LineNumberReader(FileReader("/proc/tty/drivers"))
                while (true) {
                    val l = r.readLine()
                    if (l == null) {
                        break
                    }
                    val w: Array<String?> =
                        l.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (w.size == 5 && w[4] == "serial") {
                        Log.d(
                            TAG,
                            "Found new driver: " + w[1]
                        )
                        this.mDrivers!!.add(Driver(w[0], w[1]!!))
                    }
                }
                r.close()
            }
            return this.mDrivers
        }

    val allPorts: Vector<SerialPortItem?>
        get() {
            val ports = Vector<SerialPortItem?>()
            try {
                val itdriv =
                    this.drivers!!.iterator()
                while (itdriv.hasNext()) {
                    val driver = itdriv.next()
                    val itdev =
                        driver!!.devices!!.iterator()
                    while (itdev.hasNext()) {
                        val file = itdev.next()
                        val name =
                            String.format("%s (%s)", file!!.name, driver.name)
                        val fullPath = file.absolutePath
                        ports.add(SerialPortItem(name, fullPath))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return ports
        }

    companion object {
        private const val TAG = "SerialPort"
    }
}
