package com.example.picasso.dto.diary.detailDairyDto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeleteDairyDto(
    @SerializedName("result")
    val result: Boolean
) : Parcelable
