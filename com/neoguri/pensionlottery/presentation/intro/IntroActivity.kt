package com.neoguri.pensionlottery.presentation.intro

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.base.BaseActivity
import com.neoguri.pensionlottery.constant.URLs
import com.neoguri.pensionlottery.databinding.ActivityIntroBinding
import com.neoguri.pensionlottery.presentation.db.allLottoNum.AllLottoNum
import com.neoguri.pensionlottery.presentation.lotto.MainActivity
import com.neoguri.pensionlottery.presentation.lotto.PensionLotteryViewModel
import kotlin.system.exitProcess

class IntroActivity : BaseActivity() {

    private lateinit var mBinding: ActivityIntroBinding

    private val viewModel: PensionLotteryViewModel by viewModels()

    private var isAllFirstCheck = false
    private var isAllSecondCheck = false

    private var mAppVersion = ""
    private lateinit var mAllLottoNum: List<AllLottoNum>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if(mNightMode){
            mBinding.appIconImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.intro_night))
        } else {
            mBinding.appIconImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.intro))
        }

        viewModel.lottoVersionJson.observe(this) { lottoVersion ->
            // Update the cached copy of the words in the adapter.
            lottoVersion?.let {
                if(it != ""){
                    mAppVersion = it
                    isAllFirstCheck = true
                    isAllCheck(isAllFirstCheck, isAllSecondCheck)
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.network_check),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.mLiveRoomLottoNum.observe(this) { lottoNums ->
            // Update the cached copy of the words in the adapter.
            lottoNums?.let {
                mAllLottoNum = it
                isAllSecondCheck = true
                isAllCheck(isAllFirstCheck, isAllSecondCheck)
            }
        }

        getLottoFirst()

    }

    private fun isAllCheck(first: Boolean, second: Boolean) {
        if (first && second) {
            if (mAppVersion.isEmpty()) {
                alertDialogStart(resources.getString(R.string.network_check))
            } else {
                getMyVersionGet(mAppVersion)
            }
        }
    }

    private fun activityStart() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getLottoFirst() {
        viewModel.selectGetVersion(
            URLs.JSON_,
            URLs.ANDROID_GET_VERSION
        )
    }

    private fun getMyVersionGet(androidVersion: String) {
        var versionCheck = false

        try {
            val appVersion = packageManager.getPackageInfo(packageName, 0).versionName

            val versionString = androidVersion.replace(".", "")
            val deviceVersionString = appVersion.replace(".", "")

            if (java.lang.Double.parseDouble(versionString) > java.lang.Double.parseDouble(
                    deviceVersionString
                )
            ) {
                versionCheck = true
            } else if (java.lang.Double.parseDouble(versionString) <= java.lang.Double.parseDouble(
                    deviceVersionString
                )
            ) {
                versionCheck = false
            }

            if (versionCheck) {
                val builder = AlertDialog.Builder(this, R.style.DialogThem)

                builder.setTitle("알림").setMessage("새로운 버전이 있습니다.\n보다 나은 서비스를 위해 업데이트 해 주세요.")
                    .setPositiveButton("업데이트") { _, _ ->
                        val marketLaunch = Intent(Intent.ACTION_VIEW)
                        marketLaunch.data = Uri.parse("market://details?id=com.neoguri.pensionlottery")
                        startActivity(marketLaunch)
                        exitProcess(0)
                    }.setNegativeButton("닫기") { _, _ -> finish() }
                    .setNeutralButton("나중에") { _, _ -> activityStart() }

                val dialog = builder.create()

                dialog.setOnShowListener { dialogInterface ->
                    (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.alram_text))
                    dialogInterface.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.alram_text))
                    dialogInterface.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setTextColor(ContextCompat.getColor(this, R.color.alram_text))
                }

                dialog.show()
            } else {
                activityStart()
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun alertDialogStart(failText: String) {

        if(!isFinishing){
            val builder = AlertDialog.Builder(this, R.style.DialogThem)

            if(mAllLottoNum.isEmpty()){
                builder.setTitle("알림").setMessage(failText.toSpanned())
                    .setPositiveButton("재시도") { _, _ ->
                        getLottoFirst()
                    }.setNegativeButton("닫기") { _, _ -> finish() }

                val dialog = builder.create()

                dialog.setOnShowListener { dialogInterface ->
                    (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.alram_text))
                    dialogInterface.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.alram_text))
                }
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
            } else {
                val lottoFirstPlacePeople = failText + ".<br/>" + "내부데이터를 통한 " + "<font color=\"" + ContextCompat.getColor(this,
                    R.color.colorAccent
                ) +"\">" +
                        "강제실행" +
                        "</font>" + "을 할 수 있습니다."

                builder.setTitle("알림").setMessage(lottoFirstPlacePeople.toSpanned())
                    .setNeutralButton("강제실행") { _, _ ->
                        activityStart()
                    }.setPositiveButton("재시도") { _, _ ->
                        getLottoFirst()
                    }.setNegativeButton("닫기") { _, _ -> finish() }

                val dialog = builder.create()

                dialog.setOnShowListener { dialogInterface ->
                    (dialogInterface as AlertDialog).getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                    dialogInterface.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.alram_text))
                    dialogInterface.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.alram_text))
                }
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
            }
        }

    }

    private fun String.toSpanned(): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            return Html.fromHtml(this)
        }
    }

}
