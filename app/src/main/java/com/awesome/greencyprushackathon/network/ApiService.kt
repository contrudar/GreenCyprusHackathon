package com.awesome.greencyprushackathon.network

import com.awesome.greencyprushackathon.network.model.AddMoneyRequest
import com.awesome.greencyprushackathon.network.model.AuthorizationRequest
import com.awesome.greencyprushackathon.network.model.AuthorizationResponse
import com.awesome.greencyprushackathon.network.model.BoughtTree
import com.awesome.greencyprushackathon.network.model.BuyTreeRequest
import com.awesome.greencyprushackathon.network.model.StoreTree
import com.awesome.greencyprushackathon.network.model.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("auth")
    suspend fun authorize(@Body request: AuthorizationRequest): AuthorizationResponse

    @GET("trees/bought")
    suspend fun getBoughtTrees(): List<BoughtTree>

    @POST("trees/buy")
    suspend fun buyTree(@Body request: BuyTreeRequest): String

    @GET("profile")
    suspend fun getUserProfile(): UserProfile

    @POST("wallet/add")
    suspend fun addMoneyToWallet(@Body request: AddMoneyRequest): UserProfile

    @GET("trees/store")
    suspend fun getStoreTrees(): List<StoreTree>
}
