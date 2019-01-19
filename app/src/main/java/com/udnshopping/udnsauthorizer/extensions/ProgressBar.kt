package com.udnshopping.udnsauthorizer.extensions

import android.content.res.ColorStateList
import android.widget.ProgressBar

fun ProgressBar.setProgressTintColor(color: Int) {
    progressTintList = ColorStateList.valueOf(color)
}