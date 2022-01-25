package com.neoguri.pensionlottery.dto

class QrLottoNum {

    var lotto_round: String = ""
    var lotto_date: String = ""
    var lotto_time: String = ""
    var lotto_title_item: String = ""
    var lotto_item: String = "0"
    var lottery_date: String = "0"
    var favorit: String = "0"

    var isCheck = false

    override fun toString(): String {
        return "{" +
                "\n  \"lotto_date\":\"" + lotto_date + '\"'.toString() +
                "\n  \"lotto_time\":\"" + lotto_time + '\"'.toString() +
                "\n  \"lotto_title_item\":\"" + lotto_title_item + '\"'.toString() +
                "\n,  \"lotto_item\":\"" + lotto_item + '\"'.toString() +
                "\n,  \"lottery_date\":\"" + lottery_date + '\"'.toString() +
                "\n,  \"favorit\":\"" + favorit + '\"'.toString() +
                "}"
    }

}