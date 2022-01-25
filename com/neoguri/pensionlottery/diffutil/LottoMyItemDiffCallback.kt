package com.neoguri.pensionlottery.diffutil

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.neoguri.pensionlottery.data.model.lottomyitem.LottoMyItemList
import java.util.*

class LottoMyItemDiffCallback(oldLottoMyItemList: ArrayList<LottoMyItemList>, newLottoMyItemList: ArrayList<LottoMyItemList>) :
    DiffUtil.Callback() {
    private val mOldLottoMyItemList: ArrayList<LottoMyItemList>
    private val mNewLottoMyItemList: ArrayList<LottoMyItemList>
    override fun getOldListSize(): Int {
        return mOldLottoMyItemList.size
    }

    override fun getNewListSize(): Int {
        return mNewLottoMyItemList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldLottoMyItemList[oldItemPosition].myLottoNum._id.hashCode().toLong() == mNewLottoMyItemList[newItemPosition].myLottoNum._id.hashCode().toLong()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: LottoMyItemList = mOldLottoMyItemList[oldItemPosition]
        val newItem: LottoMyItemList = mNewLottoMyItemList[newItemPosition]
        return oldItem.isCheck == newItem.isCheck
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

    init {
        mOldLottoMyItemList = oldLottoMyItemList
        mNewLottoMyItemList = newLottoMyItemList
    }
}