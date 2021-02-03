package com.udnshopping.udnsauthorizer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BroadcastMessage (
    val date: String? = null,
    val title: String? = null,
    val body: String? = null
) : Parcelable