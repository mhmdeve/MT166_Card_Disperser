package com.mt.mt166demo


class APDUCmdItem(var name: String?, var cmdData: ByteArray) {
    override fun toString(): String {
        return this.name + "\t{ " + Utils.bytesToHexString(this.cmdData) + " }"
    }
}
