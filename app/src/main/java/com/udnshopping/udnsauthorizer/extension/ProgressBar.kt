package com.udnshopping.udnsauthorizer.extension

import android.content.res.ColorStateList
import android.widget.ProgressBar

fun ProgressBar.setProgressTintColor(color: Int) {
    progressTintList = ColorStateList.valueOf(color)
}