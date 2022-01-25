package com.neoguri.pensionlottery.data.repository

import com.neoguri.pensionlottery.constant.URLs
import com.neoguri.pensionlottery.data.model.admincheck.AdminCheckEntity
import com.neoguri.pensionlottery.data.model.admininsert.AdminLottoEntity
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryEntity
import com.neoguri.pensionlottery.data.model.version.VersionEntity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 로또 조회 API Remote DataSource
 */

object LottoRemoteDataSource {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(URLs.PREFIX)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val lottoService = retrofit.create(LottoService::class.java)

    /**
     * 연금복권 버젼 정보 조회
     */
    fun getVersionInfo(php: String, url: String, callback: LottoRepository.GetDataCallback<VersionEntity>) {
        lottoService.getVersionInfo(php, url).enqueue(object : Callback<VersionEntity> {
            override fun onResponse(call: Call<VersionEntity>, response: Response<VersionEntity>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<VersionEntity>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }
    
    /**
     * 연금복권 당첨 정보 조회
     */
    fun getLottoInfo(php: String, url: String, jsonString: String, callback: LottoRepository.GetDataCallback<PensionLotteryEntity>) {
        lottoService.getLottoInfo(php, url, jsonString).enqueue(object : Callback<PensionLotteryEntity> {
            override fun onResponse(call: Call<PensionLotteryEntity>, response: Response<PensionLotteryEntity>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<PensionLotteryEntity>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    /**
     * 연금복권 마지막 정보 조회
     */
    fun getAdminCheckInfo(php: String, url: String, callback: LottoRepository.GetDataCallback<AdminCheckEntity>) {
        lottoService.getAdminCheckInfo(php, url).enqueue(object : Callback<AdminCheckEntity> {
            override fun onResponse(call: Call<AdminCheckEntity>, response: Response<AdminCheckEntity>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<AdminCheckEntity>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    /**
     * 연금복권 최신 정보 입력
     */
    fun insertAdminLottoInfo(php: String, url: String, input_type: String, callback: LottoRepository.GetDataCallback<AdminLottoEntity>) {
        lottoService.insertAdminLottoInfo(php, url, input_type).enqueue(object : Callback<AdminLottoEntity> {
            override fun onResponse(call: Call<AdminLottoEntity>, response: Response<AdminLottoEntity>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<AdminLottoEntity>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

}