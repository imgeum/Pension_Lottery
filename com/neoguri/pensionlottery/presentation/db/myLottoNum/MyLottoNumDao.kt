package com.neoguri.pensionlottery.presentation.db.myLottoNum

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MyLottoNumDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from my_pension_lottery_table ORDER BY _id ASC")
    fun selectAll(): LiveData<List<MyLottoNum>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(blockLottoNum: MyLottoNum)

    @Delete
    fun delete(blockLottoNum: MyLottoNum)


}