package com.awesome.greencyprushackathon.network

import com.awesome.greencyprushackathon.features.carbon_footprint.domain.DataStoreRepository
import com.awesome.greencyprushackathon.features.your_impact.model.TreeStatus
import com.awesome.greencyprushackathon.network.model.BoughtTree
import com.awesome.greencyprushackathon.network.model.StoreTree
import com.awesome.greencyprushackathon.network.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

// Enum for tree types with photo URL
enum class TreeType(
    val storeUrl: String,
    val plantedUrl: String,
    val description: String
) {
    OAK(
        storeUrl = "https://rassadacvetov.com/wp-content/uploads/2024/06/dub-chereshchatyj-kompakta-compacta--scaled.jpg",
        plantedUrl = "https://s0.geograph.org.uk/geophotos/05/39/26/5392672_7c73798e.jpg",
        description = "Absorbs approximately 10 kg of CO₂ per year"
    ),
    PINE(
        storeUrl = "https://luxgarden.md/wp-content/uploads/2024/08/pin-negru-650-scaled.jpg",
        plantedUrl = "https://www.nationalarboretum.act.gov.au/__data/assets/image/0020/1512533/Young-stone-pine-in-Forest-56-2.jpg",
        description = "Absorbs approximately 8 kg of CO₂ per year"
    ),
    MAPLE(
        storeUrl = "https://bahorgullari.uz/image/cache/catalog/%202023/Vibranced%20photos%20new/01000099-700x700.jpg",
        plantedUrl = "https://kostka.by/wp-content/uploads/2024/06/dub-krasnyj.jpg",
        description = "Absorbs approximately 6 kg of CO₂ per year"
    ),
    FIR(
        storeUrl = "https://www.pervocvet-shop.ru/img/work/nomencl/27178-m.jpg",
        plantedUrl = "https://images.prom.ua/2974647960_w600_h600_2974647960.jpg",
        description = "Absorbs approximately 7 kg of CO₂ per year"
    ),
    WILLOW(
        storeUrl = "https://s.alicdn.com/@sc04/kf/HTB1TI8taHH1gK0jSZFwq6A7aXXaU.jpg",
        plantedUrl = "https://krasnodar.pitomnik-rastenij.ru/image/data/parser3/iva-plakuchaya-pamyati-mindovskogo.jpg",
        description = "Absorbs approximately 5 kg of CO₂ per year"
    );
}

class MockNetworkRepository @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : NetworkRepository {

