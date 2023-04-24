package com.example.picasso.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Objects

@Parcelize
data class DiariesListDto(
    val id: Int,
    val date: String
) : Parcelable