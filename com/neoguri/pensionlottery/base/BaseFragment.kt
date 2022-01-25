package com.neoguri.pensionlottery.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.neoguri.pensionlottery.util.PrefUtil

open class BaseFragment: Fragment() {

    protected var mPrefUtil: PrefUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefUtil = PrefUtil().getInstance(requireActivity())

    }

    fun setBack(backBtn: View) {
        backBtn.setOnClickListener { activity?.onBackPressed() }
    }

    fun setDontTouchBtn(dontTouchBtn: View) {
        dontTouchBtn.setOnClickListener { }
    }

}