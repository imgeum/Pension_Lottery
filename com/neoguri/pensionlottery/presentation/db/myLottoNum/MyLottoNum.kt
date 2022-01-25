package com.neoguri.pensionlottery.presentation.db.myLottoNum

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_pension_lottery_table")
data class MyLottoNum(
    val lotto_date: String,
    val lotto_time: String,
    val lotto_title_item: String,
    val lotto_item: String,
    val lottery_date: String,
    val favorit: String
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Int = 0
}