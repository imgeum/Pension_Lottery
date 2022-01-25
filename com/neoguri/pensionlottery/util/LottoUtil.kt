package com.neoguri.pensionlottery.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.dto.PensionLotteryItemList
import com.neoguri.pensionlottery.dto.QrLottoNum
import java.text.SimpleDateFormat
import java.util.*

object LottoUtil {
    private var mImageViewList = ArrayList<ImageView>()
    private var mTextViewList = ArrayList<TextView>()
    private var mImageViewTaedooriList = ArrayList<ImageView>()

    fun initLottoSetting(
        context: Context?,
        lottoItemList: ArrayList<PensionLotteryData>,
        position: Int?,
        imageViewList: ArrayList<ImageView>,
        imageViewTaedooriList: ArrayList<ImageView>,
        textViewList: ArrayList<TextView>,
        lotto7: ImageView,
        lotto7Taedoori: ImageView,
        lotto_text7: TextView
    ) {

        this.mImageViewList = imageViewList
        this.mTextViewList = textViewList
        this.mImageViewTaedooriList = imageViewTaedooriList

        context?.let { initLottoData(it, lottoItemList[position!!].pensionlotteryNum) }
        context?.let { initBonusData(
            it,
            lotto7,
            lotto7Taedoori,
            lotto_text7,
            lottoItemList[position!!].pensionlotteryTitleNum,
            ContextCompat.getColor(context, R.color.color_teadoori_)
        ) }

    }

    fun initLottoSetting(
        context: Context?,
        lottoItemList: ArrayList<PensionLotteryData>,
        position: Int?,
        imageViewList: ArrayList<ImageView>,
        imageViewTaedooriList: ArrayList<ImageView>,
        textViewList: ArrayList<TextView>,
        checkBonus: Boolean
    ) {

        this.mImageViewList = imageViewList
        this.mTextViewList = textViewList
        this.mImageViewTaedooriList = imageViewTaedooriList

        context?.let {
            if(checkBonus){
                initLottoData(it, lottoItemList[position!!].bonusNum)
            } else {
                initLottoData(it, lottoItemList[position!!].pensionlotteryNum)
            }
        }

    }

    private fun initLottoData(context: Context, lottoNum: String?) {

        val split = lottoNum!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        initBonusData(
            context,
            mImageViewList[0],
            mImageViewTaedooriList[0],
            mTextViewList[0],
            split[0],
            ContextCompat.getColor(context, R.color.color_red)
        )
        initBonusData(
            context,
            mImageViewList[1],
            mImageViewTaedooriList[1],
            mTextViewList[1],
            split[1],
            ContextCompat.getColor(context, R.color.color_orange)
        )
        initBonusData(
            context,
            mImageViewList[2],
            mImageViewTaedooriList[2],
            mTextViewList[2],
            split[2],
            ContextCompat.getColor(context, R.color.color_yellow)
        )
        initBonusData(
            context,
            mImageViewList[3],
            mImageViewTaedooriList[3],
            mTextViewList[3],
            split[3],
            ContextCompat.getColor(context, R.color.color_blue)
        )
        initBonusData(
            context,
            mImageViewList[4],
            mImageViewTaedooriList[4],
            mTextViewList[4],
            split[4],
            ContextCompat.getColor(context, R.color.color_purple)
        )
        initBonusData(
            context,
            mImageViewList[5],
            mImageViewTaedooriList[5],
            mTextViewList[5],
            split[5],
            ContextCompat.getColor(context, R.color.color_gray)
        )

    }

    fun initBonusData(
        context: Context,
        imageView: ImageView,
        imageViewTaedoori: ImageView,
        textView: TextView,
        lottoBonus: String?,
        color: Int
    ) {
        textView.text = lottoBonus

        imageViewTaedoori.setImageResource(R.drawable.main_loto_circle_taedoori)
        imageViewTaedoori.setColorFilter(color)
        imageView.setImageResource(R.drawable.main_loto_circle)
        imageView.setColorFilter(ContextCompat.getColor(context, R.color.color_background))
    }

    fun expandMain(ctx: Context, v: View, sv: ScrollView) { // 열기
        v.measure(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ctx.resources.getDimensionPixelSize(R.dimen.dimen_43)
        )
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

                v.layoutParams.height = if (interpolatedTime == 1f)
                    ViewGroup.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = 400
        v.startAnimation(a)
    }

    fun collapseMain(v: View) { // 닫기
        val initialHeight = v.measuredHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun nowDate(): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        return sdf.format(date)
    }

    fun nowTime(): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
        return sdf.format(date)
    }

    fun setAdmob(v: View, heightInPixels: Int) {
        v.layoutParams.height = heightInPixels
        v.requestLayout()
    }

    fun setLayout(v: View, heightInPixels: Int) {
        v.layoutParams.height = heightInPixels
        v.requestLayout()
    }

    fun setMarginBottom(v: View, bottom: Int) {
        val params = v.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            params.leftMargin, params.topMargin,
            params.rightMargin, bottom
        )
    }

    fun isNumeric(s: String): Boolean {
        return try {
            s.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

}