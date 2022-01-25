package com.neoguri.pensionlottery.presentation.db.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.neoguri.pensionlottery.presentation.db.allLottoNum.AllLottoNum
import com.neoguri.pensionlottery.presentation.db.allLottoNum.AllLottoNumDao
import com.neoguri.pensionlottery.presentation.db.myLottoNum.MyLottoNum
import com.neoguri.pensionlottery.presentation.db.myLottoNum.MyLottoNumDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [AllLottoNum::class, MyLottoNum::class], version = 1, exportSchema = false)
abstract class LottoNumRoomDatabase : RoomDatabase() {

    abstract fun allLottoNumDao(): AllLottoNumDao
    abstract fun myLottoNumDao(): MyLottoNumDao

    companion object {
        @Volatile
        private var INSTANCE: LottoNumRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): LottoNumRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LottoNumRoomDatabase::class.java,
                    "lotto_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.allLottoNumDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */
        fun populateDatabase(allLottoNumDao: AllLottoNumDao) {

        }
    }

}