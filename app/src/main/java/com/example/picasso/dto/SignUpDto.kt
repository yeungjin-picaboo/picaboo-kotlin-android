package com.example.picasso.dto

import android.os.Parcelable
import android.provider.ContactsContract.CommonDataKinds.Nickname
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("nickname")
    val nickname: String
) : Parcelable

