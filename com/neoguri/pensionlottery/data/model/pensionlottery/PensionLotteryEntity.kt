package com.neoguri.pensionlottery.data.model.pensionlottery


import com.google.gson.annotations.SerializedName

data class PensionLotteryEntity(
    @SerializedName("data")
    val data: ArrayList<PensionLotteryData>,
    @SerializedName("result")
    val result: String
)