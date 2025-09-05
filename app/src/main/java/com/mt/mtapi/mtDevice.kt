package com.mt.mtapi

import android.content.Context
import android.util.Log
import androidx.core.view.MotionEventCompat
import com.mt.mt166demo.R
import kotlin.experimental.inv


class mtDevice(context: Context?) {
    class RDATA {
        lateinit var bRDataBuf: ByteArray
        var bState: Byte = 0
        var ilen: Int = 0
    }

    init {
        ctx = context
    }

    @Throws(Exception::class)
    fun CheckValueBlock(bBlockAddress: Byte, Buff: ByteArray): Int {
        if (Buff[0] != Buff[8] || Buff[1] != Buff[9] || Buff[2] != Buff[10] || Buff[3] != Buff[11]) {
            throw Exception(ctx!!.getString(R.string.strDataFormat))
        }
        if (((Buff[0].inv()).toByte()) != Buff[4] || ((Buff[1].inv()).toByte()) != Buff[5] || ((Buff[2].inv()).toByte()) != Buff[6] || ((Buff[3].inv()).toByte()) != Buff[7]) {
            throw Exception(ctx!!.getString(R.string.strDataFormat))
        }
        if (Buff[12] != bBlockAddress || Buff[14] != bBlockAddress) {
            throw Exception(ctx!!.getString(R.string.strDataFormat))
        }
        if (Buff[13] != ((bBlockAddress.inv()).toByte()) || Buff[15] != ((bBlockAddress.inv()).toByte())) {
            throw Exception(ctx!!.getString(R.string.strDataFormat))
        }
        val values =
            ((Buff[1].toInt() shl 8) and 0xFF00) or (Buff[0].toInt() and 0xFF) or ((Buff[2].toInt() shl 16) and 0xFF0000) or ((Buff[3].toInt() shl 24) and -0x1000000)
        val str = String.format("values: %d ", values)
        Log.d("ContentValues", str)
        return values
    }

    fun MtSetLanguage(bFlag: Boolean) {
        bLanguage = bFlag
        LanguageUtil.set(bFlag)
    }

    @Synchronized
    @Throws(Exception::class)
    fun MtConnect(portName: String?, baudRate: Int, parity: Int) {
        val lRet: Long
        val strLanguage: String = ctx!!.resources.configuration.locale.language
        bLanguage = strLanguage == "en"
        Log.d("ContentValues", "strLanguage: " + strLanguage + " bLanguage: " + bLanguage)
        lRet = mtConnect(portName, baudRate, parity)
    }

