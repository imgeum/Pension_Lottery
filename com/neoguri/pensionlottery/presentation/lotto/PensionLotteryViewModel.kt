package com.neoguri.pensionlottery.presentation.lotto

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.neoguri.pensionlottery.R
import com.neoguri.pensionlottery.constant.URLs
import com.neoguri.pensionlottery.data.model.admincheck.AdminCheckEntity
import com.neoguri.pensionlottery.data.model.admininsert.AdminLottoEntity
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryData
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryEntity
import com.neoguri.pensionlottery.data.model.version.VersionEntity
import com.neoguri.pensionlottery.data.repository.LottoRepository
import com.neoguri.pensionlottery.dto.QrLottoNum
import com.neoguri.pensionlottery.presentation.db.allLottoNum.AllLottoNum
import com.neoguri.pensionlottery.presentation.db.allLottoNum.AllLottoNumRepository
import com.neoguri.pensionlottery.presentation.db.databases.LottoNumRoomDatabase
import com.neoguri.pensionlottery.presentation.db.myLottoNum.MyLottoNum
import com.neoguri.pensionlottery.presentation.db.myLottoNum.MyLottoNumRepository
import com.neoguri.pensionlottery.util.JsonTransmission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PensionLotteryViewModel(application: Application) : AndroidViewModel(application) {

    private var allLottoNumRepository: AllLottoNumRepository
    private var myLottoNumRepository: MyLottoNumRepository

    val mAllLottoNum = ArrayList<PensionLotteryData>()
    val mLiveRoomLottoNum: LiveData<List<AllLottoNum>>
    val mMyLottoNum: LiveData<List<MyLottoNum>>

    val mRJLottoNum: MutableLiveData<ArrayList<PensionLotteryData>> by lazy {
        MutableLiveData<ArrayList<PensionLotteryData>>()
    }

    val lottoItemAdminCheck: MutableLiveData<AdminCheckEntity> by lazy {
        MutableLiveData<AdminCheckEntity>()
    }

    //FastWinListFragment//
    val winnigStoreFirst: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val winnigStoreBonus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun winnigStoreFirstSuccess(check: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        winnigStoreFirst.postValue(check)
    }

    fun winnigStoreBonusSuccess(check: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        winnigStoreBonus.postValue(check)
    }
    //FastWinListFragment//

    //MyNumChoiceFragment//
    fun insertMyNum(myLottoNum: MyLottoNum) = viewModelScope.launch(Dispatchers.IO) {
        myLottoNumRepository.insert(myLottoNum)
    }
    //MyNumChoiceFragment//

    //MyNumListFragment//
    fun deleteMyNum(myLottoNum: MyLottoNum) = viewModelScope.launch(Dispatchers.IO) {
        myLottoNumRepository.delete(myLottoNum)
    }
    //MyNumListFragment//

    //QRCodeFragment
    val qrStartCheck: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val myQRLottoNumItemList: MutableLiveData<ArrayList<QrLottoNum>> by lazy {
        MutableLiveData<ArrayList<QrLottoNum>>()
    }

    fun myQRLottoNum(
        roomLottoNum: ArrayList<QrLottoNum>
    ) {
        myQRLottoNumItemList.postValue(roomLottoNum)
    }
    //QRCodeFragment

    //IntroActivity
    val lottoVersionJson: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    //IntroActivity

    init {
        val allLottoNumDao =
            LottoNumRoomDatabase.getDatabase(application, viewModelScope).allLottoNumDao()
        allLottoNumRepository = AllLottoNumRepository(allLottoNumDao)
        mLiveRoomLottoNum = allLottoNumRepository.allLottoNum

        val myLottoNumDao =
            LottoNumRoomDatabase.getDatabase(application, viewModelScope).myLottoNumDao()
        myLottoNumRepository = MyLottoNumRepository(myLottoNumDao)
        mMyLottoNum = myLottoNumRepository.myLottoNum
    }

    fun selectGetVersion(
        context: Context,
        php: String,
        url: String
    ) {
        LottoRepository.getVersionInfo(
            php,
            url,
            object : LottoRepository.GetDataCallback<VersionEntity> {
                override fun onSuccess(data: VersionEntity?) {
                    data?.let {
                        lottoVersionJson.postValue(it.androidVersion)
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.network_check),
                        Toast.LENGTH_SHORT
                    ).show()
                    lottoVersionJson.postValue("")
                    throwable.printStackTrace()
                }
            })
    }

    private fun insertRoom(data: ArrayList<PensionLotteryData>) =
        viewModelScope.launch(Dispatchers.IO) {
            val dbInsertLottoNum = ArrayList<AllLottoNum>()
            for (i in data.indices) {
                if (data[i].run != "N") {
                    dbInsertLottoNum.add(
                        AllLottoNum(
                            data[i].code,
                            data[i].date,
                            data[i].firstPlacePeople,
                            data[i].twoPlacePeople,
                            data[i].threePlacePeople,
                            data[i].foPlacePeople,
                            data[i].fivePlacePeople,
                            data[i].sixPlacePeople,
                            data[i].sevenPlacePeople,
                            data[i].pensionlotteryTitleNum,
                            data[i].pensionlotteryNum,
                            data[i].bonusNum,
                            data[i].bonusPlacePeople,
                            data[i].run
                        )
                    )
                }
            }
            if (dbInsertLottoNum.size > 0) {
                allLottoNumRepository.insertAll(dbInsertLottoNum)
            }
        }

    fun roomGetLottoSum(
        roomLottoNum: List<AllLottoNum>
    ) = viewModelScope.launch(Dispatchers.IO) {
        mAllLottoNum.clear()

        for (i in roomLottoNum.indices) {
            val lottoNums = PensionLotteryData(
                roomLottoNum[i].code,
                roomLottoNum[i].date,
                roomLottoNum[i].first_place_people,
                roomLottoNum[i].two_place_people,
                roomLottoNum[i].three_place_people,
                roomLottoNum[i].fo_place_people,
                roomLottoNum[i].five_place_people,
                roomLottoNum[i].six_place_people,
                roomLottoNum[i].seven_place_people,
                roomLottoNum[i].pensionlottery_title_num,
                roomLottoNum[i].pensionlottery_num,
                roomLottoNum[i].bonus_num,
                roomLottoNum[i].bonus_place_people,
                roomLottoNum[i].run
            )
            mAllLottoNum.add(lottoNums)
        }
        selectGetLottoNum(
            JsonTransmission().resultJsonLottoNums((roomLottoNum.size + 1).toString())
        )

    }

    private fun selectGetLottoNum(jsonString: String) {
        LottoRepository.getLottoInfo(
            URLs.JSON_,
            URLs.SELECT_PENSION_LOTTERY,
            jsonString,
            object : LottoRepository.GetDataCallback<PensionLotteryEntity> {
                override fun onSuccess(data: PensionLotteryEntity?) {
                    data?.let {
                        for (i in data.data.indices) {
                            mAllLottoNum.add(data.data[i])
                        }
                        insertRoom(data.data)
                        mRJLottoNum.postValue(mAllLottoNum)
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    mRJLottoNum.postValue(null)
                    throwable.printStackTrace()
                }
            })
    }

    fun selectGetAdminCheck(
        php: String,
        url: String
    ) {
        LottoRepository.getAdminCheckInfo(
            php,
            url,
            object : LottoRepository.GetDataCallback<AdminCheckEntity> {
                override fun onSuccess(data: AdminCheckEntity?) {
                    data?.let {
                        lottoItemAdminCheck.postValue(it)
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    lottoItemAdminCheck.postValue(null)
                    throwable.printStackTrace()
                }
            })
    }

    fun insertAdminLotto(
        context: Context,
        php: String,
        url: String,
        input_type: String
    ) {
        LottoRepository.insertAdminLottoInfo(
            php,
            url,
            input_type,
            object : LottoRepository.GetDataCallback<AdminLottoEntity> {
                override fun onSuccess(data: AdminLottoEntity?) {
                    data?.let {
                        if (data.result == "T") {
                            Toast.makeText(
                                context,
                                "해당 회차 입력 성공",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        selectGetAdminCheck(
                            URLs.JSON_,
                            URLs.LOTTO_MAX_CHECK
                        )
                    }
                }

                override fun onFailure(throwable: Throwable) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.network_check),
                        Toast.LENGTH_SHORT
                    ).show()
                    throwable.printStackTrace()
                }
            })
    }

}