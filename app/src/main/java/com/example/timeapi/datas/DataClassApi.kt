package com.example.timeapi.datas


import com.google.gson.annotations.SerializedName

data class DataClassApi(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
)