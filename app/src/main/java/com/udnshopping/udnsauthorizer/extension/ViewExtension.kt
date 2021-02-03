package com.udnshopping.udnsauthorizer.extension

import android.view.View
import androidx.databinding.BindingAdapter


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.isVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("android:visibility")
fun bindVisibility(view: View, isVisible: Boolean) {
    view.isVisible(isVisible)
}