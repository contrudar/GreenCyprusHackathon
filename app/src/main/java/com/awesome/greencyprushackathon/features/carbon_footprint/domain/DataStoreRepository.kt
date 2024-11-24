package com.awesome.greencyprushackathon.features.carbon_footprint.domain

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.awesome.greencyprushackathon.network.model.BoughtTree
import com.awesome.greencyprushackathon.network.model.StoreTree
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID

val Context.dataStore by preferencesDataStore("user_preferences")

class DataStoreRepository(private val context: Context) {

    companion object {
        private val CARBON_FOOTPRINT_KEY: Preferences.Key<Int> = intPreferencesKey("carbon_footprint")
        private val TREES_NEEDED_KEY: Preferences.Key<Int> = intPreferencesKey("trees_needed")
        private val WEEKLY_KILOMETERS_DRIVEN_KEY: Preferences.Key<Int> = intPreferencesKey("weekly_kilometers_driven")
        private val MONTHLY_ELECTRICITY_USAGE_KEY: Preferences.Key<Int> = intPreferencesKey("monthly_electricity_usage")
        private val WEEKLY_MEAT_MEALS_KEY: Preferences.Key<Int> = intPreferencesKey("weekly_meat_meals")
        private val SHORT_HAUL_FLIGHTS_PER_YEAR_KEY: Preferences.Key<Int> =
            intPreferencesKey("short_haul_flights_per_year")
        private val LONG_HAUL_FLIGHTS_PER_YEAR_KEY: Preferences.Key<Int> =
            intPreferencesKey("long_haul_flights_per_year")
        private val NEW_CLOTHING_ITEMS_PER_MONTH_KEY: Preferences.Key<Int> =
            intPreferencesKey("new_clothing_items_per_month")
        private val RECYCLES_WASTE_KEY: Preferences.Key<Boolean> = booleanPreferencesKey("recycles_waste")

        private val SESSION: Preferences.Key<String> = stringPreferencesKey("session")

        private val USER_ID_KEY: Preferences.Key<String> = stringPreferencesKey("user_id")
        private val WALLET_BALANCE_KEY: Preferences.Key<Double> = doublePreferencesKey("wallet_balance")

        private val BOUGHT_TREES_KEY = stringPreferencesKey("bought_trees")
        private val STORE_TREES_KEY = stringPreferencesKey("store_trees")
    }

    fun getCarbonFootprint(): Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[CARBON_FOOTPRINT_KEY] }

    fun getTreesNeeded(): Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[TREES_NEEDED_KEY] }

    fun getWeeklyKilometersDriven(): Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[WEEKLY_KILOMETERS_DRIVEN_KEY] }

    fun getMonthlyElectricityUsage(): Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[MONTHLY_ELECTRICITY_USAGE_KEY] }

    fun getWeeklyMeatMeals(): Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[WEEKLY_MEAT_MEALS_KEY] }

    fun getShortHaulFlightsPerYear(): Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[SHORT_HAUL_FLIGHTS_PER_YEAR_KEY] }

    fun getLongHaulFlightsPerYear(): Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[LONG_HAUL_FLIGHTS_PER_YEAR_KEY] }

    fun getNewClothingItemsPerMonth(): Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[NEW_CLOTHING_ITEMS_PER_MONTH_KEY] }

    fun getRecyclesWaste(): Flow<Boolean?> = context.dataStore.data
        .map { preferences -> preferences[RECYCLES_WASTE_KEY] }

    suspend fun saveCarbonFootprint(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[CARBON_FOOTPRINT_KEY] = value
        }
    }

    suspend fun saveTreesNeeded(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[TREES_NEEDED_KEY] = value
        }
    }

    suspend fun saveWeeklyKilometersDriven(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[WEEKLY_KILOMETERS_DRIVEN_KEY] = value
        }
    }

    suspend fun saveMonthlyElectricityUsage(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[MONTHLY_ELECTRICITY_USAGE_KEY] = value
        }
    }

    suspend fun saveWeeklyMeatMeals(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[WEEKLY_MEAT_MEALS_KEY] = value
        }
    }

    suspend fun saveShortHaulFlightsPerYear(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[SHORT_HAUL_FLIGHTS_PER_YEAR_KEY] = value
        }
    }

    suspend fun saveLongHaulFlightsPerYear(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[LONG_HAUL_FLIGHTS_PER_YEAR_KEY] = value
        }
    }

    suspend fun saveNewClothingItemsPerMonth(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[NEW_CLOTHING_ITEMS_PER_MONTH_KEY] = value
        }
    }

    suspend fun saveRecyclesWaste(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[RECYCLES_WASTE_KEY] = value
        }
    }

    suspend fun saveSession(session: String) {
        context.dataStore.edit { preferences ->
            preferences[SESSION] = session
        }
    }

    fun getSession(): Flow<String?> {
        return context.dataStore.data.map { preferences -> preferences[SESSION] }
    }

    suspend fun getUserId(): String =
        context.dataStore.data
            .map { preferences -> preferences[USER_ID_KEY] }
            .firstOrNull() ?: UUID.randomUUID().toString()

    // Save wallet balance
    suspend fun saveWalletBalance(balance: Double) {
        context.dataStore.edit { preferences ->
            preferences[WALLET_BALANCE_KEY] = balance
        }
    }

    // Retrieve wallet balance
    suspend fun getWalletBalance(): Double =
        context.dataStore.data
            .map { preferences -> preferences[WALLET_BALANCE_KEY] }
            .firstOrNull() ?: 1000.0

    private val gson = Gson()

    // Save bought trees
    suspend fun saveBoughtTrees(boughtTrees: List<BoughtTree>) {
        val json = gson.toJson(boughtTrees)
        context.dataStore.edit { preferences ->
            preferences[BOUGHT_TREES_KEY] = json
        }
    }

    // Get bought trees
    fun getBoughtTrees(): Flow<List<BoughtTree>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[BOUGHT_TREES_KEY]
            if (json != null) {
                gson.fromJson(json, Array<BoughtTree>::class.java).toList()
            } else {
                emptyList()
            }
        }

    // Save store trees
    suspend fun saveStoreTrees(storeTrees: List<StoreTree>) {
        val json = gson.toJson(storeTrees)
        context.dataStore.edit { preferences ->
            preferences[STORE_TREES_KEY] = json
        }
    }

    // Get store trees
    fun getStoreTrees(): Flow<List<StoreTree>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[STORE_TREES_KEY]
            if (json != null) {
                gson.fromJson(json, Array<StoreTree>::class.java).toList()
            } else {
                emptyList()
            }
        }
}