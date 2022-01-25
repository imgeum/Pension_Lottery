package com.neoguri.pensionlottery.util

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.Key
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 양방향 암호화 알고리즘인 AES256 암호화를 지원하는 클래스
 *
 * AES256Util("2D45E:7zTYy23=47bQLeR29LxE32F7kb").encrypt("ABCDE")
 */
class AES256Util(skey: String) {
    private val iv: String
    private val keySpec: Key

    /**
     * AES256 으로 암호화 한다.
     *
     * @param str
     * 암호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     */
    @Throws(
        NoSuchAlgorithmException::class,
        GeneralSecurityException::class
    )
    fun encrypt(str: String): String {
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        c.init(
            Cipher.ENCRYPT_MODE,
            keySpec,
            IvParameterSpec(iv.toByteArray())
        )
        val encrypted =
            c.doFinal(str.toByteArray(StandardCharsets.UTF_8))
        //return String(Base64.encode(encrypted, Base64.DEFAULT)) // 정확히는 76글자가 넘는다면, 해당 위치에 개행문자(LF)를 삽입합니다. https://kjwsx23.tistory.com/234
        return String(Base64.encode(encrypted, Base64.NO_WRAP)) // 개행문자 없이 쭈욱 뽑을때
    }

    /**
     * AES256으로 암호화된 txt 를 복호화한다.
     *
     * @param str
     * 복호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     */
    @Throws(
        NoSuchAlgorithmException::class,
        GeneralSecurityException::class
    )
    fun decrypt(str: String): String {
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        c.init(
            Cipher.DECRYPT_MODE,
            keySpec,
            IvParameterSpec(iv.toByteArray())
        )
        // val byteStr = Base64.decode(str.toByteArray(), Base64.DEFAULT)
        val byteStr = Base64.decode(str.toByteArray(), Base64.NO_WRAP)
        return String(c.doFinal(byteStr), StandardCharsets.UTF_8)
    }

    /**
     * 16자리의 키값을 입력하여 객체를 생성한다.
     *
     * @param skey
     * 암/복호화를 위한 키값
     * @throws UnsupportedEncodingException
     * 키값의 길이가 16이하일 경우 발생
     */
    init {
        iv = skey.substring(0, 16)
        val keyBytes = ByteArray(16)
        val b = skey.toByteArray(StandardCharsets.UTF_8)
        var len = b.size
        if (len > keyBytes.size) {
            len = keyBytes.size
        }
        System.arraycopy(b, 0, keyBytes, 0, len)
        keySpec = SecretKeySpec(keyBytes, "AES")
    }
}