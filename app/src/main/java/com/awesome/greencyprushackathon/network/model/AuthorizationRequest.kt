package com.awesome.greencyprushackathon.network.model

import com.google.gson.annotations.SerializedName

data class AuthorizationRequest(
    @SerializedName("uuid") val uuid: String
)