    @Synchronized
    @Throws(Exception::class)
    fun MtDisConnect() {
        mtDisconnect()
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_GetVersion(): String {
        val resData: RDATA?
        resData = RDATA()
        ExecuteCmd(48.toByte(), 48.toByte(), null, 0, resData)
        return String(resData.bRDataBuf)
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_Dispense(bPos: Byte) {
        if (bPos > 50 || bPos < 48) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        val resData = RDATA()
        ExecuteCmd(49.toByte(), bPos, null, 0, resData)
        if (resData.bState.toInt() != 89) {
            throw Exception(ctx!!.getString(R.string.strOperate))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_GetState(): ByteArray? {
        val bState: ByteArray?
        bState = ByteArray(8)
        val resData = RDATA()
        ExecuteCmd(MT166_M1_KEY_A, 48.toByte(), null, 0, resData)
        if ((resData.bState.toInt() and TARG_MT166.toInt()) == 1) {
            bState[0] = 49
        } else {
            bState[0] = 48
        }
        if (((resData.bState.toInt() and 2) shr 1) == 1) {
            bState[1] = 49
        } else {
            bState[1] = 48
        }
        if (((resData.bState.toInt() and 4) shr 2) == 1) {
            bState[2] = 49
        } else {
            bState[2] = 48
        }
        if (((8 and resData.bState.toInt()) shr 3) == 1) {
            bState[3] = 49
        } else {
            bState[3] = 48
        }
        if (((resData.bState.toInt() and 16) shr 4) == 1) {
            bState[4] = 49
        } else {
            bState[4] = 48
        }
        if (((resData.bState.toInt() and 32) shr 5) == 1) {
            bState[5] = 49
        } else {
            bState[5] = 48
        }
        if (((resData.bState.toInt() and 64) shr 6) == 1) {
            bState[6] = 49
        } else {
            bState[6] = 48
        }
        if (((resData.bState.toInt() and 128) shr 7) == 1) {
            bState[7] = 49
        } else {
            bState[7] = 48
        }
        return bState
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_RecycleBinStatus(): Byte {
        val resData: RDATA?
        resData = RDATA()
        ExecuteCmd(MT166_M1_KEY_A, 49.toByte(), null, 0, resData)
        return resData.bState
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_CardRecycle() {
        val resData = RDATA()
        ExecuteCmd(51.toByte(), 48.toByte(), null, 0, resData)
        if (resData.bState.toInt() != 89) {
            throw Exception(ctx!!.getString(R.string.strOperate))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_MoveReaderPos() {
        val resData = RDATA()
        ExecuteCmd(51.toByte(), 49.toByte(), null, 0, resData)
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_EnableEntry() {
        val resData = RDATA()
        ExecuteCmd(51.toByte(), MT166_M1_KEY_A, null, 0, resData)
        if (resData.bState.toInt() != 89) {
            throw Exception(ctx!!.getString(R.string.strOperate))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_M1FindCard() {
        val rd = RDATA()
        ExecuteCmd(53.toByte(), 48.toByte(), null, 0, rd)
        if (rd.bState.toInt() == 78) {
            throw Exception(ctx!!.getString(R.string.strFindCard))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_M1GetSerial(): ByteArray {
        val rd: RDATA?
        rd = RDATA()
        ExecuteCmd(53.toByte(), 49.toByte(), null, 0, rd)
        if (rd.bState.toInt() == 78) {
            throw Exception(ctx!!.getString(R.string.strGetSerial))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        return rd.bRDataBuf
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_M1PasswordVerify(bKey: Byte, bSectorNo: Byte, bKeyBytes: ByteArray) {
        if (bKey.toInt() != 50 && bKey.toInt() != 57) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        if (bKeyBytes.size != 6) {
            throw Exception(ctx!!.getString(R.string.strKeyLenErr))
        }
        val rd = RDATA()
        val bData = ByteArray(7)
        bData[0] = bSectorNo
        System.arraycopy(bKeyBytes, 0, bData, 1, 6)
        ExecuteCmd(53.toByte(), bKey, bData, 1, rd)
        if (rd.bState.toInt() == 48) {
            throw Exception(ctx!!.getString(R.string.strDetectedRF))
        }
        if (rd.bState.toInt() == 51) {
            throw Exception(ctx!!.getString(R.string.strKeyErr))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_M1ReadBlock(bSectorNo: Byte, bBlockNo: Byte): ByteArray {
        val rd: RDATA?
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        if (bBlockNo > 15 || bBlockNo < 0) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        val bData = byteArrayOf(bSectorNo, bBlockNo)
        rd = RDATA()
        ExecuteCmd(53.toByte(), 51.toByte(), bData, 2, rd)
        if (rd.bState.toInt() == 48) {
            throw Exception(ctx!!.getString(R.string.strDetectedRF))
        }
        if (rd.bState.toInt() == 49) {
            throw Exception(ctx!!.getString(R.string.strSectorErr))
        }
        if (rd.bState.toInt() == 51) {
            throw Exception(ctx!!.getString(R.string.strKeyErr))
        }
        if (rd.bState.toInt() == 52) {
            throw Exception(ctx!!.getString(R.string.strReadErr))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
        return rd.bRDataBuf
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_M1WriteBlock(bSectorNo: Byte, bBlockNo: Byte, blockData: ByteArray) {
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        if (bBlockNo > 15 || bBlockNo < 0) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        if (blockData.size != 16) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        val bData = ByteArray(18)
        bData[0] = bSectorNo
        bData[1] = bBlockNo
        System.arraycopy(blockData, 0, bData, 2, blockData.size)
        val rd = RDATA()
        ExecuteCmd(53.toByte(), 52.toByte(), bData, 0, rd)
        if (rd.bState.toInt() == 48) {
            throw Exception(ctx!!.getString(R.string.strDetectedRF))
        }
        if (rd.bState.toInt() == 49) {
            throw Exception(ctx!!.getString(R.string.strSectorErr))
        }
        if (rd.bState.toInt() == 51) {
            throw Exception(ctx!!.getString(R.string.strKeyErr))
        }
        if (rd.bState.toInt() == 52) {
            throw Exception(ctx!!.getString(R.string.strWriteErr))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_M1UpdatePassword(bSectorNo: Byte, bKeyBytes: ByteArray) {
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        if (bKeyBytes.size != 6) {
            throw Exception(ctx!!.getString(R.string.strKeyLenErr))
        }
        val rd = RDATA()
        val bData = ByteArray(7)
        bData[0] = bSectorNo
        System.arraycopy(bKeyBytes, 0, bData, 1, bKeyBytes.size)
        ExecuteCmd(53.toByte(), 53.toByte(), bData, 0, rd)
        if (rd.bState.toInt() == 48) {
            throw Exception(ctx!!.getString(R.string.strDetectedRF))
        }
        if (rd.bState.toInt() == 49) {
            throw Exception(ctx!!.getString(R.string.strSectorErr))
        }
        if (rd.bState.toInt() == 51) {
            throw Exception(ctx!!.getString(R.string.strKeyErr))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_M1Increase(sectorNo: Int, blockNo: Int, value: Int) {
        val cmdData = byteArrayOf(
            sectorNo.toByte(),
            blockNo.toByte(),
            (value and 255).toByte(),
            ((value shr 8) and 255).toByte(),
            ((value shr 16) and 255).toByte(),
            ((value shr 24) and 255).toByte()
        )
        val rd = RDATA()
        ExecuteCmd(53.toByte(), 55.toByte(), cmdData, 2, rd)
        if (rd.bState.toInt() == 48) {
            throw Exception(ctx!!.getString(R.string.strDetectedRF))
        }
        if (rd.bState.toInt() == 49) {
            throw Exception(ctx!!.getString(R.string.strSectorErr))
        }
        if (rd.bState.toInt() == 51) {
            throw Exception(ctx!!.getString(R.string.strKeyErr))
        }
        if (rd.bState.toInt() == 52) {
            throw Exception(ctx!!.getString(R.string.strBlockFormat))
        }
        if (rd.bState.toInt() == 53) {
            throw Exception(ctx!!.getString(R.string.strValuesErr))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
        if (rd.bState.toInt() == 78) {
            throw Exception(ctx!!.getString(R.string.strOperate))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_M1Decrement(sectorNo: Int, blockNo: Int, value: Int) {
        val cmdData = byteArrayOf(
            sectorNo.toByte(),
            blockNo.toByte(),
            (value and 255).toByte(),
            ((value shr 8) and 255).toByte(),
            ((value shr 16) and 255).toByte(),
            ((value shr 24) and 255).toByte()
        )
        val rd = RDATA()
        ExecuteCmd(53.toByte(), 56.toByte(), cmdData, 2, rd)
        if (rd.bState.toInt() == 48) {
            throw Exception(ctx!!.getString(R.string.strDetectedRF))
        }
        if (rd.bState.toInt() == 49) {
            throw Exception(ctx!!.getString(R.string.strSectorErr))
        }
        if (rd.bState.toInt() == 51) {
            throw Exception(ctx!!.getString(R.string.strKeyErr))
        }
        if (rd.bState.toInt() == 52) {
            throw Exception(ctx!!.getString(R.string.strBlockFormat))
        }
        if (rd.bState.toInt() == 53) {
            throw Exception(ctx!!.getString(R.string.strValuesErr))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
        if (rd.bState.toInt() == 78) {
            throw Exception(ctx!!.getString(R.string.strOperate))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_M1InitValue(bSectorNo: Byte, bBlockNo: Byte, iValues: Int) {
        val bBlockAddress: Byte
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        if (bBlockNo > 15 || bBlockNo < 0) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        val bBlockAddress2 = ((bSectorNo * 4) + bBlockNo).toByte()
        if (bSectorNo < 32) {
            bBlockAddress = bBlockAddress2
        } else {
            bBlockAddress = (((bSectorNo - 32) * 16) + bSectorNo + 128).toByte()
        }
        val Buff = ByteArray(16)
        val b = (iValues and 255).toByte()
        Buff[0] = b
        Buff[8] = b
        val b2 = ((iValues shr 8) and 255).toByte()
        Buff[1] = b2
        Buff[9] = b2
        val b3 = ((iValues shr 16) and 255).toByte()
        Buff[2] = b3
        Buff[10] = b3
        val b4 = ((iValues shr 24) and 255).toByte()
        Buff[3] = b4
        Buff[11] = b4
        Buff[4] = (Buff[0].inv()).toByte()
        Buff[5] = (Buff[1].inv()).toByte()
        Buff[6] = (Buff[2].inv()).toByte()
        Buff[7] = (Buff[3].inv()).toByte()
        Buff[12] = bBlockAddress
        Buff[14] = bBlockAddress
        val b5 = (bBlockAddress.inv()).toByte()
        Buff[13] = b5
        Buff[15] = b5
        val bData = ByteArray(18)
        bData[0] = bSectorNo
        bData[1] = bBlockNo
        System.arraycopy(Buff, 0, bData, 2, Buff.size)
        val rd = RDATA()
        ExecuteCmd(53.toByte(), 52.toByte(), bData, 0, rd)
        if (rd.bState.toInt() == 48) {
            throw Exception(ctx!!.getString(R.string.strDetectedRF))
        }
        if (rd.bState.toInt() == 49) {
            throw Exception(ctx!!.getString(R.string.strSectorErr))
        }
        if (rd.bState.toInt() == 51) {
            throw Exception(ctx!!.getString(R.string.strKeyErr))
        }
        if (rd.bState.toInt() == 52) {
            throw Exception(ctx!!.getString(R.string.strWriteErr))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_M1ReadValue(bSectorNo: Byte, bBlockNo: Byte): Int {
        val rd: RDATA?
        var bBlockAddress: Byte
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        if (bBlockNo > 15 || bBlockNo < 0) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        val bData = byteArrayOf(bSectorNo, bBlockNo)
        rd = RDATA()
        ExecuteCmd(53.toByte(), 51.toByte(), bData, 2, rd)
        if (rd.bState.toInt() == 48) {
            throw Exception(ctx!!.getString(R.string.strDetectedRF))
        }
        if (rd.bState.toInt() == 49) {
            throw Exception(ctx!!.getString(R.string.strSectorErr))
        }
        if (rd.bState.toInt() == 51) {
            throw Exception(ctx!!.getString(R.string.strKeyErr))
        }
        if (rd.bState.toInt() == 52) {
            throw Exception(ctx!!.getString(R.string.strReadErr))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
        bBlockAddress = ((bSectorNo * 4) + bBlockNo).toByte()
        if (bSectorNo >= 32) {
            bBlockAddress = (((bSectorNo - 32) * 16) + bSectorNo + 128).toByte()
        }
        return CheckValueBlock(bBlockAddress, rd.bRDataBuf)
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_TypeCpuActive(bType: Byte): ByteArray? {
        val bATR: ByteArray?
        val rd = RDATA()
        if (bType.toInt() == 65) {
            ExecuteCmd(53.toByte(), 96.toByte(), null, 0, rd)
        } else if (bType.toInt() == 66) {
            ExecuteCmd(53.toByte(), 97.toByte(), null, 0, rd)
        } else {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        if (rd.bState.toInt() == 78) {
            throw Exception(ctx!!.getString(R.string.strActivateErr))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
        val iATSLen =
            (rd.bRDataBuf[0].toInt() and MotionEventCompat.ACTION_POINTER_INDEX_MASK) or (rd.bRDataBuf[1].toInt() and 255)
        bATR = ByteArray(iATSLen)
        System.arraycopy(rd.bRDataBuf, 2, bATR, 0, iATSLen)
        return bATR
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_TypeCpuTransmit(bType: Byte, apduData: ByteArray): ByteArray? {
        val bApdu: ByteArray?
        val rd = RDATA()
        val bSendData = ByteArray(apduData.size + 2)
        bSendData[0] = ((apduData.size shr 8) and 255).toByte()
        bSendData[1] = (apduData.size and 255).toByte()
        System.arraycopy(apduData, 0, bSendData, 2, apduData.size)
        if (bType.toInt() == 65 || bType.toInt() == 66) {
            ExecuteCmd(53.toByte(), 101.toByte(), bSendData, 0, rd)
            if (rd.bState.toInt() == 69) {
                throw Exception(ctx!!.getString(R.string.strNoCard))
            }
            if (rd.bState.toInt() == 78) {
                throw Exception(ctx!!.getString(R.string.strApduErr))
            }
            if (rd.bState.toInt() == 87) {
                throw Exception(ctx!!.getString(R.string.strErrPos))
            }
            val iApduLen =
                (rd.bRDataBuf[0].toInt() and MotionEventCompat.ACTION_POINTER_INDEX_MASK) or (rd.bRDataBuf[1].toInt() and 255)
            bApdu = ByteArray(iApduLen)
            System.arraycopy(rd.bRDataBuf, 2, bApdu, 0, iApduLen)
        } else {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        return bApdu
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_TypeCpuDeselect() {
        val rd = RDATA()
        ExecuteCmd(53.toByte(), 104.toByte(), null, 0, rd)
        if (rd.bState.toInt() == 78) {
            throw Exception(ctx!!.getString(R.string.strDeselect))
        }
        if (rd.bState.toInt() == 69) {
            throw Exception(ctx!!.getString(R.string.strNoCard))
        }
        if (rd.bState.toInt() == 87) {
            throw Exception(ctx!!.getString(R.string.strErrPos))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_ULFind(): ByteArray {
        val rd: RDATA?
        rd = RDATA()
        ExecuteCmd(53.toByte(), 32.toByte(), null, 0, rd)
        if (rd.bState.toInt() != 89) {
            throw Exception(ctx!!.getString(R.string.strOperate))
        }
        return rd.bRDataBuf
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_ULRead(blockNo: Byte): ByteArray {
        val rd: RDATA?
        rd = RDATA()
        if (blockNo < 0 || blockNo > 16) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        val bData = byteArrayOf(blockNo)
        ExecuteCmd(53.toByte(), 33.toByte(), bData, 1, rd)
        if (rd.bState.toInt() != 89) {
            throw Exception(ctx!!.getString(R.string.strOperate))
        }
        return rd.bRDataBuf
    }

    @Synchronized
    @Throws(Exception::class)
    fun MT166_ULWrite(blockNo: Byte, bData: ByteArray) {
        val rd: RDATA?
        rd = RDATA()
        if (blockNo < 0 || blockNo > 16) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        if (bData.size != 4) {
            throw Exception(ctx!!.getString(R.string.strParam))
        }
        val bUlData = ByteArray(5)
        bUlData[0] = blockNo
        System.arraycopy(bData, 0, bUlData, 1, bData.size)
        ExecuteCmd(53.toByte(), 34.toByte(), bUlData, 1, rd)
        if (rd.bState.toInt() != 89) {
            throw Exception(ctx!!.getString(R.string.strOperate))
        }
    }

    @Synchronized
    @Throws(Exception::class)
    fun ExecuteCmd(bCM: Byte, bPM: Byte, bData: ByteArray?, iStOffset: Int, rd: RDATA?) {
        val ret: Long
        val resData = RDATA()
        val timeout = if (bCM.toInt() == 49) 10000 else 0
        ret = mtExecute(TARG_MT166, bCM, bPM, bData, timeout, 0, resData)
        val len = resData.bRDataBuf.size - (iStOffset + 1)
        if (rd != null) {
            rd.bState = resData.bRDataBuf[iStOffset]
            if (len > 1) {
                rd.bRDataBuf = ByteArray(len)
                System.arraycopy(resData.bRDataBuf, iStOffset + 1, rd.bRDataBuf, 0, len)
            }
        }
    }

    companion object {
        const val MT166_M1_KEY_A: Byte = 50
        const val MT166_M1_KEY_B: Byte = 57
        private const val TARG_MT166: Byte = 1
        var ctx: Context? = null
        var bLanguage: Boolean = true

        @Throws(Exception::class)
        private external fun mtConnect(str: String?, i: Int, i2: Int): Long

        @Throws(Exception::class)
        private external fun mtDisconnect(): Long

        @Throws(Exception::class)
        private external fun mtExecute(
            b: Byte,
            b2: Byte,
            b3: Byte,
            bArr: ByteArray?,
            i: Int,
            i2: Int,
            rdata: RDATA?
        ): Long

        init {
            System.loadLibrary("MtJniA")
        }
    }
}
