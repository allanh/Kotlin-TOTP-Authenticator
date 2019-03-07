package com.udnshopping.udnsauthorizer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BroadcastMessage (
    val date: String,
    val title: String,
    val body: String
) : Parcelable