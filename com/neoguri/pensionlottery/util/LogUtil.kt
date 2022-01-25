package com.neoguri.pensionlottery.util

/**
 * Created by user on 2018-07-03.
 */
import android.util.Log
import com.neoguri.pensionlottery.PensionLottery
import java.io.IOException

/**
 * 로그 출력을 쉽게 하기 위한 class로 세가지 debug 모드에 따른 로그 기능을 제공한다. (none, trace, file)
 *
 * @author
 */
object LogUtil {
    /**
     * 로그 TAG 명
     */
    internal val TAG = "_pension_lottery"

    /**
     * 디버그 없음
     */
    val NONE_DEBUG = 0

    /**
     * LogCat으로만 출력
     */
    val TRACE_DEBUG = 1

    /**
     * 파일에 로그 출력
     */
    val FILE_DEBUG = 2

    /**
     * 정적 로그 모드 멤버 변수
     */
    /**
     * 현재 설정된 debug 모드 반환
     *
     * @return
     */
    /**
     * debug 모드 설정
     *
     * @param debugMode
     */
    var debugMode = if (PensionLottery.APP_MODE == PensionLottery.APP_MODE_DEBUG) TRACE_DEBUG else NONE_DEBUG
        set(debugMode) = if (debugMode < NONE_DEBUG || debugMode > FILE_DEBUG)
            field = NONE_DEBUG
        else
            field = debugMode

    /**
     * 전달 받은 로그 level, msg를 파일에 출력
     *
     * @param level
     * @param msg
     */
    private fun writeToFile(level: String, msg: String) {
        var logToFile: LogToFile? = null

        try {
            logToFile = LogToFile()
            logToFile.println("[$level]$msg")

        } catch (e: IOException) {
            Log.e(TAG, e.localizedMessage!!)
        } finally {
            if (logToFile != null)
                try {
                    logToFile.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.localizedMessage!!)
                }

        }
    }

    /**
     * verbose message 로그 출력
     *
     * @param message
     */
    fun v(message: String) {
        if (debugMode == TRACE_DEBUG || debugMode == FILE_DEBUG) {
            var tag = ""
            val temp = Throwable().stackTrace[1].className
            if (temp != null) {
                val lastDotPos = temp.lastIndexOf(".")
                tag = temp.substring(lastDotPos + 1)
            }
            val methodName = Throwable().stackTrace[1]
                    .methodName
            val lineNumber = Throwable().stackTrace[1].lineNumber

            val logText = ("[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message)

            if (debugMode == FILE_DEBUG) {
                writeToFile("verbose", logText)
            } else {
                Log.v(TAG, logText)
            }
        }
    }

    /**
     * info message 로그 출력
     *
     * @param message
     */
    fun i(message: String) {
        if (debugMode == TRACE_DEBUG || debugMode == FILE_DEBUG) {
            var tag = ""
            val temp = Throwable().stackTrace[1].className
            if (temp != null) {
                val lastDotPos = temp.lastIndexOf(".")
                tag = temp.substring(lastDotPos + 1)
            }
            val methodName = Throwable().stackTrace[1]
                    .methodName
            val lineNumber = Throwable().stackTrace[1].lineNumber

            val logText = ("[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message)

            if (debugMode == FILE_DEBUG) {
                writeToFile("info", logText)
            } else {
                Log.i(TAG, logText)
            }
        }
    }

    /**
     * debug message 로그 출력
     *
     * @param message
     */
    fun d(message: String) {
        if (debugMode == TRACE_DEBUG || debugMode == FILE_DEBUG) {
            var tag = ""
            val temp = Throwable().stackTrace[1].className
            if (temp != null) {
                val lastDotPos = temp.lastIndexOf(".")
                tag = temp.substring(lastDotPos + 1)
            }
            val methodName = Throwable().stackTrace[1]
                    .methodName
            val lineNumber = Throwable().stackTrace[1].lineNumber

            val logText = ("[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message)
            if (debugMode == FILE_DEBUG) {
                writeToFile("debug", logText)
            } else {
                Log.d(TAG, logText)
            }
        }
    }

    /**
     * warning message 로그 출력
     *
     * @param message
     */
    fun w(message: String) {
        if (debugMode == TRACE_DEBUG || debugMode == FILE_DEBUG) {
            var tag = ""
            val temp = Throwable().stackTrace[1].className
            if (temp != null) {
                val lastDotPos = temp.lastIndexOf(".")
                tag = temp.substring(lastDotPos + 1)
            }
            val methodName = Throwable().stackTrace[1]
                    .methodName
            val lineNumber = Throwable().stackTrace[1].lineNumber

            val logText = ("[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message)

            if (debugMode == FILE_DEBUG) {
                writeToFile("warn", logText)
            } else {
                Log.w(TAG, logText)
            }
        }
    }

    /**
     * error message 로그 출력
     *
     * @param message
     */
    fun e(message: String) {
        if (debugMode == TRACE_DEBUG || debugMode == FILE_DEBUG) {
            var tag = ""
            val temp = Throwable().stackTrace[1].className
            if (temp != null) {
                val lastDotPos = temp.lastIndexOf(".")
                tag = temp.substring(lastDotPos + 1)
            }
            val methodName = Throwable().stackTrace[1]
                    .methodName
            val lineNumber = Throwable().stackTrace[1].lineNumber

            val logText = ("[" + tag + "] " + methodName + "()" + "["
                    + lineNumber + "]" + " >> " + message)

            if (debugMode == FILE_DEBUG) {
                writeToFile("error", logText)
            } else {
                Log.e(TAG, logText)
            }
        }
    }

}