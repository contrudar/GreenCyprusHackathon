package com.awesome.greencyprushackathon.features.your_impact.model

enum class TreeStatus(val status: String) {
    PLANTED("Planted"),
    READY_TO_BE_PLANTED("Ready to be planted (Already bought)")
}

data class Tree(
    val id: String,
    val title: String,
    val description: String,
    val plantedDate: String?,
    val longitude: Double?,
    val latitude: Double?,
    val type: String,
    val photoUrl: String,
    val status: TreeStatus
)