package com.mt.mt166demo;

/* loaded from: classes2.dex */
public final class Utils {
    public static String bytesToHexString(byte[] bytes) {
        char[] rgHexChars = "0123456789ABCDEF".toCharArray();
        char[] chars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 255;
            chars[j * 3] = rgHexChars[v >>> 4];
            chars[(j * 3) + 1] = rgHexChars[v & 15];
            chars[(j * 3) + 2] = ' ';
        }
        return new String(chars);
    }

    public static byte[] hexStringToBytes(String strHex) {
        if (strHex == null || strHex.equals("")) {
            return null;
        }
        String strHex2 = strHex.toUpperCase().replaceAll(" ", "");
        int length = strHex2.length() / 2;
        char[] chars = strHex2.toCharArray();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) ((hexValue(chars[i * 2]) << 4) | hexValue(chars[(i * 2) + 1]));
        }
        return bytes;
    }

    private static int hexValue(char c) {
        if (Character.isDigit(c)) {
            return c - '0';
        }
        return (c - 'A') + 10;
    }
}
