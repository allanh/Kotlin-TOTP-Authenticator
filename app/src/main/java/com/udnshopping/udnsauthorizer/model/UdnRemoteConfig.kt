package com.udnshopping.udnsauthorizer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UdnRemoteConfig (
    val isEmailInput: Boolean? = null,
    val isForceUpdate: Boolean? = null,
    val forceUpdateVersion: String? = null,
    val broadcast: Boolean? = null,
    val broadcastMessage: BroadcastMessage? = null
) : Parcelable