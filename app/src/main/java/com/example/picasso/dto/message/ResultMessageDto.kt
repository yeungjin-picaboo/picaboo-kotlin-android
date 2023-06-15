package com.example.picasso.dto.message

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultMessageDto(
    @SerializedName("ok")
    val ok: Boolean,

    val message: String?
) : Parcelable
