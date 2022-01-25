package com.neoguri.pensionlottery.spineradapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.neoguri.pensionlottery.R

class AutoSpinnerAdapter (
    internal var mContext: Context,
    textViewResourceId: Int, objects: Array<String>
) :
    ArrayAdapter<String>(mContext, textViewResourceId, objects) {
    private var items = arrayOf<String>()

    init {
        this.items = objects
    }

    /**
     * 스피너 클릭시 보여지는 View의 정의
     */
    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        var dropDownConvertView = convertView

        if (dropDownConvertView == null) {
            val inflater = LayoutInflater.from(mContext)
            dropDownConvertView = inflater.inflate(
                R.layout.spinner_auto_item, parent, false
            )
        }

        val tv = dropDownConvertView!!
            .findViewById<TextView>(android.R.id.text1)
        tv.text = items[position]
        return dropDownConvertView
    }

    /**
     * 기본 스피너 View 정의
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var basicConvertView = convertView
        if (basicConvertView == null) {
            val inflater = LayoutInflater.from(mContext)
            basicConvertView = inflater.inflate(
                R.layout.spinner_auto_root_item, parent, false
            )
        }

        val tv = basicConvertView!!
            .findViewById<TextView>(android.R.id.text1)
        tv.text = items[position]
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.text_color))
        return basicConvertView
    }
}