package com.neoguri.pensionlottery.util

import android.content.Context
import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.neoguri.pensionlottery.R

object LottoColorUtil {

    fun initLottoColorSetting(context: Context, imageView: ImageView) {

        imageView.setColorFilter(ContextCompat.getColor(context, R.color.color_main_icon), PorterDuff.Mode.SRC_IN) // 색 적용

    }

    fun initLeftArrowColorSetting(context: Context, imageView: ImageView) {

        imageView.setColorFilter(ContextCompat.getColor(context, R.color.hamberger_arrow), PorterDuff.Mode.SRC_IN) // 색 적용

    }

    fun initSearchColorSetting(context: Context, imageView: ImageView) {

        imageView.setColorFilter(ContextCompat.getColor(context, R.color.color_search_color), PorterDuff.Mode.SRC_IN) // 색 적용

    }

}