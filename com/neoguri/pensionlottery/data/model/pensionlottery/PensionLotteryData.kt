package com.neoguri.pensionlottery.data.model.pensionlottery


import com.google.gson.annotations.SerializedName

data class PensionLotteryData(
    @SerializedName("code")
    var code: String,
    @SerializedName("date")
    var date: String,
    @SerializedName("first_place_people")
    var firstPlacePeople: String,
    @SerializedName("two_place_people")
    var twoPlacePeople: String,
    @SerializedName("three_place_people")
    var threePlacePeople: String,
    @SerializedName("fo_place_people")
    var foPlacePeople: String,
    @SerializedName("five_place_people")
    var fivePlacePeople: String,
    @SerializedName("six_place_people")
    var sixPlacePeople: String,
    @SerializedName("seven_place_people")
    var sevenPlacePeople: String,
    @SerializedName("pensionlottery_title_num")
    var pensionlotteryTitleNum: String,
    @SerializedName("pensionlottery_num")
    var pensionlotteryNum: String,
    @SerializedName("bonus_num")
    var bonusNum: String,
    @SerializedName("bonus_place_people")
    var bonusPlacePeople: String,
    @SerializedName("run")
    var run: String
)