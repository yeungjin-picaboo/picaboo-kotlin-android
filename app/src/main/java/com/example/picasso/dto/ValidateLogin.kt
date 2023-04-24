package com.example.picasso.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ValidateLogin(
    @SerializedName("userId") val userId: String,

    @SerializedName("nickname") val nickname: String,

    @SerializedName("iat") val iat: String,

    @SerializedName("exp") val exp: String

) : Parcelable

