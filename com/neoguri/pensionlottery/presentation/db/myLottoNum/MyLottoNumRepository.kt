package com.neoguri.pensionlottery.presentation.db.myLottoNum

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class MyLottoNumRepository(private val myLottoNumDao: MyLottoNumDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val myLottoNum: LiveData<List<MyLottoNum>> = myLottoNumDao.selectAll()

    // You must call this on a non-UI thread or your app will crash. So we're making this a
    // suspend function so the caller methods know this.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(myLottoNum: MyLottoNum) {
        myLottoNumDao.insert(myLottoNum)
    }

    @WorkerThread
    suspend fun delete(myLottoNum: MyLottoNum) {
        myLottoNumDao.delete(myLottoNum)
    }
}
