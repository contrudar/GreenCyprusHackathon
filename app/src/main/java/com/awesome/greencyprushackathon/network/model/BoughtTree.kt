package com.awesome.greencyprushackathon.network.model

import com.awesome.greencyprushackathon.features.your_impact.model.Tree
import com.awesome.greencyprushackathon.features.your_impact.model.TreeStatus
import com.google.gson.annotations.SerializedName

data class BoughtTree(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("plantedDate") val plantedDate: String?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("type") val type: String,
    @SerializedName("photoUrl") val photoUrl: String,
    @SerializedName("status") val status: String // "Planted" or "Ready to be planted"
) {
    fun mapToUiModel() =
        Tree(
            id = id,
            title = title,
            description = description,
            plantedDate = plantedDate,
            longitude = longitude,
            latitude = latitude,
            type = type,
            photoUrl = photoUrl,
            status = if (TreeStatus.PLANTED.status == status) TreeStatus.PLANTED else TreeStatus.READY_TO_BE_PLANTED,
        )
}
