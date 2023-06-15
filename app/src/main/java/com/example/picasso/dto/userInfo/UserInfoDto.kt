package com.example.picasso.dto.userInfo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfoDto(
    @SerializedName("ok")
    val ok: Boolean,

    val message: String?
) : Parcelable