    private val baseCoordinates = listOf(
        Pair(32.5681111, 34.8470833),
        Pair(33.156175, 35.075562),
        Pair(32.813894, 34.735973)
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeStoreTrees()
            initializeBoughtTrees()
        }
    }

    // Function to generate random coordinates around base point (1km radius)
    private fun generateRandomCoordinates(baseLongitude: Double, baseLatitude: Double): Pair<Double, Double> {
        val randomAngle = Random.nextDouble(0.0, 360.0)  // Random angle
        val randomDistance = Random.nextDouble(0.0, 100.0)  // Random distance up to 1km

        // Convert the angle and distance into lat/long offsets
        val earthRadius = 6371.0  // Earth's radius in kilometers
        val offsetLat = randomDistance * Math.cos(Math.toRadians(randomAngle)) / earthRadius
        val offsetLong =
            randomDistance * Math.sin(Math.toRadians(randomAngle)) / (earthRadius * Math.cos(Math.toRadians(baseLatitude)))

        return Pair(baseLongitude + offsetLong, baseLatitude + offsetLat)
    }

    // Function to generate a random planted date within the last 2 months
    private fun generateRandomPlantedDate(): String {
        val calendar = Calendar.getInstance()

        // Get the date 12 months ago
        calendar.add(Calendar.MONTH, -12)  // Move back by 12 months

        // Random number of days within the last 2 months
        val randomDaysAgo = Random.nextInt(0, 360)

        // Add the random number of days to the calculated date
        calendar.add(Calendar.DAY_OF_YEAR, randomDaysAgo)

        // Format the date to "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // Generate BoughtTree with a random type, planted date, coordinates, and photo URL
    private fun generateBoughtTree(time: Int, basePoint: Pair<Double, Double>): BoughtTree {
        val isPlanted = Random.nextBoolean()
        val type = TreeType.entries.toTypedArray().random()
        val (longitude, latitude) = if (isPlanted) generateRandomCoordinates(
            basePoint.first,
            basePoint.second
        ) else Pair(
            null,
            null
        )
        val plantedDate = if (isPlanted) generateRandomPlantedDate() else null
        val photoUrl = if (isPlanted) {
            type.plantedUrl
        } else {
            type.storeUrl
        }

        return BoughtTree(
            id = "$time",
            title = type.name,
            description = type.description,
            plantedDate = plantedDate,
            longitude = longitude,
            latitude = latitude,
            type = type.name,
            photoUrl = photoUrl,
            status = if (isPlanted) TreeStatus.PLANTED.status else TreeStatus.READY_TO_BE_PLANTED.status
        )
    }

    // Initialize bought trees (saved in DataStore)
    private suspend fun initializeBoughtTrees() {
        val existingBoughtTrees = dataStoreRepository.getBoughtTrees().firstOrNull()
        if (existingBoughtTrees.isNullOrEmpty()) {
            val newBoughtTrees = mutableListOf<BoughtTree>()
            var count = 1
            baseCoordinates.forEach { basePoint ->
                repeat(20) {
                    newBoughtTrees.add(generateBoughtTree(count, basePoint))
                    count++
                }
            }
            dataStoreRepository.saveBoughtTrees(newBoughtTrees)
        }
    }

    // Initialize store trees (saved in DataStore)
    private suspend fun initializeStoreTrees() {
        val existingStoreTrees = dataStoreRepository.getStoreTrees().firstOrNull()
        if (existingStoreTrees.isNullOrEmpty()) {
            val newStoreTrees = TreeType.entries.map { type ->
                val price = BigDecimal(Random.nextDouble(3.0, 25.0)).setScale(2, RoundingMode.HALF_EVEN).toDouble()

                StoreTree(
                    price = price,
                    title = type.name,
                    description = type.description,
                    photoUrl = type.storeUrl,
                    type = type.name
                )
            }
            dataStoreRepository.saveStoreTrees(newStoreTrees)
        }
    }

    // Get bought trees (from DataStore)
    override suspend fun getBoughtTrees(): List<BoughtTree> {
        ensureSession()
        return dataStoreRepository.getBoughtTrees().firstOrNull() ?: emptyList()
    }

    // Buy a tree (from DataStore)
    override suspend fun buyTree(treeType: TreeType): String {
        ensureSession()
        val treePrice = dataStoreRepository.getStoreTrees().firstOrNull()?.find { it.type == treeType.name }?.price
            ?: throw IllegalArgumentException("Invalid tree type")

        val currentBalance = dataStoreRepository.getWalletBalance()
        if (currentBalance < treePrice) {
            throw InsufficientBalanceException("You have insufficient balance to purchase a $treeType tree. Current balance: $currentBalance, Tree price: $treePrice")
        }

        // Deduct the price and update the balance
        val updatedBalance = currentBalance - treePrice
        dataStoreRepository.saveWalletBalance(updatedBalance)

        val currentBoughtTrees = dataStoreRepository.getBoughtTrees().firstOrNull()?.toMutableList() ?: mutableListOf()


        // Add the bought tree to DataStore
        val newBoughtTree = BoughtTree(
            id = "${currentBoughtTrees.size + 1}",
            title = treeType.name,
            description = treeType.description,
            plantedDate = null,
            longitude = null,
            latitude = null,
            type = treeType.name,
            photoUrl = treeType.storeUrl,
            status = TreeStatus.READY_TO_BE_PLANTED.status
        )


        currentBoughtTrees.add(newBoughtTree)

        dataStoreRepository.saveBoughtTrees(currentBoughtTrees)

        return newBoughtTree.id
    }

    override suspend fun addMoneyToWallet(amount: Double): UserProfile {
        ensureSession()
        val currentBalance = dataStoreRepository.getWalletBalance()
        val updatedBalance = currentBalance + amount
        dataStoreRepository.saveWalletBalance(updatedBalance)

        return UserProfile(
            wallet = updatedBalance,
            userId = dataStoreRepository.getUserId(),
            treesOwned = dataStoreRepository.getBoughtTrees().first().size
        )
    }

    // Get store trees (from DataStore)
    override suspend fun getStoreTrees(): List<StoreTree> {
        ensureSession()
        return dataStoreRepository.getStoreTrees().firstOrNull() ?: emptyList()
    }

    override suspend fun getUserProfile(): UserProfile {
        ensureSession()
        val currentBalance = dataStoreRepository.getWalletBalance()
        return UserProfile(
            wallet = currentBalance,
            userId = dataStoreRepository.getUserId(),
            treesOwned = dataStoreRepository.getBoughtTrees().first().size
        )
    }

    private suspend fun ensureSession() {
        val existingSession = dataStoreRepository.getSession().firstOrNull()
        if (existingSession == null) {
            dataStoreRepository.saveSession("local-session-123")
        }
    }
}

class InsufficientBalanceException(message: String) : Exception(message)