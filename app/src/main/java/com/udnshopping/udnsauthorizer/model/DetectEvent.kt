package com.udnshopping.udnsauthorizer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetectEvent(val auth: String) : Parcelable