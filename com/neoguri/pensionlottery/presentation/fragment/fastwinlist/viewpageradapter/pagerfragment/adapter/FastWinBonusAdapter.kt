package com.neoguri.pensionlottery.presentation.fragment.fastwinlist.viewpageradapter.pagerfragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.diffutil.FastWinFirstDiffCallback
import com.neoguri.pensionlottery.dto.PensionLotteryItemList
import java.util.ArrayList

class FastWinBonusAdapter constructor(context: Context, itemLayout: Int) :
    RecyclerView.Adapter<FastWinBonusAdapter.ViewHolder>() {

    private val mContext: Context = context
    private var mLottoItemList: ArrayList<PensionLotteryData> = ArrayList()
    private val mItemLayout: Int = itemLayout

    fun updateBlockNumListItems(lottoPastItem: ArrayList<PensionLotteryData>) {
        val diffCallback = FastWinFirstDiffCallback(this.mLottoItemList, lottoPastItem)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.mLottoItemList.clear()
        this.mLottoItemList.addAll(lottoPastItem)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(mItemLayout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val lottoItemList = mLottoItemList[position]

        val winningResults = lottoItemList.code + mContext.resources.getString(R.string.winning_results)
        val winningDate = "["+lottoItemList.date+"]"

        viewHolder.mIdPastWinningResults.text = winningResults
        viewHolder.mIdPastWinningResultsDay.text = winningDate

        val split = lottoItemList.bonusNum.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        initSetText(viewHolder.lottoText1, split[0])
        initSetText(viewHolder.lottoText2, split[1])
        initSetText(viewHolder.lottoText3, split[2])
        initSetText(viewHolder.lottoText4, split[3])
        initSetText(viewHolder.lottoText5, split[4])
        initSetText(viewHolder.lottoText6, split[5])

        initSetColor(viewHolder.lotto1, viewHolder.lotto1Taedoori, ContextCompat.getColor(mContext, R.color.color_red))
        initSetColor(viewHolder.lotto2, viewHolder.lotto2Taedoori, ContextCompat.getColor(mContext, R.color.color_orange))
        initSetColor(viewHolder.lotto3, viewHolder.lotto3Taedoori, ContextCompat.getColor(mContext, R.color.color_yellow))
        initSetColor(viewHolder.lotto4, viewHolder.lotto4Taedoori, ContextCompat.getColor(mContext, R.color.color_blue))
        initSetColor(viewHolder.lotto5, viewHolder.lotto5Taedoori, ContextCompat.getColor(mContext, R.color.color_purple))
        initSetColor(viewHolder.lotto6, viewHolder.lotto6Taedoori, ContextCompat.getColor(mContext, R.color.color_gray))

    }

    override fun getItemId(position: Int): Long {
        return mLottoItemList[position].code.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return mLottoItemList.size
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        //var mView: View = view

        var lotto1: ImageView = view.findViewById(R.id.bonus_ball_view_lotto1)
        var lotto2: ImageView = view.findViewById(R.id.bonus_ball_view_lotto2)
        var lotto3: ImageView = view.findViewById(R.id.bonus_ball_view_lotto3)
        var lotto4: ImageView = view.findViewById(R.id.bonus_ball_view_lotto4)
        var lotto5: ImageView = view.findViewById(R.id.bonus_ball_view_lotto5)
        var lotto6: ImageView = view.findViewById(R.id.bonus_ball_view_lotto6)
        var lotto1Taedoori: ImageView = view.findViewById(R.id.bonus_ball_view_lotto1_taedoori)
        var lotto2Taedoori: ImageView = view.findViewById(R.id.bonus_ball_view_lotto2_taedoori)
        var lotto3Taedoori: ImageView = view.findViewById(R.id.bonus_ball_view_lotto3_taedoori)
        var lotto4Taedoori: ImageView = view.findViewById(R.id.bonus_ball_view_lotto4_taedoori)
        var lotto5Taedoori: ImageView = view.findViewById(R.id.bonus_ball_view_lotto5_taedoori)
        var lotto6Taedoori: ImageView = view.findViewById(R.id.bonus_ball_view_lotto6_taedoori)
        var lottoText1: TextView = view.findViewById(R.id.bonus_ball_view_lotto_text1)
        var lottoText2: TextView = view.findViewById(R.id.bonus_ball_view_lotto_text2)
        var lottoText3: TextView = view.findViewById(R.id.bonus_ball_view_lotto_text3)
        var lottoText4: TextView = view.findViewById(R.id.bonus_ball_view_lotto_text4)
        var lottoText5: TextView = view.findViewById(R.id.bonus_ball_view_lotto_text5)
        var lottoText6: TextView = view.findViewById(R.id.bonus_ball_view_lotto_text6)

        var mIdPastWinningResults: TextView = view.findViewById(R.id.id_past_winning_results)
        var mIdPastWinningResultsDay: TextView = view.findViewById(R.id.id_past_winning_results_day)

    }

    private fun initSetText(textView: TextView, lottoNum: String?) {
        textView.text = lottoNum
    }

    private fun initSetColor(imageView: ImageView, imageViewTaedoori: ImageView, color: Int) {

        imageViewTaedoori.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.main_loto_circle_taedoori))
        imageViewTaedoori.setColorFilter(color)
        imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.main_loto_circle))
        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.color_background))

    }

}