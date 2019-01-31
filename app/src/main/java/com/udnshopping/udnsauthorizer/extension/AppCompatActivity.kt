package com.udnshopping.udnsauthorizer.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

/**
 * Get current
 */
fun AppCompatActivity.getCurrentFragmentId(navHostId: Int) : Int {
    val host: NavHostFragment? = supportFragmentManager
        .findFragmentById(navHostId) as NavHostFragment?
    return host?.navController?.currentDestination?.id ?: 0
}

fun AppCompatActivity.isCurrentFragment(navHostId: Int, fragmentId: Int) : Boolean =
        getCurrentFragmentId(navHostId) == fragmentId