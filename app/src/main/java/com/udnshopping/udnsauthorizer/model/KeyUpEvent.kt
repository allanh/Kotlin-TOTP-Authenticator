package com.udnshopping.udnsauthorizer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KeyUpEvent(val keyCode: Int) : Parcelable
