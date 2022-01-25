package com.neoguri.pensionlottery

import androidx.multidex.MultiDexApplication

class PensionLottery : MultiDexApplication(){

    companion object {
        /** 앱 모드 설정  */
        const val APP_MODE_DEBUG = 0
        const val APP_MODE_RELEASE = 1
        const val APP_MODE = APP_MODE_DEBUG
    }

}