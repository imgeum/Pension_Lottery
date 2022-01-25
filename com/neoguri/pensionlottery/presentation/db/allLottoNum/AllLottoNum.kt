package com.neoguri.pensionlottery.presentation.db.allLottoNum

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pension_lottery_table")
data class AllLottoNum(
    val code: String,
    val date: String,
    val first_place_people: String,
    val two_place_people: String,
    val three_place_people: String,
    val fo_place_people: String,
    val five_place_people: String,
    var six_place_people: String,
    var seven_place_people: String,
    var pensionlottery_title_num: String,
    var pensionlottery_num: String,
    var bonus_num: String,
    var bonus_place_people: String,
    var run: String
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Int = 0
}