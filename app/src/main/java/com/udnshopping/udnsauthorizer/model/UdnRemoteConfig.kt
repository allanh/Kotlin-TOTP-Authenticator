package com.udnshopping.udnsauthorizer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UdnRemoteConfig (
    val isEmailInput: Boolean,
    val isForceUpdate: Boolean,
    val forceUpdateVersion: String,
    val broadcast: Boolean,
    val broadcastMessage: BroadcastMessage
) : Parcelable