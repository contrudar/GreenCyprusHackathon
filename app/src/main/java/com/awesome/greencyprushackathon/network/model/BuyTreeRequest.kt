package com.awesome.greencyprushackathon.network.model

import com.google.gson.annotations.SerializedName

data class BuyTreeRequest(
    @SerializedName("type") val type: String
)
