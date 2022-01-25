package com.neoguri.pensionlottery.presentation.db.allLottoNum

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class AllLottoNumRepository(private val allLottoNumDao: AllLottoNumDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allLottoNum: LiveData<List<AllLottoNum>> = allLottoNumDao.selectAll()

    // You must call this on a non-UI thread or your app will crash. So we're making this a
    // suspend function so the caller methods know this.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(allLottoNum: ArrayList<AllLottoNum>) {
        allLottoNumDao.insertAll(allLottoNum)
    }

    @WorkerThread
    suspend fun deleteAll() {
        allLottoNumDao.deleteAll()
    }
}
