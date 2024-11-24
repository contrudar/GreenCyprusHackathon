package com.awesome.greencyprushackathon.network.model

import com.google.gson.annotations.SerializedName

data class StoreTree(
    @SerializedName("price") val price: Double,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("photoUrl") val photoUrl: String,
    @SerializedName("type") val type: String
)
