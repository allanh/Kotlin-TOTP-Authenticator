package com.udnshopping.udnsauthorizer.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Secret(
    @SerializedName("secret") private val _key: String? = null,
    @SerializedName("user") private val _value: String? = null,
    @SerializedName("date") private val _date: String? = null
) : Parcelable {
    val key: String
        get() = _key ?: ""

    val value: String
        get() = _value ?: ""

    val date: String
        get() = _date ?: ""
}