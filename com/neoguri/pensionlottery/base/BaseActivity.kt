package com.neoguri.pensionlottery.base

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LifecycleOwner
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.util.LogUtil
import com.neoguri.pensionlottery.util.PrefUtil

open class BaseActivity : AppCompatActivity(), LifecycleOwner {

    protected var mPrefUtil: PrefUtil = PrefUtil()

    protected var mNightMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefUtil = PrefUtil().getInstance(this)

        val nightMode = Integer.toHexString(
            ContextCompat.getColor(
                this,
                R.color.color_statusbar
            ) and 0x00ffffff
        )

        mNightMode = nightMode != "ffffff"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.color_statusbar)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = !mNightMode
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(this, R.color.color_statusbar)
        }

        window.navigationBarColor = ContextCompat.getColor(this, R.color.color_statusbar)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = !mNightMode

    }

    //툴바 제목을 설정하기 위함
    fun setTitle(title: String) {
        val mTitleTextView = findViewById<TextView>(R.id.toolbar_text)
        mTitleTextView.text = title
    }

    fun setBack(backBtn: View) {
        backBtn.setOnClickListener { finish() }
    }

}