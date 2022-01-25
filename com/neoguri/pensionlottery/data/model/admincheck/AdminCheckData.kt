package com.neoguri.pensionlottery.data.model.admincheck


import com.google.gson.annotations.SerializedName

data class AdminCheckData(
    @SerializedName("code")
    val code: Int,
    @SerializedName("first_place_people")
    val firstPlacePeople: String,
    @SerializedName("pensionlottery_num")
    val pensionlotteryNum: String
)