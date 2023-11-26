package com.dmdbilal.agroweathertip.domain

import com.google.gson.annotations.SerializedName

data class CropData(
    @SerializedName("crop")
    val crop: String,
    @SerializedName("probability")
    val probability: Double
)
