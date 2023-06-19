/*
package com.neoguri.pensionlottery.test

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.tech.NfcF
import android.os.*
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.neoguri.battle.R
import com.neoguri.battle.databinding.ActivityNfcBinding
import com.neoguri.presentation.base.BaseActivity
import com.neoguri.presentation.battle.login.LoginActivity
import com.neoguri.presentation.constant.Constant
import com.neoguri.util.BattleUtil.setStatusBarTransparent
import com.neoguri.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import kotlin.system.exitProcess

class NfcActivity : BaseActivity() {

    private lateinit var mBinding: ActivityNfcBinding

    var mUserNFC: String = ""
    var isOkFlag: Boolean = false

    private var mAnimation1: Animation? = null
    private var mAnimation2: Animation? = null
    private var mAnimation3: Animation? = null
    private var mAnimation4: Animation? = null
    private var mAnimation5: Animation? = null
    private var mAnimation6: Animation? = null

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null

    private var intentFiltersArray: Array<IntentFilter>? = null
    private var techListsArray: Array<Array<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityNfcBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setStatusBarTransparent()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(applicationContext, resources.getString(R.string.battle_device_no_nfc), Toast.LENGTH_SHORT)
                .show()
            finish()
            return
        }

        if (!nfcAdapter!!.isEnabled) {

            val builder = AlertDialog.Builder(this, R.style.DialogThem)

            builder.setTitle(resources.getString(R.string.battle_connect_nfc)).setMessage(resources.getString(R.string.battle_setting_nfc))
                .setPositiveButton(resources.getString(R.string.battle_connect)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                }.setNegativeButton(resources.getString(R.string.battle_close)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    finish()
                }

            val dialog = builder.create()

            dialog.setOnShowListener { dialogInterface ->
                (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.dialog_text))
                dialogInterface.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.dialog_text))
            }

            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()

        }

        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, 0)
        }

        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("스타슬러시스타")    */
/* Handles all MIME based dispatches.
                                     You should specify only the ones that you need. *//*

            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
        }

        intentFiltersArray = arrayOf(ndef)
        techListsArray = arrayOf(arrayOf(NfcF::class.java.name))

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMsgs ->
            (rawMsgs[0] as NdefMessage).apply {
                // record 0 contains the MIME type, record 1 is the AAR, if present

                try {

                    val response = String(records[0].payload)

                    mUserNFC = response
                    isOkFlag = mUserNFC != ""

                    val vibrator =
                        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) vibrator.vibrate(
                        VibrationEffect.createOneShot(
                            150,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    ) else vibrator.vibrate(150)

                    val mIntent = Intent(this@NfcActivity, LoginActivity::class.java)
                    if (!isOkFlag) {
                        setResult(Activity.RESULT_CANCELED, mIntent)
                    } else if (isOkFlag) {
                        mIntent.putExtra(Constant.ACTIVITY_RESULT_QR, response)
                        setResult(Activity.RESULT_OK, mIntent)
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(150)
                        if(!isFinishing) finish()
                    }

                } catch (e: UnsupportedEncodingException) {

                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        initLayout()
        if (nfcAdapter != null) nfcAdapter!!.enableForegroundDispatch(
            this,
            pendingIntent,
            intentFiltersArray,
            techListsArray
        )
    }

    private fun initLayout() {
        mAnimation1 = AnimationUtils.loadAnimation(this, R.anim.scale)
        mAnimation2 = AnimationUtils.loadAnimation(this, R.anim.scale)
        mAnimation3 = AnimationUtils.loadAnimation(this, R.anim.scale)
        mAnimation4 = AnimationUtils.loadAnimation(this, R.anim.scale)
        mAnimation5 = AnimationUtils.loadAnimation(this, R.anim.scale)
        mAnimation6 = AnimationUtils.loadAnimation(this, R.anim.scale)

        activityAnimation(mBinding.circleImage1, mAnimation1!!, 0)
        activityAnimation(mBinding.circleImage2, mAnimation2!!, 800)
        activityAnimation(mBinding.circleImage3, mAnimation3!!, 1600)
        activityAnimation(mBinding.circleImage4, mAnimation4!!, 2400)
        activityAnimation(mBinding.circleImage5, mAnimation5!!, 3200)
        activityAnimation(mBinding.circleImage6, mAnimation6!!, 4000)
    }

    private fun activityAnimation(imageView: ImageView, animation: Animation, time: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(time)
            imageView.visibility = View.VISIBLE
            imageView.startAnimation(animation)
        }
    }

    override fun onPause() {
        super.onPause()

        mAnimation1!!.cancel()
        mAnimation2!!.cancel()
        mAnimation3!!.cancel()
        mAnimation4!!.cancel()
        mAnimation5!!.cancel()
        mAnimation6!!.cancel()

        mBinding.circleImage1.visibility = View.GONE
        mBinding.circleImage2.visibility = View.GONE
        mBinding.circleImage3.visibility = View.GONE
        mBinding.circleImage4.visibility = View.GONE
        mBinding.circleImage5.visibility = View.GONE
        mBinding.circleImage6.visibility = View.GONE

        if (nfcAdapter != null) nfcAdapter!!.disableForegroundDispatch(this)

    }

}*/
