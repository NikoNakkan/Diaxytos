package com.example.diaxytos.utils

import android.content.Context
import android.view.View
import android.widget.AdapterView
import com.example.diaxytos.R


class SpinnerListener(val context: Context, val arrayId: Int, val callback: (String) -> Unit)
    : AdapterView.OnItemSelectedListener{

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        callback(context.resources.getStringArray(arrayId)[position])
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // do nothing
    }
}
