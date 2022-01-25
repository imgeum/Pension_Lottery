package com.neoguri.pensionlottery.data.repository

import com.neoguri.pensionlottery.data.model.admincheck.AdminCheckEntity
import com.neoguri.pensionlottery.data.model.admininsert.AdminLottoEntity
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryEntity
import com.neoguri.pensionlottery.data.model.version.VersionEntity

/**
 * 로또 데이터 리파지토리
 *
 * DataSource로 부터 Model을 가져오는 것을 추상화하는 역할
 */
object LottoRepository {

    private val remoteDataSource = LottoRemoteDataSource

    /**
     * 연금복권 버젼 조회
     */
    fun getVersionInfo(php: String, url: String, callback: GetDataCallback<VersionEntity>) {
        remoteDataSource.getVersionInfo(php, url, callback)
    }

    /**
     * 연금복권 당첨 정보 조회
     */
    fun getLottoInfo(php: String, url: String, jsonString: String, callback: GetDataCallback<PensionLotteryEntity>) {
        remoteDataSource.getLottoInfo(php, url, jsonString, callback)
    }

    /**
     * 연금복권 마지막 정보 조회
     */
    fun getAdminCheckInfo(php: String, url: String, callback: GetDataCallback<AdminCheckEntity>) {
        remoteDataSource.getAdminCheckInfo(php, url, callback)
    }

    /**
     * 연금복권 최신 정보 입력
     */
    fun insertAdminLottoInfo(php: String, url: String, input_type: String, callback: GetDataCallback<AdminLottoEntity>) {
        remoteDataSource.insertAdminLottoInfo(php, url, input_type, callback)
    }

    /**
     * 데이터 조회 콜백
     */
    interface GetDataCallback<T> {
        fun onSuccess(data: T?)
        fun onFailure(throwable: Throwable)

    }
}