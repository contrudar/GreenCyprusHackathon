package com.awesome.greencyprushackathon.network.model

import com.google.gson.annotations.SerializedName

data class AuthorizationResponse(
    @SerializedName("session") val session: String
)
