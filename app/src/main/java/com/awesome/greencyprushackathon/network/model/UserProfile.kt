package com.awesome.greencyprushackathon.network.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("wallet") val wallet: Double,
    @SerializedName("userId") val userId: String,
    @SerializedName("treesOwned") val treesOwned: Int
)
