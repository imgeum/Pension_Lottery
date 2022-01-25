package com.neoguri.pensionlottery.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.neoguri.pensionlottery.R
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * var keystoreAES = KeystoreAES(this)
 *
 * String(Base64.encode(keystoreAES.encrypt("text"), Base64.DEFAULT))
 *
 * String(sejung.decrypt(keystoreAES.encrypt("text")))
 **/
class KeystoreAES(context: Context) {

    val secretKey: SecretKey
    val alias: String
    val ks : KeyStore
    var iv: ByteArray

    fun encrypt(text: String): ByteArray {
        val cipher_enc = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher_enc.init(Cipher.ENCRYPT_MODE, secretKey)
        iv = cipher_enc.iv
        return cipher_enc.doFinal(text.toByteArray())
    }

    fun decrypt(byteArray: ByteArray): ByteArray {
        val cipher_dec = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher_dec.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        return cipher_dec.doFinal(byteArray)
    }

    init {
        alias = context.resources.getString(R.string.app_name)
        iv = ByteArray(16)
        ks = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        if (ks.containsAlias(alias)) {
            //키가 존재할경우
            val secretKeyEntry = ks.getEntry(alias, null) as KeyStore.SecretKeyEntry
            secretKey = secretKeyEntry.secretKey
        } else {
            //키가 없을경우
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val parameterSpec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).run {
                setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                setDigests(KeyProperties.DIGEST_SHA256)
                setUserAuthenticationRequired(false)
                build()
            }
            keyGenerator.init(parameterSpec)
            secretKey = keyGenerator.generateKey()
        }
    }

}