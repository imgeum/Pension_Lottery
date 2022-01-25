package com.neoguri.pensionlottery.util

import java.security.DigestException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
/**
 * SHA256Utill().encrypt("블랙핑크")
 *
 * SHA256Utill().hashSHA256("블랙핑크")
 *
 * SHA256Utill().developerHashSHA256("블랙핑크")
 **/
class SHA256Utill {

    private val digits = "0123456789ABCDEF"

    fun encrypt(str: String): String? {
        var SHA: String? = ""
        SHA = try {
            val sh: MessageDigest = MessageDigest.getInstance("SHA-256")
            sh.update(str.toByteArray())
            val byteData: ByteArray = sh.digest()
            val sb = StringBuffer()
            for (i in byteData.indices) sb.append(
                ((byteData[i].toInt() and 0xff) + 0x100).toString(16).substring(1)
            )
            sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
        return SHA
    }

    fun hashSHA256(msg: String): String {
        val hash: ByteArray
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(msg.toByteArray())
            hash = md.digest()
        } catch (e: CloneNotSupportedException) {
            throw DigestException("couldn't make digest of partial content");
        }

        return bytesToHex(hash)
    }

    fun developerHashSHA256(msg: String): String {
        val message: ByteArray = msg.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest: ByteArray = md.digest(message)
        return bytesToHex(digest)
    }

    private fun bytesToHex(byteArray: ByteArray): String {
        val hexChars = CharArray(byteArray.size * 2)
        for (i in byteArray.indices) {
            val v = byteArray[i].toInt() and 0xff
            hexChars[i * 2] = digits[v shr 4]
            hexChars[i * 2 + 1] = digits[v and 0xf]
        }
        return String(hexChars)
    }

}