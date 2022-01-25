package com.neoguri.pensionlottery.util

import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.dto.PensionLotteryItemList
import org.json.JSONException
import org.json.JSONObject

class JsonTransmission {

    fun resultJsonLottoNums(my_lotto_count: String): String {
        var get_all_lotto_req: String? = null
        try {
            val sObject = JSONObject() //배열 내에 들어갈 json
            run {
                sObject.put("my_lotto_count", my_lotto_count)
            }
            get_all_lotto_req = sObject.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        get_all_lotto_req = JsonReplaceUtil().replaceString(get_all_lotto_req!!)
        return get_all_lotto_req
    }

    fun adminLotto(mLottoNums: PensionLotteryData, input_type: String): String {
        var get_all_shop_req: String? = null
        try {
            val sObject = JSONObject() //배열 내에 들어갈 json
            run {
                sObject.put("code", mLottoNums.code)
                sObject.put("first_place_people", mLottoNums.firstPlacePeople)
                sObject.put("two_place_people", mLottoNums.twoPlacePeople)
                sObject.put("three_place_people", mLottoNums.threePlacePeople)
                sObject.put("fo_place_people", mLottoNums.foPlacePeople)
                sObject.put("five_place_people", mLottoNums.fivePlacePeople)
                sObject.put("six_place_people", mLottoNums.sixPlacePeople)
                sObject.put("seven_place_people", mLottoNums.sevenPlacePeople)
                sObject.put("bonus_place_people", mLottoNums.bonusPlacePeople)
                sObject.put("date", mLottoNums.date)
                sObject.put("pensionlottery_title_num", mLottoNums.pensionlotteryTitleNum)
                sObject.put("pensionlottery_num", mLottoNums.pensionlotteryNum)
                sObject.put("bonus_num", mLottoNums.bonusNum)
                sObject.put("run", mLottoNums.run)
                sObject.put("input_type", input_type)
            }
            get_all_shop_req = sObject.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        get_all_shop_req = JsonReplaceUtil().replaceString(get_all_shop_req!!)
        return get_all_shop_req
    }

}