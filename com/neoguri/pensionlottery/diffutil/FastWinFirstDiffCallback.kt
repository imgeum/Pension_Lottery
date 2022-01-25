package com.neoguri.pensionlottery.diffutil

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.dto.PensionLotteryItemList
import java.util.ArrayList

class FastWinFirstDiffCallback(oldBlockNumList: ArrayList<PensionLotteryData>, newBlockNumList: ArrayList<PensionLotteryData>) :
    DiffUtil.Callback() {
    private val mOldBlockNumList: ArrayList<PensionLotteryData>
    private val mNewBlockNumList: ArrayList<PensionLotteryData>
    override fun getOldListSize(): Int {
        return mOldBlockNumList.size
    }

    override fun getNewListSize(): Int {
        return mNewBlockNumList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldBlockNumList[oldItemPosition].code.hashCode().toLong() == mNewBlockNumList[newItemPosition].code.hashCode().toLong()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: PensionLotteryData = mOldBlockNumList[oldItemPosition]
        val newItem: PensionLotteryData = mNewBlockNumList[newItemPosition]
        return oldItem.code == newItem.code
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

    init {
        mOldBlockNumList = oldBlockNumList
        mNewBlockNumList = newBlockNumList
    }
}