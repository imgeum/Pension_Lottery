package com.neoguri.pensionlottery.util

/**
 * Created by user on 2018-07-03.
 */

import android.os.Environment

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 어플의 로그를 파일에 남기기 위한 LogToFile class를 정의 한 파일
 *
 * @author
 */
class LogToFile
/**
 * LogToFile 생성자로 기본 설정된 log file path 설정
 *
 * @throws IOException
 */
@Throws(IOException::class)
constructor() {

    /**
     * 로그 파일 저장 폴더명
     */
    internal val FOLDER_NAME = "Log"
    /**
     * 로그 파일명
     */
    internal val FILE_NAME = "LogTrace.txt"

    /**
     * 로그를 남길 filePath(외장에) 멤버 문자열 변수
     */
    /**
     * 설정된 log file path 반환
     *
     * @return
     */
    var path: String? = null
        private set

    /**
     * 파일 write를 쉽게 도와주는 멤버 Writer 변수
     */
    private var mWriter: Writer? = null

    init {
        openLogFile()
    }

    /**
     * Log File을 open하는 함수
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    protected fun openLogFile() {
        val sdcard = Environment.getExternalStorageDirectory()
        val sdcardPath = sdcard.absolutePath
        val extStorageState = Environment.getExternalStorageState()

        if (Environment.MEDIA_MOUNTED == extStorageState) {
            val dir = String.format("%s/%s/", Environment.getExternalStorageDirectory().absolutePath, FOLDER_NAME)

            val fDir = File(dir)
            if (!fDir.exists()) {
                fDir.mkdirs()
            }

            val f = File(String.format("%s/%s/%s", sdcardPath, FOLDER_NAME, FILE_NAME))

            path = f.absolutePath
            mWriter = BufferedWriter(FileWriter(path!!, true), 2048)
        }
    }

    /**
     * open된 Log File에 전달된 메시지를 write
     *
     * @param message
     * @throws IOException
     */
    @Throws(IOException::class)
    fun println(message: String) {
        mWriter!!.write(TIMESTAMP_FMT.format(Date()))
        mWriter!!.write(message)
        mWriter!!.write("\n")
        mWriter!!.flush()
    }

    /**
     * Writer 종료 (File open close)
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun close() {
        mWriter!!.close()
    }

    companion object {

        private val TIMESTAMP_FMT = SimpleDateFormat("[HH:mm:ss] ", Locale.KOREA)
    }

}