package com.example.picasso.dto.sign

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignInDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    ) : Parcelable

