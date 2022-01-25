package com.neoguri.pensionlottery.dto

import java.util.ArrayList

class PensionLotteryItemList {

    var data: ArrayList<PensionLotteryItem> = ArrayList()

    companion object {

        @Volatile private var instance: ArrayList<PensionLotteryItem>? = null
        @Volatile private var singleInstance: PensionLotteryItem? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: ArrayList<PensionLotteryItem>().also { instance = it }
            }

        fun getSingleInstance() = singleInstance ?: PensionLotteryItemList().PensionLotteryItem()

        fun setLottoItem(qrItem: ArrayList<PensionLotteryItem>?) {
            instance = qrItem
        }
    }

    inner class PensionLotteryItem {
        var code: String = ""
        var date: String = ""
        var first_place_people: String = ""
        var two_place_people: String = ""
        var three_place_people: String = ""
        var fo_place_people: String = ""
        var five_place_people: String = ""
        var six_place_people: String = ""
        var seven_place_people: String = ""
        var pensionlottery_title_num: String = ""
        var pensionlottery_num: String = ""
        var bonus_num: String = ""
        var bonus_place_people: String = ""
        var run: String = ""

        override fun toString(): String {
            return "{" +
                    "\n  \"code\":\"" + code + '\"'.toString() + "," +
                    "\n  \"date\":\"" + date + '\"'.toString() + "," +
                    "\n  \"first_place_people\":\"" + first_place_people + '\"'.toString() + "," +
                    "\n  \"two_place_people\":\"" + two_place_people + '\"'.toString() + "," +
                    "\n  \"three_place_people\":\"" + three_place_people + '\"'.toString() + "," +
                    "\n  \"fo_place_people\":\"" + fo_place_people + '\"'.toString() + "," +
                    "\n  \"five_place_people\":\"" + five_place_people + '\"'.toString() + "," +
                    "\n  \"six_place_people\":\"" + six_place_people + '\"'.toString() + "," +
                    "\n  \"seven_place_people\":\"" + seven_place_people + '\"'.toString() + "," +
                    "\n  \"pensionlottery_title_num\":\"" + pensionlottery_title_num + '\"'.toString() + "," +
                    "\n  \"pensionlottery_num\":\"" + pensionlottery_num + '\"'.toString() + "," +
                    "\n  \"bonus_num\":\"" + bonus_num + '\"'.toString() + "," +
                    "\n  \"bonus_place_people\":\"" + bonus_place_people + '\"'.toString() + "," +
                    "\n  \"run\":\"" + run + '\"'.toString() + "," +
                    "}"
        }
    }

}