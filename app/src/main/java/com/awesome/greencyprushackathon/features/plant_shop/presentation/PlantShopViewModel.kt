package com.awesome.greencyprushackathon.features.plant_shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.greencyprushackathon.di.LocalNetworkRepository
import com.awesome.greencyprushackathon.network.NetworkRepository
import com.awesome.greencyprushackathon.network.TreeType
import com.awesome.greencyprushackathon.network.model.StoreTree
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantShopViewModel @Inject constructor(
    @LocalNetworkRepository private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _storeTrees = MutableStateFlow<List<StoreTree>>(emptyList())
    val storeTrees: StateFlow<List<StoreTree>> = _storeTrees

    private val _notification = MutableStateFlow<String?>(null)
    val notification: StateFlow<String?> = _notification

    init {
        loadStoreTrees()
    }

    private fun loadStoreTrees() {
        viewModelScope.launch {
            val trees = networkRepository.getStoreTrees()
            _storeTrees.value = trees
        }
    }

    fun buyTree(type: String) {
        viewModelScope.launch {
            try {
                val treeId = networkRepository.buyTree(TreeType.entries.find { it == TreeType.valueOf(type) }!!)
                _notification.value = "Tree purchased successfully! Tree ID: $treeId"
            } catch (e: Throwable) {
                _notification.value = "Error: ${e.message}"
            }
        }
    }

    fun clearNotification() {
        _notification.value = null
    }
}
