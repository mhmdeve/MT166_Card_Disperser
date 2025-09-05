package com.mt.mt166demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment

/* Migrated from AppCompatActivity to Fragment */
class ULFragment : Fragment(), View.OnClickListener {
    private var adapterBlock: ArrayAdapter<String?>? = null
    private val listBlock: MutableList<String?> = ArrayList<String?>()

    private var mEdtData: EditText? = null
    private var mSpinBlock: Spinner? = null
    private var mTvResp: TextView? = null

    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_ul, container, false)

        mEdtData = mView!!.findViewById<EditText>(R.id.edtULData)
        mEdtData!!.setText("FF FF FF FF")

        mSpinBlock = mView!!.findViewById<Spinner>(R.id.spinBlockNo)
        mTvResp = mView!!.findViewById<TextView>(R.id.txtApduResp)

        initSpinner()

        mView!!.findViewById<View?>(R.id.btnUlFind).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnRead).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnWrite).setOnClickListener(this)

        return mView
    }

    private fun initSpinner() {
        listBlock.clear()
        for (i in 0..15) {
            listBlock.add(i.toString())
        }
        adapterBlock = ArrayAdapter<String?>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listBlock
        )
        adapterBlock!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpinBlock!!.adapter = adapterBlock
    }

    override fun onClick(view: View) {
        try {
            val bBlockNo = mSpinBlock!!.selectedItemPosition.toByte()
            val viewId = view.id

            if (viewId == R.id.btnRead) {
                val data = Application.cr.MT166_ULRead(bBlockNo)
                val strInfo = Utils.bytesToHexString(data)
                mTvResp!!.text = strInfo
            } else if (viewId == R.id.btnUlFind) {
                val atr = Application.cr.MT166_ULFind()
                val strInfo = Utils.bytesToHexString(atr)
                mTvResp!!.text = strInfo
            } else if (viewId == R.id.btnWrite) {
                val bData = this.rawData
                Application.cr.MT166_ULWrite(bBlockNo, bData)
                mTvResp!!.text = getString(R.string.strSucceed)
            }
        } catch (e: Exception) {
            Application.showError(e.message, requireContext())
        }
    }

    @get:Throws(Exception::class)
    private val rawData: ByteArray
        get() {
            val text = mEdtData!!.text.toString()
            val data = Utils.hexStringToBytes(text)
            if (data == null) {
                throw Exception(getString(R.string.strNoData))
            }
            if (data.size != 4) {
                throw Exception(getString(R.string.strBlockDataErr))
            }
            return data
        }
}
