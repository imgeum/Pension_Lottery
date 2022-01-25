package com.neoguri.pensionlottery.diffutil

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.neoguri.pensionlottery.presentation.db.myLottoNum.MyLottoNum
import java.util.ArrayList

class LottoPastMyItemDiffCallback(oldLottoMyItemList: ArrayList<MyLottoNum>, newLottoMyItemList: ArrayList<MyLottoNum>) :
    DiffUtil.Callback() {
    private val mOldLottoPastMyItemList: ArrayList<MyLottoNum>
    private val mNewLottoPastMyItemList: ArrayList<MyLottoNum>
    override fun getOldListSize(): Int {
        return mOldLottoPastMyItemList.size
    }

    override fun getNewListSize(): Int {
        return mNewLottoPastMyItemList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldLottoPastMyItemList[oldItemPosition]._id.hashCode().toLong() == mNewLottoPastMyItemList[newItemPosition]._id.hashCode().toLong()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: MyLottoNum = mOldLottoPastMyItemList[oldItemPosition]
        val newItem: MyLottoNum = mNewLottoPastMyItemList[newItemPosition]
        return oldItem._id == newItem._id
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

    init {
        mOldLottoPastMyItemList = oldLottoMyItemList
        mNewLottoPastMyItemList = newLottoMyItemList
    }
}