package com.awesome.greencyprushackathon.features.your_impact.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.greencyprushackathon.di.LocalNetworkRepository
import com.awesome.greencyprushackathon.features.your_impact.model.Tree
import com.awesome.greencyprushackathon.network.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YourImpactViewModel @Inject constructor(
    @LocalNetworkRepository val networkRepository: NetworkRepository
) : ViewModel() {

    private val _trees = MutableStateFlow<List<Tree>>(emptyList())
    val trees: StateFlow<List<Tree>> = _trees

    private val _isMapMode = MutableStateFlow(true)
    val isMapMode: StateFlow<Boolean> = _isMapMode

     fun loadTrees() {
        viewModelScope.launch {
            val treeList = networkRepository.getBoughtTrees()
            _trees.value = treeList.map { it.mapToUiModel() }
        }
    }

    fun toggleMode() {
        _isMapMode.value = !_isMapMode.value
    }

    fun setMapMode(isMap: Boolean) {
        _isMapMode.value = isMap
    }
}