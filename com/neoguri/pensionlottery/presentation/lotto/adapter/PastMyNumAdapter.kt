package com.neoguri.pensionlottery.presentation.lotto.adapter

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.diffutil.LottoPastMyItemDiffCallback
import com.neoguri.pensionlottery.presentation.db.myLottoNum.MyLottoNum
import com.neoguri.pensionlottery.util.LottoUtil
import java.util.*

class PastMyNumAdapter constructor(context: Context, itemLayout: Int) :
    RecyclerView.Adapter<PastMyNumAdapter.ViewHolder>() {

    private val mContext: Context = context
    private var mMyLottoNum: ArrayList<MyLottoNum> = ArrayList()
    private val mItemLayout: Int = itemLayout

    private var mLottoTitleNum: String = ""
    private var mLottoNum: String = ""

    private var mLottoBonusNum: String = ""

    fun getLottoNum(lottoTitleNum: String, lottoNum: String, bonusNum: String) {
        mLottoTitleNum = lottoTitleNum
        mLottoNum = lottoNum
        mLottoBonusNum = bonusNum
    }

    fun updatePastMyNum(lottoPastMyItem: ArrayList<MyLottoNum>) {
        val diffCallback = LottoPastMyItemDiffCallback(this.mMyLottoNum, lottoPastMyItem)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.mMyLottoNum.clear()
        this.mMyLottoNum.addAll(lottoPastMyItem)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(mItemLayout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val myLottoNum = mMyLottoNum[position]

        val imageViewList = ArrayList<ImageView>()
        val imageViewTaedooriList = ArrayList<ImageView>()
        val textViewList = ArrayList<TextView>()
        val imageColor = ArrayList<Int>()

        initSetting(imageViewList, imageViewTaedooriList, textViewList, imageColor, viewHolder)
        initSetColor(imageViewList, imageViewTaedooriList)
        initSetColor(viewHolder.lotto7, viewHolder.lotto7Taedoori)

        var rankCount = 0

        val firstSplit = mLottoNum.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val split = myLottoNum.lotto_item.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        initSetText(viewHolder.lottoText1, split[0])
        initSetText(viewHolder.lottoText2, split[1])
        initSetText(viewHolder.lottoText3, split[2])
        initSetText(viewHolder.lottoText4, split[3])
        initSetText(viewHolder.lottoText5, split[4])
        initSetText(viewHolder.lottoText6, split[5])
        initSetText(viewHolder.lottoText7, myLottoNum.lotto_title_item)

        for(i in 5 downTo 0){
            if(firstSplit[i] == split[i]){
                rankCount += 1
                LottoUtil.initBonusData(
                    mContext,
                    imageViewList[i],
                    imageViewTaedooriList[i],
                    textViewList[i],
                    split[i],
                    imageColor[i]
                )
            } else {
                break
            }
        }

        if(rankCount == 6){
            if(mLottoTitleNum == myLottoNum.lotto_title_item){
                rankCount += 1
                LottoUtil.initBonusData(
                    mContext,
                    viewHolder.lotto7,
                    viewHolder.lotto7Taedoori,
                    viewHolder.lottoText7,
                    myLottoNum.lotto_title_item,
                    ContextCompat.getColor(mContext, R.color.color_teadoori_)
                )
            }
        }

        when (rankCount) {
            1 -> {
                viewHolder.mIdPastWinningResultsMoney.text = settingText(R.string.popup_window_7nd, R.string.win_7_price).toSpanned()
            }
            2 -> {
                viewHolder.mIdPastWinningResultsMoney.text = settingText(R.string.popup_window_6nd, R.string.win_6_price).toSpanned()
            }
            3 -> {
                viewHolder.mIdPastWinningResultsMoney.text = settingText(R.string.popup_window_5nd, R.string.win_5_price).toSpanned()
            }
            4 -> {
                viewHolder.mIdPastWinningResultsMoney.text = settingText(R.string.popup_window_4nd, R.string.win_4_price).toSpanned()
            }
            5 -> {
                viewHolder.mIdPastWinningResultsMoney.text = settingText(R.string.popup_window_3nd, R.string.win_3_price).toSpanned()
            }
            6 -> {
                viewHolder.mIdPastWinningResultsMoney.text = settingText(R.string.popup_window_2nd, R.string.win_2_price).toSpanned()
            }
            7 -> {
                viewHolder.mIdPastWinningResultsMoney.text = settingText(R.string.popup_window_first_place, R.string.win_1_price).toSpanned()
                val autoPastMinusTextString = mContext.getString(R.string.popup_window_first_place) + " : " + mContext.getString(R.string.win_1_price)
                viewHolder.mIdPastWinningResultsMoneyAni.text = autoPastMinusTextString
            }
        }

        viewHolder.joText.visibility = View.VISIBLE

        if (rankCount == 0) {
            viewHolder.mIdPastWinningResultsMoney.text = mContext.resources.getString(R.string.next_time)
        }

        if(rankCount < 6){
            val result = bonusCheck(mLottoBonusNum, myLottoNum.lotto_item)

            if(result){
                viewHolder.joText.visibility = View.GONE

                viewHolder.mIdPastWinningResultsMoney.text = settingText(R.string.popup_window_bonus, R.string.win_bonus_price).toSpanned()

                viewHolder.lotto7.visibility = View.GONE
                viewHolder.lotto7Taedoori.visibility = View.GONE
                viewHolder.lottoText7.visibility = View.GONE

                val bonusSplit = mLottoBonusNum.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                for(i in 5 downTo 0){
                    if(bonusSplit[i] == split[i]){
                        LottoUtil.initBonusData(
                            mContext,
                            imageViewList[i],
                            imageViewTaedooriList[i],
                            textViewList[i],
                            split[i],
                            imageColor[i]
                        )
                    } else {
                        break
                    }
                }
            }
        }

        val colorAnimation = ValueAnimator.ofObject(
            ArgbEvaluator(),
            ContextCompat.getColor(mContext, R.color.orderConfirmRed),
            ContextCompat.getColor(mContext, R.color.colorYellow)
        )

        startTextColorAnimation(viewHolder.mIdPastWinningResultsMoneyAni, colorAnimation)

        if(rankCount < 7){
            viewHolder.mIdPastWinningResultsMoney.visibility = View.VISIBLE
            viewHolder.mIdPastWinningResultsMoneyAni.visibility = View.GONE
        } else {
            viewHolder.mIdPastWinningResultsMoney.visibility = View.GONE
            viewHolder.mIdPastWinningResultsMoneyAni.visibility = View.VISIBLE
        }

    }

    override fun getItemId(position: Int): Long {
        return mMyLottoNum[position]._id.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return mMyLottoNum.size
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        //var mView: View = view

        var lotto1: ImageView = view.findViewById(R.id.lotto1)
        var lotto2: ImageView = view.findViewById(R.id.lotto2)
        var lotto3: ImageView = view.findViewById(R.id.lotto3)
        var lotto4: ImageView = view.findViewById(R.id.lotto4)
        var lotto5: ImageView = view.findViewById(R.id.lotto5)
        var lotto6: ImageView = view.findViewById(R.id.lotto6)
        var lotto7: ImageView = view.findViewById(R.id.lotto7)
        var lotto1Taedoori: ImageView = view.findViewById(R.id.lotto1_taedoori)
        var lotto2Taedoori: ImageView = view.findViewById(R.id.lotto2_taedoori)
        var lotto3Taedoori: ImageView = view.findViewById(R.id.lotto3_taedoori)
        var lotto4Taedoori: ImageView = view.findViewById(R.id.lotto4_taedoori)
        var lotto5Taedoori: ImageView = view.findViewById(R.id.lotto5_taedoori)
        var lotto6Taedoori: ImageView = view.findViewById(R.id.lotto6_taedoori)
        var lotto7Taedoori: ImageView = view.findViewById(R.id.lotto7_taedoori)
        var lottoText1: TextView = view.findViewById(R.id.lotto_text1)
        var lottoText2: TextView = view.findViewById(R.id.lotto_text2)
        var lottoText3: TextView = view.findViewById(R.id.lotto_text3)
        var lottoText4: TextView = view.findViewById(R.id.lotto_text4)
        var lottoText5: TextView = view.findViewById(R.id.lotto_text5)
        var lottoText6: TextView = view.findViewById(R.id.lotto_text6)
        var lottoText7: TextView = view.findViewById(R.id.lotto_text7)

        var joText: TextView = view.findViewById(R.id.jo_text)

        var mIdMyNumDate: TextView = view.findViewById(R.id.id_my_num_date)
        var mIdPastWinningResultsMoney: TextView = view.findViewById(R.id.id_past_winning_results_money)
        var mIdPastWinningResultsMoneyAni: TextView = view.findViewById(R.id.id_past_winning_results_money_ani)

        var mFirstWinning: LinearLayout = view.findViewById(R.id.first_winning)

    }

    private fun initSetText(textView: TextView, lottoNum: String?) {
        textView.text = lottoNum
    }

    private fun startTextColorAnimation(
        idPastWinningResultsMoney: TextView,
        colorAnimation: ValueAnimator?
    ) {
        colorAnimation!!.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            idPastWinningResultsMoney.setTextColor(animator.animatedValue as Int)
        }
        colorAnimation.repeatCount = ValueAnimator.INFINITE
        colorAnimation.repeatMode = ValueAnimator.REVERSE

        colorAnimation.start()
    }

    private fun settingText(popupWindow: Int, winPrice: Int): String{
        val text = mContext.resources.getString(popupWindow)
        val textPrice = mContext.resources.getString(winPrice)
        val textColor = Integer.toHexString(
            ContextCompat.getColor(
                mContext,
                R.color.text_color
            ) and 0x00ffffff
        )
        var autoPastMinusTextString = "<font color=\"#FF0000\">$text :</font>"
        autoPastMinusTextString = "$autoPastMinusTextString<font color=\"$textColor\"> $textPrice</font>"

        return autoPastMinusTextString
    }

    private fun initSetting(
        imageViewList: ArrayList<ImageView>,
        imageViewTaedooriList: ArrayList<ImageView>,
        textViewList: ArrayList<TextView>,
        imageColor: ArrayList<Int>,
        viewHolder: ViewHolder
    ) {
        imageViewList.clear()
        imageViewTaedooriList.clear()
        textViewList.clear()
        imageColor.clear()

        imageViewList.add(viewHolder.lotto1)
        imageViewList.add(viewHolder.lotto2)
        imageViewList.add(viewHolder.lotto3)
        imageViewList.add(viewHolder.lotto4)
        imageViewList.add(viewHolder.lotto5)
        imageViewList.add(viewHolder.lotto6)

        imageViewTaedooriList.add(viewHolder.lotto1Taedoori)
        imageViewTaedooriList.add(viewHolder.lotto2Taedoori)
        imageViewTaedooriList.add(viewHolder.lotto3Taedoori)
        imageViewTaedooriList.add(viewHolder.lotto4Taedoori)
        imageViewTaedooriList.add(viewHolder.lotto5Taedoori)
        imageViewTaedooriList.add(viewHolder.lotto6Taedoori)

        textViewList.add(viewHolder.lottoText1)
        textViewList.add(viewHolder.lottoText2)
        textViewList.add(viewHolder.lottoText3)
        textViewList.add(viewHolder.lottoText4)
        textViewList.add(viewHolder.lottoText5)
        textViewList.add(viewHolder.lottoText6)

        imageColor.add(ContextCompat.getColor(mContext, R.color.color_red))
        imageColor.add(ContextCompat.getColor(mContext, R.color.color_orange))
        imageColor.add(ContextCompat.getColor(mContext, R.color.color_yellow))
        imageColor.add(ContextCompat.getColor(mContext, R.color.color_blue))
        imageColor.add(ContextCompat.getColor(mContext, R.color.color_purple))
        imageColor.add(ContextCompat.getColor(mContext, R.color.color_gray))
    }

    private fun initSetColor(
        imageViewList: ArrayList<ImageView>,
        imageViewTaedooriList: ArrayList<ImageView>
    ) {
        for(i in imageViewList.indices){
            imageViewList[i].setImageDrawable(null)
            imageViewTaedooriList[i].setImageDrawable(null)
        }
    }

    private fun initSetColor(
        imageViewList: ImageView,
        imageViewTaedooriList: ImageView
    ) {
        imageViewList.setImageDrawable(null)
        imageViewTaedooriList.setImageDrawable(null)
    }

    private fun bonusCheck(lottoBonusNum: String, lottoItem: String): Boolean {
        var rankCount = 0

        val bonusSplit = lottoBonusNum.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val split = lottoItem.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for(i in 5 downTo 0){
            if(bonusSplit[i] == split[i]){
                rankCount += 1
            } else {
                break
            }
        }

        return rankCount == 6
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