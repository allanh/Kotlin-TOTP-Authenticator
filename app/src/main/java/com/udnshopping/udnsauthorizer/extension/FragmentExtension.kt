package com.udnshopping.udnsauthorizer.extension

import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

inline fun Fragment.setupToolbar(@IdRes id: Int, title:String?= null, upNavigation: Boolean = false) : ActionBar {

    val activityCompat = activity as AppCompatActivity
    val toolbar = activity?.findViewById<Toolbar>(id)
    activityCompat.setSupportActionBar(toolbar)
    if(title != null){
        activityCompat.supportActionBar?.title = title
    }
    activityCompat.supportActionBar?.setHomeButtonEnabled(true)
    return activityCompat.supportActionBar!!
}