package com.neoguri.pensionlottery.presentation.fragment.mynumlist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.data.model.lottomyitem.LottoMyItemList
import com.neoguri.pensionlottery.diffutil.LottoMyItemDiffCallback
import com.neoguri.pensionlottery.util.LottoUtil

class PastMyNumListAdapter constructor(context: Context, itemLayout: Int) :
    RecyclerView.Adapter<PastMyNumListAdapter.ViewHolder>() {

    private var itemClick: ItemClick? = null

    private val mContext: Context = context
    private val mItemLayout: Int = itemLayout
    private val mLottoMyItemList: ArrayList<LottoMyItemList> = ArrayList()

    fun updateLottoMyItemListItems(lottoMyItem: ArrayList<LottoMyItemList>) {
        val diffCallback = LottoMyItemDiffCallback(this.mLottoMyItemList, lottoMyItem)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.mLottoMyItemList.clear()
        this.mLottoMyItemList.addAll(lottoMyItem)
        diffResult.dispatchUpdatesTo(this)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int, item: LottoMyItemList)
    }

    fun setItemClick(itemClick: ItemClick) {
        this.itemClick = itemClick
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(mItemLayout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val lottoMyItemList = mLottoMyItemList[position]

        viewHolder.mIdMyNumDate.text = lottoMyItemList.myLottoNum.lotto_date

        val split = lottoMyItemList.myLottoNum.lotto_item.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        initSetText(viewHolder.lottoText1, split[0])
        initSetText(viewHolder.lottoText2, split[1])
        initSetText(viewHolder.lottoText3, split[2])
        initSetText(viewHolder.lottoText4, split[3])
        initSetText(viewHolder.lottoText5, split[4])
        initSetText(viewHolder.lottoText6, split[5])

        LottoUtil.initBonusData(
            mContext,
            viewHolder.lotto7,
            viewHolder.lotto7Taedoori,
            viewHolder.lottoText7,
            lottoMyItemList.myLottoNum.lotto_title_item,
            ContextCompat.getColor(mContext, R.color.color_teadoori_)
        )

        LottoUtil.initBonusData(
            mContext,
            viewHolder.lotto1,
            viewHolder.lotto1Taedoori,
            viewHolder.lottoText1,
            split[0],
            ContextCompat.getColor(mContext, R.color.color_red)
        )

        LottoUtil.initBonusData(
            mContext,
            viewHolder.lotto2,
            viewHolder.lotto2Taedoori,
            viewHolder.lottoText2,
            split[1],
            ContextCompat.getColor(mContext, R.color.color_orange)
        )

        LottoUtil.initBonusData(
            mContext,
            viewHolder.lotto3,
            viewHolder.lotto3Taedoori,
            viewHolder.lottoText3,
            split[2],
            ContextCompat.getColor(mContext, R.color.color_yellow)
        )

        LottoUtil.initBonusData(
            mContext,
            viewHolder.lotto4,
            viewHolder.lotto4Taedoori,
            viewHolder.lottoText4,
            split[3],
            ContextCompat.getColor(mContext, R.color.color_blue)
        )

        LottoUtil.initBonusData(
            mContext,
            viewHolder.lotto5,
            viewHolder.lotto5Taedoori,
            viewHolder.lottoText5,
            split[4],
            ContextCompat.getColor(mContext, R.color.color_purple)
        )

        LottoUtil.initBonusData(
            mContext,
            viewHolder.lotto6,
            viewHolder.lotto6Taedoori,
            viewHolder.lottoText6,
            split[5],
            ContextCompat.getColor(mContext, R.color.color_gray)
        )

        viewHolder.mCheckmViewClick.setOnClickListener{ v ->
            if (itemClick != null) {
                itemClick?.onClick(v, position, lottoMyItemList)
            }
        }

        if (lottoMyItemList.isCheck) {
            viewHolder.mCheckImage.visibility = View.VISIBLE
        } else {
            viewHolder.mCheckImage.visibility = View.GONE
        }

    }

    override fun getItemId(position: Int): Long {
        return mLottoMyItemList[position].myLottoNum._id.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return mLottoMyItemList.size
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

        var mIdMyNumDate: TextView = view.findViewById(R.id.id_my_num_date)

        var mCheckmViewClick: FrameLayout = view.findViewById(R.id.check_view_click)
        var mCheckImage: ImageView = view.findViewById(R.id.check_image)

    }

    private fun initSetText(textView: TextView, lottoNum: String?) {
        textView.text = lottoNum
    }

}