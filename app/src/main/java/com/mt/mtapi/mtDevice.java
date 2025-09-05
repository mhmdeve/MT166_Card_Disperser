package com.mt.mtapi;

import android.content.Context;
import android.util.Log;

import androidx.core.view.MotionEventCompat;

import com.mt.mt166demo.R;

/* loaded from: classes3.dex */
public class mtDevice {
    public static final byte MT166_M1_KEY_A = 50;
    public static final byte MT166_M1_KEY_B = 57;
    private static final byte TARG_MT166 = 1;
    public static Context ctx = null;
    public static boolean bLanguage = true;

    public static class RDATA {
        public byte[] bRDataBuf;
        public byte bState;
        public int ilen;
    }

    private static native long mtConnect(String str, int i, int i2) throws Exception;

    private static native long mtDisconnect() throws Exception;

    private static native long mtExecute(byte b, byte b2, byte b3, byte[] bArr, int i, int i2, RDATA rdata) throws Exception;

    static {
        System.loadLibrary("MtJniA");
    }

    public mtDevice(Context context) {
        ctx = context;
    }

    int CheckValueBlock(byte bBlockAddress, byte[] Buff) throws Exception {
        if (Buff[0] != Buff[8] || Buff[1] != Buff[9] || Buff[2] != Buff[10] || Buff[3] != Buff[11]) {
            throw new Exception(ctx.getString(R.string.strDataFormat));
        }
        if (((byte) (~Buff[0])) != Buff[4] || ((byte) (~Buff[1])) != Buff[5] || ((byte) (~Buff[2])) != Buff[6] || ((byte) (~Buff[3])) != Buff[7]) {
            throw new Exception(ctx.getString(R.string.strDataFormat));
        }
        if (Buff[12] != bBlockAddress || Buff[14] != bBlockAddress) {
            throw new Exception(ctx.getString(R.string.strDataFormat));
        }
        if (Buff[13] != ((byte) (~bBlockAddress)) || Buff[15] != ((byte) (~bBlockAddress))) {
            throw new Exception(ctx.getString(R.string.strDataFormat));
        }
        int values = ((Buff[1] << 8) & 0xFF00) | (Buff[0] & 0xFF) | ((Buff[2] << 16) & 0xFF0000) | ((Buff[3] << 24) & 0xFF000000);
        String str = String.format("values: %d ", Integer.valueOf(values));
        Log.d("ContentValues", str);
        return values;
    }

    public void MtSetLanguage(boolean bFlag) {
        bLanguage = bFlag;
        LanguageUtil.set(bFlag);
    }

    public synchronized void MtConnect(String portName, int baudRate, int parity) throws Exception {
        long lRet;
        String strLanguage = ctx.getResources().getConfiguration().locale.getLanguage();
        bLanguage = strLanguage.equals("en");
        Log.d("ContentValues", "strLanguage: " + strLanguage + " bLanguage: " + bLanguage);
        lRet = mtConnect(portName, baudRate, parity);
    }

    public synchronized void MtDisConnect() throws Exception {
        mtDisconnect();
    }

    public synchronized String MT166_GetVersion() throws Exception {
        RDATA resData;
        resData = new RDATA();
        ExecuteCmd((byte) 48, (byte) 48, null, 0, resData);
        return new String(resData.bRDataBuf);
    }

    public synchronized void MT166_Dispense(byte bPos) throws Exception {
        if (bPos > 50 || bPos < 48) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        RDATA resData = new RDATA();
        ExecuteCmd((byte) 49, bPos, null, 0, resData);
        if (resData.bState != 89) {
            throw new Exception(ctx.getString(R.string.strOperate));
        }
    }

