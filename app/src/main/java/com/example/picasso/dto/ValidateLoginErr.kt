package com.example.picasso.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ValidateLoginErr(
    @SerializedName("statusCode")
    val statusCode: Int,

    @SerializedName("message")
    val message: String,

    ) : Parcelable


