package com.udnshopping.udnsauthorizer.models

import com.google.gson.annotations.SerializedName

data class Secret(
    @SerializedName("secret") private val _key: String?,
    @SerializedName("user") private val _value: String?,
    @SerializedName("date") private val _date: String?
) {
    val key: String
        get() = _key ?: ""

    val value: String
        get() = _value ?: ""

    val date: String
        get() = _date ?: ""
}