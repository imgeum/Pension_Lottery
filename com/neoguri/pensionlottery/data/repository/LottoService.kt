package com.neoguri.pensionlottery.data.repository

import com.neoguri.pensionlottery.constant.URLs
import com.neoguri.pensionlottery.data.model.admincheck.AdminCheckEntity
import com.neoguri.pensionlottery.data.model.admininsert.AdminLottoEntity
import com.neoguri.pensionlottery.data.model.pensionlottery.PensionLotteryEntity
import com.neoguri.pensionlottery.data.model.version.VersionEntity
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit Interface
 */
interface LottoService {
    // GET으로 파라메터 넘길때
    //@GET("api/{php}")
    //fun getMovieDetails(@Path("php") php: String): Single<MovieDetails>

    // POST로 파라메터 넘길때
    //@FormUrlEncoded
    //@POST("api/{php}")
    //fun postGetNetworkList(@Path("php") php: String, @Field("user_qrnum") snsName: String): Single<MovieDetails>

    /**
     * 연금복권 버젼 정보 조회
     */
    @FormUrlEncoded
    @POST(URLs.API_JSON + "{php}")
    fun getVersionInfo(@Path("php") php: String, @Field("url") url: String): Call<VersionEntity>

    /**
     * 연금복권 당첨 정보 조회
     */
    @FormUrlEncoded
    @POST(URLs.API_JSON + "{php}")
    fun getLottoInfo(@Path("php") php: String, @Field("url") url: String, @Field("json_string") jsonString: String): Call<PensionLotteryEntity>

    /**
     * 연금복권 마지막 정보 조회
     */
    @FormUrlEncoded
    @POST(URLs.API_JSON + "{php}")
    fun getAdminCheckInfo(@Path("php") php: String, @Field("url") url: String): Call<AdminCheckEntity>

    /**
     * 연금복권 최신 정보 입력
     */
    @FormUrlEncoded
    @POST(URLs.API_JSON + "{php}")
    fun insertAdminLottoInfo(@Path("php") php: String, @Field("url") url: String, @Field("json_string") jsonString: String): Call<AdminLottoEntity>

}