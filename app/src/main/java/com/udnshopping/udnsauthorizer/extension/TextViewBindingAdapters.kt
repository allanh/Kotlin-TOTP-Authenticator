package com.udnshopping.udnsauthorizer.extension

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("customTextColor")
fun bindCustomTextColor(textView: TextView, color: Int?) {
    color ?: return
    textView.setTextColor(color)
}