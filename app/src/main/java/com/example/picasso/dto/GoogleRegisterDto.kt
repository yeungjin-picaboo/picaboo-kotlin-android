package com.example.picasso.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoogleRegisterDto(
    @SerializedName("email")
    val email:String,

    @SerializedName("password")
    val password:String,

    @SerializedName("nickName")
    val nickName: String,

):Parcelable
