package com.example.diaxytos.utils

import android.content.Context
import android.widget.Toast

fun Context.toast(text: Any?) =
    Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()

fun Context.longToast(text: Any?) =
    Toast.makeText(this, text.toString(), Toast.LENGTH_LONG).show()