package com.neoguri.pensionlottery.data.model.admincheck


import com.google.gson.annotations.SerializedName

data class AdminCheckEntity(
    @SerializedName("data")
    val `data`: List<AdminCheckData>,
    @SerializedName("result")
    val result: String
)