package com.neoguri.pensionlottery.util

import android.app.Activity
import android.widget.Toast
import com.neoguri.pensionlottery.R

class BackPressCloseHandler(private val activity: Activity) {
    private var backKeyPressedTime: Long = 0
    private var toast: Toast? = null

    fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            showGuide()
            return
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish()
            toast!!.cancel()
        }
    }

    fun showGuide() {
        toast = Toast.makeText(activity, activity.resources.getString(R.string.all_close_app_confirm), Toast.LENGTH_SHORT)
        toast!!.show()
    }
}