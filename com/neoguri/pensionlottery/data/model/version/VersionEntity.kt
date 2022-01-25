package com.neoguri.pensionlottery.data.model.version


import com.google.gson.annotations.SerializedName

data class VersionEntity(
    @SerializedName("android_version")
    val androidVersion: String,
    @SerializedName("result")
    val result: String
)