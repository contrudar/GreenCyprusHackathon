package com.awesome.greencyprushackathon.network

import com.awesome.greencyprushackathon.network.model.BoughtTree
import com.awesome.greencyprushackathon.network.model.StoreTree
import com.awesome.greencyprushackathon.network.model.UserProfile

interface NetworkRepository {

    suspend fun getBoughtTrees(): List<BoughtTree>

    suspend fun buyTree(treeType: TreeType): String

    suspend fun getUserProfile(): UserProfile

    suspend fun addMoneyToWallet(amount: Double): UserProfile

    suspend fun getStoreTrees(): List<StoreTree>
}