    public synchronized byte[] MT166_GetState() throws Exception {
        byte[] bState;
        bState = new byte[8];
        RDATA resData = new RDATA();
        ExecuteCmd(MT166_M1_KEY_A, (byte) 48, null, 0, resData);
        if ((resData.bState & TARG_MT166) == 1) {
            bState[0] = 49;
        } else {
            bState[0] = 48;
        }
        if (((resData.bState & 2) >> 1) == 1) {
            bState[1] = 49;
        } else {
            bState[1] = 48;
        }
        if (((resData.bState & 4) >> 2) == 1) {
            bState[2] = 49;
        } else {
            bState[2] = 48;
        }
        if (((8 & resData.bState) >> 3) == 1) {
            bState[3] = 49;
        } else {
            bState[3] = 48;
        }
        if (((resData.bState & 16) >> 4) == 1) {
            bState[4] = 49;
        } else {
            bState[4] = 48;
        }
        if (((resData.bState & 32) >> 5) == 1) {
            bState[5] = 49;
        } else {
            bState[5] = 48;
        }
        if (((resData.bState & 64) >> 6) == 1) {
            bState[6] = 49;
        } else {
            bState[6] = 48;
        }
        if (((resData.bState & 128) >> 7) == 1) {
            bState[7] = 49;
        } else {
            bState[7] = 48;
        }
        return bState;
    }

    public synchronized byte MT166_RecycleBinStatus() throws Exception {
        RDATA resData;
        resData = new RDATA();
        ExecuteCmd(MT166_M1_KEY_A, (byte) 49, null, 0, resData);
        return resData.bState;
    }

    public synchronized void MT166_CardRecycle() throws Exception {
        RDATA resData = new RDATA();
        ExecuteCmd((byte) 51, (byte) 48, null, 0, resData);
        if (resData.bState != 89) {
            throw new Exception(ctx.getString(R.string.strOperate));
        }
    }

    public synchronized void MT166_MoveReaderPos() throws Exception {
        RDATA resData = new RDATA();
        ExecuteCmd((byte) 51, (byte) 49, null, 0, resData);
    }

    public synchronized void MT166_EnableEntry() throws Exception {
        RDATA resData = new RDATA();
        ExecuteCmd((byte) 51, MT166_M1_KEY_A, null, 0, resData);
        if (resData.bState != 89) {
            throw new Exception(ctx.getString(R.string.strOperate));
        }
    }

