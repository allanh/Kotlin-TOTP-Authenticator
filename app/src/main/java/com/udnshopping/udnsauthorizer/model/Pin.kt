package com.udnshopping.udnsauthorizer.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pin(
        @SerializedName("pin") private val _key: String? = null,
        @SerializedName("user") private val _user: String? = null,
        @SerializedName("progress") private val _progress: Int? = null,
        @SerializedName("date") private val _date: String? = null,
        @Volatile var isValid: Boolean = true
) : Parcelable {

    // 認證碼
    val key: String
        get() = if ((_key?.length ?: 0) > 5) {
            "${_key?.substring(0, 3)} ${_key?.substring(3, 6)}"
        } else {
            _key ?: ""
        }

    // 使用者代號
    val user: String
        get() = if ((_user?.length ?: 0) > 5) {
            _user?.removePrefix("/UDN:") ?: ""
        } else {
            _user ?: ""
        }

    val progress: Int
        get() = _progress ?: 0

    // 日期
    val date: String
        get() = _date ?: ""
}