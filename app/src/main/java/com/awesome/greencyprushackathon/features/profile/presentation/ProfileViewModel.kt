package com.awesome.greencyprushackathon.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.greencyprushackathon.di.LocalNetworkRepository
import com.awesome.greencyprushackathon.features.carbon_footprint.domain.DataStoreRepository
import com.awesome.greencyprushackathon.network.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @LocalNetworkRepository private val networkRepository: NetworkRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadProfileData() {
        viewModelScope.launch {
            val userProfile = networkRepository.getUserProfile()
            val treesNeeded = dataStoreRepository.getTreesNeeded().firstOrNull() ?: 0

            _uiState.update {
                it.copy(
                    treesBought = userProfile.treesOwned,
                    treesNeeded = treesNeeded,
                    walletBalance = userProfile.wallet
                )
            }
        }
    }

    fun depositMoney(amount: Double) {
        if (amount <= 0) return
        viewModelScope.launch {
            val updatedProfile = networkRepository.addMoneyToWallet(amount)
            _uiState.update {
                it.copy(walletBalance = updatedProfile.wallet)
            }
        }
    }
}

data class ProfileUiState(
    val treesBought: Int = 0,
    val treesNeeded: Int = 0,
    val walletBalance: Double = 0.0
)



