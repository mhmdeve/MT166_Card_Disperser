package com.mt.mt166demo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.mt.mtapi.mtDevice
import java.util.Vector

/* Migrated from AppCompatActivity to Fragment */
class Mf1Fragment : Fragment(), View.OnClickListener {
    private var adapterBlock: ArrayAdapter<String?>? = null
    private var adapterSector: ArrayAdapter<String?>? = null
    private var mEdtBlkData: EditText? = null
    private var mEdtKeyData: EditText? = null
    private var mEdtValue: EditText? = null
    private var mSpinBlock: Spinner? = null
    private var mSpinKeyTypes: Spinner? = null
    private var mSpinSector: Spinner? = null

    private val listSector: MutableList<String?> = ArrayList<String?>()
    private val listBlock: MutableList<String?> = ArrayList<String?>()

    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_m1, container, false)

        mSpinSector = mView!!.findViewById<Spinner>(R.id.spinSector)
        mSpinBlock = mView!!.findViewById<Spinner>(R.id.spinblock)
        mSpinKeyTypes = mView!!.findViewById<Spinner>(R.id.spinMf1KeyTypes)
        mEdtKeyData = mView!!.findViewById<EditText>(R.id.edtMf1KeyData)
        mEdtBlkData = mView!!.findViewById<EditText>(R.id.edtMf1RawData)
        mEdtValue = mView!!.findViewById<EditText>(R.id.edtMf1Value)

        mEdtKeyData!!.setText("FF FF FF FF FF FF")
        mEdtValue!!.setText("1")

        // Setup key type spinner
        val vec = Vector<Mf1KeyItem?>()
        vec.add(Mf1KeyItem("Key A", true))
        vec.add(Mf1KeyItem("Key B", false))
        val adapter = ArrayAdapter<Mf1KeyItem?>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item, vec
        )
        mSpinKeyTypes!!.adapter = adapter

        initSpinner()

        return mView
    }

    private fun initSpinner() {
        listSector.clear()
        for (i in 0..38) {
            listSector.add(i.toString())
        }
        adapterSector = ArrayAdapter<String?>(
            requireContext(),
            android.R.layout.simple_spinner_item, listSector
        )
        adapterSector!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpinSector!!.adapter = adapterSector

        listBlock.clear()
        listBlock.add("0")
        listBlock.add("1")
        listBlock.add("2")
        listBlock.add("3")

        adapterBlock = ArrayAdapter<String?>(
            requireContext(),
            android.R.layout.simple_spinner_item, listBlock
        )
        adapterBlock!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpinBlock!!.adapter = adapterBlock

        // Register click listeners
        mView!!.findViewById<View?>(R.id.btnMf1Select).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnMf1ReadUID).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnMf1Auth).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnMf1Update).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnMf1Read).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnMf1Write).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnMf1IniValue).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnMf1ReadValue).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnMf1Increment).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnMf1Decrement).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        try {
            val strMsg = getString(R.string.strSucceed)
            val btn = mView!!.findViewById<Button>(view.id)
            val btnText = btn.text.toString()
            val bSector = mSpinSector!!.selectedItemPosition.toByte()
            val bBlock = mSpinBlock!!.selectedItemPosition.toByte()
            val keyData = this.keyData

            val viewId = view.id

            if (viewId == R.id.btnMf1Auth) {
                val isKeyA = (mSpinKeyTypes!!.selectedItem as Mf1KeyItem).isKeyA
                val bKey = if (isKeyA) mtDevice.MT166_M1_KEY_A else mtDevice.MT166_M1_KEY_B
                Application.cr.MT166_M1PasswordVerify(bKey, bSector, keyData)
                Application.showInfo(strMsg, btnText, requireContext())
            } else if (viewId == R.id.btnMf1Decrement) {
                val values = this.value
                Application.cr.MT166_M1Decrement(bSector.toInt(), bBlock.toInt(), values)
                Application.showInfo(strMsg, btnText, requireContext())
            } else if (viewId == R.id.btnMf1Increment) {
                val values = this.value
                Application.cr.MT166_M1Increase(bSector.toInt(), bBlock.toInt(), values)
                Application.showInfo(strMsg, btnText, requireContext())
            } else if (viewId == R.id.btnMf1IniValue) {
                val values = this.value
                Application.cr.MT166_M1InitValue(bSector, bBlock, values)
                Application.showInfo(strMsg, btnText, requireContext())
            } else if (viewId == R.id.btnMf1Read) {
                val bBlockData = Application.cr.MT166_M1ReadBlock(bSector, bBlock)
                val strInfo = Utils.bytesToHexString(bBlockData)
                mEdtBlkData!!.setText(strInfo)
            } else if (viewId == R.id.btnMf1ReadUID) {
                val data = Application.cr.MT166_M1GetSerial()
                val strInfo = Utils.bytesToHexString(data)
                Application.showInfo(strInfo, btnText, requireContext())
            } else if (viewId == R.id.btnMf1ReadValue) {
                val values = Application.cr.MT166_M1ReadValue(bSector, bBlock)
                val strInfo = values.toString()
                Log.d("Mf1Fragment", strInfo)
                mEdtValue!!.setText(strInfo)
            } else if (viewId == R.id.btnMf1Select) {
                Application.cr.MT166_M1FindCard()
                Application.showInfo(strMsg, btnText, requireContext())
            } else if (viewId == R.id.btnMf1Update) {
                Application.cr.MT166_M1UpdatePassword(bSector, keyData)
                Application.showInfo(strMsg, btnText, requireContext())
            } else if (viewId == R.id.btnMf1Write) {
                val bWriteData = this.rawData
                Application.cr.MT166_M1WriteBlock(bSector, bBlock, bWriteData)
                Application.showInfo(strMsg, btnText, requireContext())
            }
        } catch (e: Exception) {
            Application.showError(e.message, requireContext())
        }
    }

    @get:Throws(Exception::class)
    private val keyData: ByteArray
        get() {
            val text = mEdtKeyData!!.text.toString()
            val data = Utils.hexStringToBytes(text)
            if (data == null) throw Exception(getString(R.string.strNoInputKey))
            if (data.size != 6) throw Exception(getString(R.string.strKeyerr))
            return data
        }

    @get:Throws(Exception::class)
    private val rawData: ByteArray
        get() {
            val text = mEdtBlkData!!.text.toString()
            val data = Utils.hexStringToBytes(text)
            if (data == null) throw Exception(getString(R.string.strNoData))
            if (data.size != 16) throw Exception(getString(R.string.strBlockDataErr))
            return data
        }

    @get:Throws(Exception::class)
    private val value: Int
        get() {
            val text = mEdtValue!!.text.toString().replace(" ".toRegex(), "")
            if (text.isEmpty()) throw Exception(getString(R.string.strNoData))
            return text.toInt()
        }
}