    public synchronized void MT166_M1FindCard() throws Exception {
        RDATA rd = new RDATA();
        ExecuteCmd((byte) 53, (byte) 48, null, 0, rd);
        if (rd.bState == 78) {
            throw new Exception(ctx.getString(R.string.strFindCard));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
    }

    public synchronized byte[] MT166_M1GetSerial() throws Exception {
        RDATA rd;
        rd = new RDATA();
        ExecuteCmd((byte) 53, (byte) 49, null, 0, rd);
        if (rd.bState == 78) {
            throw new Exception(ctx.getString(R.string.strGetSerial));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        return rd.bRDataBuf;
    }

    public synchronized void MT166_M1PasswordVerify(byte bKey, byte bSectorNo, byte[] bKeyBytes) throws Exception {
        if (bKey != 50 && bKey != 57) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        if (bKeyBytes.length != 6) {
            throw new Exception(ctx.getString(R.string.strKeyLenErr));
        }
        RDATA rd = new RDATA();
        byte[] bData = new byte[7];
        bData[0] = bSectorNo;
        System.arraycopy(bKeyBytes, 0, bData, 1, 6);
        ExecuteCmd((byte) 53, bKey, bData, 1, rd);
        if (rd.bState == 48) {
            throw new Exception(ctx.getString(R.string.strDetectedRF));
        }
        if (rd.bState == 51) {
            throw new Exception(ctx.getString(R.string.strKeyErr));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
    }

    public synchronized byte[] MT166_M1ReadBlock(byte bSectorNo, byte bBlockNo) throws Exception {
        RDATA rd;
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        if (bBlockNo > 15 || bBlockNo < 0) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        byte[] bData = {bSectorNo, bBlockNo};
        rd = new RDATA();
        ExecuteCmd((byte) 53, (byte) 51, bData, 2, rd);
        if (rd.bState == 48) {
            throw new Exception(ctx.getString(R.string.strDetectedRF));
        }
        if (rd.bState == 49) {
            throw new Exception(ctx.getString(R.string.strSectorErr));
        }
        if (rd.bState == 51) {
            throw new Exception(ctx.getString(R.string.strKeyErr));
        }
        if (rd.bState == 52) {
            throw new Exception(ctx.getString(R.string.strReadErr));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
        return rd.bRDataBuf;
    }

    public synchronized void MT166_M1WriteBlock(byte bSectorNo, byte bBlockNo, byte[] blockData) throws Exception {
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        if (bBlockNo > 15 || bBlockNo < 0) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        if (blockData.length != 16) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        byte[] bData = new byte[18];
        bData[0] = bSectorNo;
        bData[1] = bBlockNo;
        System.arraycopy(blockData, 0, bData, 2, blockData.length);
        RDATA rd = new RDATA();
        ExecuteCmd((byte) 53, (byte) 52, bData, 0, rd);
        if (rd.bState == 48) {
            throw new Exception(ctx.getString(R.string.strDetectedRF));
        }
        if (rd.bState == 49) {
            throw new Exception(ctx.getString(R.string.strSectorErr));
        }
        if (rd.bState == 51) {
            throw new Exception(ctx.getString(R.string.strKeyErr));
        }
        if (rd.bState == 52) {
            throw new Exception(ctx.getString(R.string.strWriteErr));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
    }

    public synchronized void MT166_M1UpdatePassword(byte bSectorNo, byte[] bKeyBytes) throws Exception {
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        if (bKeyBytes.length != 6) {
            throw new Exception(ctx.getString(R.string.strKeyLenErr));
        }
        RDATA rd = new RDATA();
        byte[] bData = new byte[7];
        bData[0] = bSectorNo;
        System.arraycopy(bKeyBytes, 0, bData, 1, bKeyBytes.length);
        ExecuteCmd((byte) 53, (byte) 53, bData, 0, rd);
        if (rd.bState == 48) {
            throw new Exception(ctx.getString(R.string.strDetectedRF));
        }
        if (rd.bState == 49) {
            throw new Exception(ctx.getString(R.string.strSectorErr));
        }
        if (rd.bState == 51) {
            throw new Exception(ctx.getString(R.string.strKeyErr));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
    }

    public synchronized void MT166_M1Increase(int sectorNo, int blockNo, int value) throws Exception {
        byte[] cmdData = {(byte) sectorNo, (byte) blockNo, (byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255)};
        RDATA rd = new RDATA();
        ExecuteCmd((byte) 53, (byte) 55, cmdData, 2, rd);
        if (rd.bState == 48) {
            throw new Exception(ctx.getString(R.string.strDetectedRF));
        }
        if (rd.bState == 49) {
            throw new Exception(ctx.getString(R.string.strSectorErr));
        }
        if (rd.bState == 51) {
            throw new Exception(ctx.getString(R.string.strKeyErr));
        }
        if (rd.bState == 52) {
            throw new Exception(ctx.getString(R.string.strBlockFormat));
        }
        if (rd.bState == 53) {
            throw new Exception(ctx.getString(R.string.strValuesErr));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
        if (rd.bState == 78) {
            throw new Exception(ctx.getString(R.string.strOperate));
        }
    }

    public synchronized void MT166_M1Decrement(int sectorNo, int blockNo, int value) throws Exception {
        byte[] cmdData = {(byte) sectorNo, (byte) blockNo, (byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255)};
        RDATA rd = new RDATA();
        ExecuteCmd((byte) 53, (byte) 56, cmdData, 2, rd);
        if (rd.bState == 48) {
            throw new Exception(ctx.getString(R.string.strDetectedRF));
        }
        if (rd.bState == 49) {
            throw new Exception(ctx.getString(R.string.strSectorErr));
        }
        if (rd.bState == 51) {
            throw new Exception(ctx.getString(R.string.strKeyErr));
        }
        if (rd.bState == 52) {
            throw new Exception(ctx.getString(R.string.strBlockFormat));
        }
        if (rd.bState == 53) {
            throw new Exception(ctx.getString(R.string.strValuesErr));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
        if (rd.bState == 78) {
            throw new Exception(ctx.getString(R.string.strOperate));
        }
    }

    public synchronized void MT166_M1InitValue(byte bSectorNo, byte bBlockNo, int iValues) throws Exception {
        byte bBlockAddress;
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        if (bBlockNo > 15 || bBlockNo < 0) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        byte bBlockAddress2 = (byte) ((bSectorNo * 4) + bBlockNo);
        if (bSectorNo < 32) {
            bBlockAddress = bBlockAddress2;
        } else {
            bBlockAddress = (byte) (((bSectorNo - 32) * 16) + bSectorNo + 128);
        }
        byte[] Buff = new byte[16];
        byte b = (byte) (iValues & 255);
        Buff[0] = b;
        Buff[8] = b;
        byte b2 = (byte) ((iValues >> 8) & 255);
        Buff[1] = b2;
        Buff[9] = b2;
        byte b3 = (byte) ((iValues >> 16) & 255);
        Buff[2] = b3;
        Buff[10] = b3;
        byte b4 = (byte) ((iValues >> 24) & 255);
        Buff[3] = b4;
        Buff[11] = b4;
        Buff[4] = (byte) (~Buff[0]);
        Buff[5] = (byte) (~Buff[1]);
        Buff[6] = (byte) (~Buff[2]);
        Buff[7] = (byte) (~Buff[3]);
        Buff[12] = bBlockAddress;
        Buff[14] = bBlockAddress;
        byte b5 = (byte) (~bBlockAddress);
        Buff[13] = b5;
        Buff[15] = b5;
        byte[] bData = new byte[18];
        bData[0] = bSectorNo;
        bData[1] = bBlockNo;
        System.arraycopy(Buff, 0, bData, 2, Buff.length);
        RDATA rd = new RDATA();
        ExecuteCmd((byte) 53, (byte) 52, bData, 0, rd);
        if (rd.bState == 48) {
            throw new Exception(ctx.getString(R.string.strDetectedRF));
        }
        if (rd.bState == 49) {
            throw new Exception(ctx.getString(R.string.strSectorErr));
        }
        if (rd.bState == 51) {
            throw new Exception(ctx.getString(R.string.strKeyErr));
        }
        if (rd.bState == 52) {
            throw new Exception(ctx.getString(R.string.strWriteErr));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
    }

    public synchronized int MT166_M1ReadValue(byte bSectorNo, byte bBlockNo) throws Exception {
        RDATA rd;
        byte bBlockAddress;
        if (bSectorNo > 40 || bSectorNo < 0) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        if (bBlockNo > 15 || bBlockNo < 0) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        byte[] bData = {bSectorNo, bBlockNo};
        rd = new RDATA();
        ExecuteCmd((byte) 53, (byte) 51, bData, 2, rd);
        if (rd.bState == 48) {
            throw new Exception(ctx.getString(R.string.strDetectedRF));
        }
        if (rd.bState == 49) {
            throw new Exception(ctx.getString(R.string.strSectorErr));
        }
        if (rd.bState == 51) {
            throw new Exception(ctx.getString(R.string.strKeyErr));
        }
        if (rd.bState == 52) {
            throw new Exception(ctx.getString(R.string.strReadErr));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
        bBlockAddress = (byte) ((bSectorNo * 4) + bBlockNo);
        if (bSectorNo >= 32) {
            bBlockAddress = (byte) (((bSectorNo - 32) * 16) + bSectorNo + 128);
        }
        return CheckValueBlock(bBlockAddress, rd.bRDataBuf);
    }

    public synchronized byte[] MT166_TypeCpuActive(byte bType) throws Exception {
        byte[] bATR;
        RDATA rd = new RDATA();
        if (bType == 65) {
            ExecuteCmd((byte) 53, (byte) 96, null, 0, rd);
        } else if (bType == 66) {
            ExecuteCmd((byte) 53, (byte) 97, null, 0, rd);
        } else {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        if (rd.bState == 78) {
            throw new Exception(ctx.getString(R.string.strActivateErr));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
        int iATSLen = (rd.bRDataBuf[0] & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (rd.bRDataBuf[1] & 255);
        bATR = new byte[iATSLen];
        System.arraycopy(rd.bRDataBuf, 2, bATR, 0, iATSLen);
        return bATR;
    }

    public synchronized byte[] MT166_TypeCpuTransmit(byte bType, byte[] apduData) throws Exception {
        byte[] bApdu;
        RDATA rd = new RDATA();
        byte[] bSendData = new byte[apduData.length + 2];
        bSendData[0] = (byte) ((apduData.length >> 8) & 255);
        bSendData[1] = (byte) (apduData.length & 255);
        System.arraycopy(apduData, 0, bSendData, 2, apduData.length);
        if (bType == 65 || bType == 66) {
            ExecuteCmd((byte) 53, (byte) 101, bSendData, 0, rd);
            if (rd.bState == 69) {
                throw new Exception(ctx.getString(R.string.strNoCard));
            }
            if (rd.bState == 78) {
                throw new Exception(ctx.getString(R.string.strApduErr));
            }
            if (rd.bState == 87) {
                throw new Exception(ctx.getString(R.string.strErrPos));
            }
            int iApduLen = (rd.bRDataBuf[0] & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (rd.bRDataBuf[1] & 255);
            bApdu = new byte[iApduLen];
            System.arraycopy(rd.bRDataBuf, 2, bApdu, 0, iApduLen);
        } else {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        return bApdu;
    }

    public synchronized void MT166_TypeCpuDeselect() throws Exception {
        RDATA rd = new RDATA();
        ExecuteCmd((byte) 53, (byte) 104, null, 0, rd);
        if (rd.bState == 78) {
            throw new Exception(ctx.getString(R.string.strDeselect));
        }
        if (rd.bState == 69) {
            throw new Exception(ctx.getString(R.string.strNoCard));
        }
        if (rd.bState == 87) {
            throw new Exception(ctx.getString(R.string.strErrPos));
        }
    }

    public synchronized byte[] MT166_ULFind() throws Exception {
        RDATA rd;
        rd = new RDATA();
        ExecuteCmd((byte) 53, (byte) 32, null, 0, rd);
        if (rd.bState != 89) {
            throw new Exception(ctx.getString(R.string.strOperate));
        }
        return rd.bRDataBuf;
    }

    public synchronized byte[] MT166_ULRead(byte blockNo) throws Exception {
        RDATA rd;
        rd = new RDATA();
        if (blockNo < 0 || blockNo > 16) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        byte[] bData = {blockNo};
        ExecuteCmd((byte) 53, (byte) 33, bData, 1, rd);
        if (rd.bState != 89) {
            throw new Exception(ctx.getString(R.string.strOperate));
        }
        return rd.bRDataBuf;
    }

    public synchronized void MT166_ULWrite(byte blockNo, byte[] bData) throws Exception {
        RDATA rd;
        rd = new RDATA();
        if (blockNo < 0 || blockNo > 16) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        if (bData.length != 4) {
            throw new Exception(ctx.getString(R.string.strParam));
        }
        byte[] bUlData = new byte[5];
        bUlData[0] = blockNo;
        System.arraycopy(bData, 0, bUlData, 1, bData.length);
        ExecuteCmd((byte) 53, (byte) 34, bUlData, 1, rd);
        if (rd.bState != 89) {
            throw new Exception(ctx.getString(R.string.strOperate));
        }
    }

    public synchronized void ExecuteCmd(byte bCM, byte bPM, byte[] bData, int iStOffset, RDATA rd) throws Exception {
        long ret;
        RDATA resData = new RDATA();
        int timeout = bCM == 49 ? 10000 : 0;
        ret = mtExecute(TARG_MT166, bCM, bPM, bData, timeout, 0, resData);
        int len = resData.bRDataBuf.length - (iStOffset + 1);
        if (rd != null) {
            rd.bState = resData.bRDataBuf[iStOffset];
            if (len > 1) {
                rd.bRDataBuf = new byte[len];
                System.arraycopy(resData.bRDataBuf, iStOffset + 1, rd.bRDataBuf, 0, len);
            }
        }
    }
}
