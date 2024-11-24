package com.awesome.greencyprushackathon.network.model

import com.google.gson.annotations.SerializedName

data class AddMoneyRequest(
    @SerializedName("amount") val amount: Double
)
