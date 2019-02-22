package com.udnshopping.udnsauthorizer.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pin(
    @SerializedName("pin") private val _key: String?,
    @SerializedName("user") private val _value: String?,
    @SerializedName("progress") private val _progress: Int?,
    @SerializedName("date") private val _date: String?,
    var isValid: Boolean = true
) : Parcelable {
    val key: String
        get() = _key ?: ""

    val value: String
        get() = _value ?: ""

    val progress: Int
        get() = _progress ?: 0

    val date: String
        get() = _date ?: ""
}