package com.neoguri.pensionlottery.presentation.db.allLottoNum

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AllLottoNumDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from pension_lottery_table ORDER BY _id ASC")
    fun selectAll(): LiveData<List<AllLottoNum>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(allLottoNum: ArrayList<AllLottoNum>)

    @Query("DELETE FROM pension_lottery_table")
    fun deleteAll()

}