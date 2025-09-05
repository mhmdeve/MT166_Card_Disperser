package com.mt.mt166demo

import java.util.Locale


object Utils {
    fun bytesToHexString(bytes: ByteArray): String {
        val rgHexChars = "0123456789ABCDEF".toCharArray()
        val chars = CharArray(bytes.size * 3)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 255
            chars[j * 3] = rgHexChars[v ushr 4]
            chars[(j * 3) + 1] = rgHexChars[v and 15]
            chars[(j * 3) + 2] = ' '
        }
        return String(chars)
    }

    fun hexStringToBytes(strHex: String?): ByteArray? {
        if (strHex == null || strHex == "") {
            return null
        }
        val strHex2 = strHex.uppercase(Locale.getDefault()).replace(" ".toRegex(), "")
        val length = strHex2.length / 2
        val chars = strHex2.toCharArray()
        val bytes = ByteArray(length)
        for (i in 0..<length) {
            bytes[i] = ((hexValue(chars[i * 2]) shl 4) or hexValue(chars[(i * 2) + 1])).toByte()
        }
        return bytes
    }

    private fun hexValue(c: Char): Int {
        if (Character.isDigit(c)) {
            return c.code - '0'.code
        }
        return (c.code - 'A'.code) + 10
    }
}
