package com.example.picasso.dto.sign

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReturnJwtDto(
    @SerializedName("accessToken")
    val accessToken: String

) : Parcelable
