package com.mt.mt166demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.mt.mtapi.mtDevice

class BaseFragment : Fragment(), View.OnClickListener {
    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout instead of setContentView
        mView = inflater.inflate(R.layout.fragment_base, container, false)

        // Set click listeners
        mView?.findViewById<View?>(R.id.btnGetVersion)!!.setOnClickListener(this)
        mView?.findViewById<View?>(R.id.btnState)!!.setOnClickListener(this)
        mView?.findViewById<View?>(R.id.btnDisReader)!!.setOnClickListener(this)
        mView?.findViewById<View?>(R.id.btnDisOutlet)!!.setOnClickListener(this)
        mView?.findViewById<View?>(R.id.btnDisEject)!!.setOnClickListener(this)
        mView?.findViewById<View?>(R.id.btnRecycle)!!.setOnClickListener(this)
        mView?.findViewById<View?>(R.id.btnEnableEntry)!!.setOnClickListener(this)

        return mView
    }

    override fun onClick(view: View) {
        try {
            var resultInfo: String? = getString(R.string.strSucceed)
            val btn = mView?.findViewById<Button>(view.id)
            val btnText = btn!!.text.toString()
            val id = view.id

            if (id == R.id.btnDisEject) {
                Application.cr.MT166_Dispense(mtDevice.MT166_M1_KEY_A)
            } else if (id == R.id.btnDisOutlet) {
                Application.cr.MT166_Dispense(49.toByte())
            } else if (id == R.id.btnDisReader) {
                Application.cr.MT166_Dispense(48.toByte())
            } else if (id == R.id.btnEnableEntry) {
                Application.cr.MT166_EnableEntry()
            } else if (id == R.id.btnGetVersion) {
                resultInfo = Application.cr.MT166_GetVersion()
            } else if (id == R.id.btnRecycle) {
                Application.cr.MT166_CardRecycle()
            } else if (id == R.id.btnState) {
                resultInfo = processDeviceStatus()
            }

            // Pass context from fragment
            Application.showInfo(resultInfo, btnText, requireContext())
        } catch (e: Exception) {
            Application.showError(e.message, requireContext())
        }
    }

    @Throws(Exception::class)
    private fun processDeviceStatus(): String {
        val status = Application.cr.MT166_GetState()
            ?: throw Exception("Device status is null (no response from MT166)")

        val statusBuilder = StringBuilder()

        processStatusByte0(status[0], statusBuilder)
        processStatusByte1(status[1], statusBuilder)
        processStatusByte2(status[2], statusBuilder)
        processStatusByte3(status[3], statusBuilder)
        processStatusByte4(status[4], statusBuilder)
        processStatusByte5(status[5], statusBuilder)
        processStatusByte6(status[6], statusBuilder)
        processStatusByte7(status[7], statusBuilder)

        return statusBuilder.toString()
    }

    private fun processStatusByte0(status: Byte, builder: StringBuilder) {
        if (status.toInt() == 48) {
            builder.append(getString(R.string.strNoCardRecycle))
        } else if (status.toInt() == 49) {
            builder.append(getString(R.string.strTimeOutRecycle))
        }
        builder.append("\n")
    }

    private fun processStatusByte1(status: Byte, builder: StringBuilder) {
        if (status.toInt() == 48) {
            // Device OK (no dispense issue)
        } else if (status.toInt() == 49) {
            builder.append(getString(R.string.strDispenseErr))
        }
        builder.append("\n")
    }

    private fun processStatusByte2(status: Byte, builder: StringBuilder) {
        if (status.toInt() == 48) {
            builder.append(getString(R.string.strDeviceOK))
        } else if (status.toInt() == 49) {
            builder.append(getString(R.string.strRecycling))
        }
        builder.append("\n")
    }

    private fun processStatusByte3(status: Byte, builder: StringBuilder) {
        if (status.toInt() == 48) {
            builder.append(getString(R.string.strDeviceOK))
        } else if (status.toInt() == 49) {
            builder.append(getString(R.string.strDispenseing))
        }
        builder.append("\n")
    }

    private fun processStatusByte4(status: Byte, builder: StringBuilder) {
        if (status.toInt() == 48) {
            builder.append(getString(R.string.strBoxOK))
        } else if (status.toInt() == 49) {
            builder.append(getString(R.string.strPreEmpty))
        }
        builder.append("\n")
    }

    private fun processStatusByte5(status: Byte, builder: StringBuilder) {
        if (status.toInt() == 48) {
            builder.append(getString(R.string.strNotInReaderPos))
        } else if (status.toInt() == 49) {
            builder.append(getString(R.string.strInReaderPos))
        }
        builder.append("\n")
    }

    private fun processStatusByte6(status: Byte, builder: StringBuilder) {
        if (status.toInt() == 48) {
            builder.append(getString(R.string.strNotInExitGate))
        } else if (status.toInt() == 49) {
            builder.append(getString(R.string.strInExitGate))
        }
        builder.append("\n")
    }

    private fun processStatusByte7(status: Byte, builder: StringBuilder) {
        if (status.toInt() == 48) {
            builder.append(getString(R.string.strBoxNotEmpty))
        } else if (status.toInt() == 49) {
            builder.append(getString(R.string.strBoxEmpty))
        }
        builder.append("\n")
    }
}
