package com.awesome.greencyprushackathon.network

import com.awesome.greencyprushackathon.features.carbon_footprint.domain.DataStoreRepository
import com.awesome.greencyprushackathon.network.model.AddMoneyRequest
import com.awesome.greencyprushackathon.network.model.AuthorizationRequest
import com.awesome.greencyprushackathon.network.model.BoughtTree
import com.awesome.greencyprushackathon.network.model.BuyTreeRequest
import com.awesome.greencyprushackathon.network.model.StoreTree
import com.awesome.greencyprushackathon.network.model.UserProfile
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class RemoteNetworkRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreRepository: DataStoreRepository
) : NetworkRepository {

    // Check if session exists, if not, perform authorization
    private suspend fun ensureSession() {
        // Check if a session exists
        val existingSession = dataStoreRepository.getSession().firstOrNull()
        if (existingSession == null) {
            // If no session exists, authorize and save the session
            val response = apiService.authorize(AuthorizationRequest(dataStoreRepository.getUserId()))
            dataStoreRepository.saveSession(response.session)
        }
    }

    // Get the session and perform the API request if session is valid
    override suspend fun getBoughtTrees(): List<BoughtTree> {
        ensureSession()
        return apiService.getBoughtTrees()
    }

    override suspend fun buyTree(type: TreeType): String {
        ensureSession()
        return apiService.buyTree(BuyTreeRequest(type.name))
    }

    override suspend fun getUserProfile(): UserProfile {
        ensureSession()
        return apiService.getUserProfile()
    }

    override suspend fun addMoneyToWallet(amount: Double): UserProfile {
        ensureSession()
        return apiService.addMoneyToWallet(AddMoneyRequest(amount))
    }

    override suspend fun getStoreTrees(): List<StoreTree> {
        ensureSession()
        return apiService.getStoreTrees()
    }
}

