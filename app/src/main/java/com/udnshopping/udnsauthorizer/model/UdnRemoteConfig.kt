package com.udnshopping.udnsauthorizer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "unused")
@Parcelize
data class UdnRemoteConfig (
    val isEmailInput: Boolean,
    val isForceUpdate: Boolean,
    val forceUpdateVersion: String
) : Parcelable