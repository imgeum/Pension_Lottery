package com.neoguri.pensionlottery.util

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList

/**
 * Created by user on 2018-07-03.
 */
class PrefUtil {
    private var mFileName: String? = "PENSIONLOTTERYSHOP"
    private var mMode = Context.MODE_PRIVATE

    private var mPref: SharedPreferences? = null
    private var mPrefUtil: PrefUtil? = null

    fun getInstance(context: Context): PrefUtil {
        if (mPrefUtil == null) {
            mPrefUtil = PrefUtil()
        }
        mPrefUtil!!.setInit(context, mFileName, mMode)
        return mPrefUtil!!
    }

    fun getInstance(context: Context, fileName: String, mode: Int): PrefUtil {
        if (mPrefUtil == null) {
            mPrefUtil = PrefUtil()
        }
        mPrefUtil!!.setInit(context, fileName, mode)
        return mPrefUtil!!
    }

    fun destroy() {
        mFileName = null
        mPref = null
        mPrefUtil = null
    }

    fun setInit(context: Context, fileName: String?, mode: Int) {
        mFileName = fileName
        mMode = mode
        mPref = context.getSharedPreferences(mFileName, mMode)
    }

    fun setPref(key: String, value: Any) {
        val editor = mPref!!.edit()

        if (value is Int) {
            editor.putInt(key, value)
        } else if (value is Boolean) {
            editor.putBoolean(key, value)
        } else {
            editor.putString(key, value as String)
        }

        editor.commit()
    }


    fun getPrefString(key: String, defaultValue: String): String {
        var value: String? = getPrefString(key)
        if (value == null || "" == value)
            value = defaultValue

        return value
    }

    fun setStringArrayPref(key: String, values: ArrayList<String>) {
        val editor = mPref!!.edit()
        val a = JSONArray()
        for (i in values.indices) {
            a.put(values[i])
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString())
        } else {
            editor.putString(key, null)
        }
        editor.commit()
    }

    fun getStringArrayPref(key: String): ArrayList<String> {
        val json = mPref!!.getString(key, null)
        val urls = ArrayList<String>()
        if (json != null) {
            try {
                val a = JSONArray(json)
                for (i in 0 until a.length()) {
                    val url = a.optString(i)
                    urls.add(url)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        return urls
    }

    fun getPrefString(key: String): String? {
        return mPref!!.getString(key, "")
    }


    fun getPrefInt(key: String): Int {
        return mPref!!.getInt(key, 0)
    }


    fun getPrefBoolean(key: String, defaultValue: Boolean): Boolean {
        return mPref!!.getBoolean(key, defaultValue)
    }

    fun getPrefBoolean(key: String): Boolean {
        return mPref!!.getBoolean(key, false)
    }

}