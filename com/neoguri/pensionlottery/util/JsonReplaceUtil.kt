package com.neoguri.pensionlottery.util

class JsonReplaceUtil {
    fun replaceString(replace: String): String {
        var replace_ = replace

        replace_ = replace_.replace("\"[", "")
        replace_ = replace_.replace("]\"", "")
        replace_ = replace_.replace("\\\\".toRegex(), "")

        return replace_
    }
}