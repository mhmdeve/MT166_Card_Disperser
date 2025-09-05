package com.mt.mt166demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment

/* Migrated from AppCompatActivity to Fragment */
class TypeABFragment : Fragment(), View.OnClickListener {
    private var typeAdapter: ArrayAdapter<String?>? = null
    private val typeList: MutableList<String?> = ArrayList<String?>()

    private var mSpinCmds: Spinner? = null
    private var mSpinTypeAB: Spinner? = null
    private var mTvATR: TextView? = null
    private var mTvResp: TextView? = null

    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_typeab, container, false)

        mSpinCmds = mView!!.findViewById<Spinner>(R.id.spinApduCmds)
        mSpinTypeAB = mView!!.findViewById<Spinner>(R.id.spinType)
        mTvATR = mView!!.findViewById<TextView>(R.id.txtATR)
        mTvResp = mView!!.findViewById<TextView>(R.id.txtApduResp)

        mTvATR!!.text = ""

        // Load APDU command list
        val vec = Application.predCmdItems
        val adapter = ArrayAdapter<APDUCmdItem?>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            vec
        )
        mSpinCmds!!.adapter = adapter

        initSpinner()

        // Click listeners
        mView!!.findViewById<View?>(R.id.btnActive).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnDeselect).setOnClickListener(this)
        mView!!.findViewById<View?>(R.id.btnTransmit).setOnClickListener(this)

        return mView
    }

    private fun initSpinner() {
        typeList.clear()
        typeList.add("TypeA")
        typeList.add("TypeB")

        typeAdapter = ArrayAdapter<String?>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            typeList
        )
        typeAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpinTypeAB!!.adapter = typeAdapter
    }

    override fun onClick(view: View) {
        try {
            val btn = mView!!.findViewById<Button>(view.id)
            btn.text.toString()

            val bType = (mSpinTypeAB!!.selectedItemPosition + 65).toByte() // 65 = 'A'

            val id = view.id
            if (id == R.id.btnActive) {
                val atr = Application.cr.MT166_TypeCpuActive(bType)
                val strInfo = Utils.bytesToHexString(atr!!)
                mTvATR!!.text = strInfo
            } else if (id == R.id.btnDeselect) {
                Application.cr.MT166_TypeCpuDeselect()
                mTvResp!!.text = getString(R.string.strCpuDeselect)
            } else if (id == R.id.btnTransmit) {
                val cmd = mSpinCmds!!.selectedItem as APDUCmdItem
                val rdata = Application.cr.MT166_TypeCpuTransmit(bType, cmd.cmdData)
                val strInfo = Utils.bytesToHexString(rdata!!)
                mTvResp!!.text = strInfo
            }
        } catch (e: Exception) {
            Application.showError(e.message, requireContext())
        }
    }
}
