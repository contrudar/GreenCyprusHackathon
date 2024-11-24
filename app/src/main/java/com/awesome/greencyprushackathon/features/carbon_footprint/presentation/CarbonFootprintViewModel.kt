package com.awesome.greencyprushackathon.features.carbon_footprint.presentation

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awesome.greencyprushackathon.features.carbon_footprint.domain.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarbonFootprintViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    companion object {
        // Constants for CO2 calculation
        private const val CAR_EMISSIONS_PER_KM = 0.2 // kg CO₂ per km
        private const val ELECTRICITY_EMISSIONS_PER_KWH = 0.45 // kg CO₂ per kWh
        private const val MEAT_MEAL_EMISSIONS = 5 // kg CO₂ per meal
        private const val SHORT_HAUL_FLIGHT_EMISSIONS = 300 // kg CO₂ per flight
        private const val LONG_HAUL_FLIGHT_EMISSIONS = 1000 // kg CO₂ per flight
        private const val CLOTHING_ITEM_EMISSIONS = 50 // kg CO₂ per item
        private const val CO2_ABSORPTION_PER_TREE = 20 // kg CO₂ per tree per year
    }

    // State for user inputs
    val weeklyKilometersDriven: MutableIntState = mutableIntStateOf(0)

    val monthlyElectricityUsage: MutableIntState = mutableIntStateOf(0)

    val weeklyMeatMeals: MutableIntState = mutableIntStateOf(0)

    val shortHaulFlightsPerYear: MutableIntState = mutableIntStateOf(0)

    val longHaulFlightsPerYear: MutableIntState = mutableIntStateOf(0)

    val newClothingItemsPerMonth: MutableIntState = mutableIntStateOf(0)

    val recyclesWaste: MutableState<Boolean> = mutableStateOf(false)

    // State for calculated values
    val carbonFootprint: MutableIntState = mutableIntStateOf(0)

    val treesNeeded: MutableIntState = mutableIntStateOf(0)

    // State for the current step
    val currentStep: MutableIntState = mutableIntStateOf(-1)

    init {
        combine(
            dataStoreRepository.getCarbonFootprint(),
            dataStoreRepository.getTreesNeeded()
        ) { carbonFootprint, treesNeeded ->
            currentStep.intValue = if (carbonFootprint != null && treesNeeded != null) 5 else 1
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            carbonFootprint.intValue = dataStoreRepository.getCarbonFootprint().firstOrNull() ?: 0
            treesNeeded.intValue = dataStoreRepository.getTreesNeeded().firstOrNull() ?: 0
            weeklyMeatMeals.intValue = dataStoreRepository.getWeeklyMeatMeals().firstOrNull() ?: 0
            weeklyKilometersDriven.intValue = dataStoreRepository.getWeeklyKilometersDriven().firstOrNull() ?: 0
            monthlyElectricityUsage.intValue = dataStoreRepository.getMonthlyElectricityUsage().firstOrNull() ?: 0
            shortHaulFlightsPerYear.intValue = dataStoreRepository.getShortHaulFlightsPerYear().firstOrNull() ?: 0
            longHaulFlightsPerYear.intValue = dataStoreRepository.getLongHaulFlightsPerYear().firstOrNull() ?: 0
            newClothingItemsPerMonth.intValue = dataStoreRepository.getNewClothingItemsPerMonth().firstOrNull() ?: 0
            recyclesWaste.value = dataStoreRepository.getRecyclesWaste().firstOrNull() ?: false
        }
    }

    // Methods to navigate between steps
    fun nextStep() {
        if (currentStep.intValue < 5) currentStep.value += 1
        if (currentStep.intValue == 5) calculateCarbonFootprint()
    }

    fun previousStep() {
        if (currentStep.intValue > 1) currentStep.value -= 1
    }

    // Logic to calculate the carbon footprint and trees needed
    private fun calculateCarbonFootprint() {
        val transportationEmissions = weeklyKilometersDriven.intValue * CAR_EMISSIONS_PER_KM * 52
        val electricityEmissions = monthlyElectricityUsage.intValue * ELECTRICITY_EMISSIONS_PER_KWH * 12
        val dietEmissions = weeklyMeatMeals.intValue * MEAT_MEAL_EMISSIONS * 52
        val flightEmissions =
            shortHaulFlightsPerYear.intValue * SHORT_HAUL_FLIGHT_EMISSIONS + longHaulFlightsPerYear.intValue * LONG_HAUL_FLIGHT_EMISSIONS
        val clothingEmissions = newClothingItemsPerMonth.intValue * CLOTHING_ITEM_EMISSIONS * 12

        var totalEmissions =
            transportationEmissions + electricityEmissions + dietEmissions + flightEmissions + clothingEmissions

        // Apply a reduction if the user recycles waste
        if (recyclesWaste.value) {
            totalEmissions *= 0.8 // Reduce emissions by 20%
        }

        carbonFootprint.intValue = totalEmissions.toInt()

        val trees = (totalEmissions / CO2_ABSORPTION_PER_TREE).toInt()
        treesNeeded.intValue = trees

        saveQuestionnaireResults(
            carbonFootprint = totalEmissions.toInt(),
            treesNeeded = trees,
            weeklyKilometersDriven = weeklyKilometersDriven.intValue,
            monthlyElectricityUsage = monthlyElectricityUsage.intValue,
            weeklyMeatMeals = weeklyMeatMeals.intValue,
            shortHaulFlightsPerYear = shortHaulFlightsPerYear.intValue,
            longHaulFlightsPerYear = longHaulFlightsPerYear.intValue,
            newClothingItemsPerMonth = newClothingItemsPerMonth.intValue,
            recyclesWaste = recyclesWaste.value
        )
    }

    private fun saveQuestionnaireResults(
        carbonFootprint: Int,
        treesNeeded: Int,
        weeklyKilometersDriven: Int,
        monthlyElectricityUsage: Int,
        weeklyMeatMeals: Int,
        shortHaulFlightsPerYear: Int,
        longHaulFlightsPerYear: Int,
        newClothingItemsPerMonth: Int,
        recyclesWaste: Boolean
    ) {
        viewModelScope.launch {
            dataStoreRepository.apply {
                saveCarbonFootprint(carbonFootprint)
                saveTreesNeeded(treesNeeded)
                saveWeeklyKilometersDriven(weeklyKilometersDriven)
                saveMonthlyElectricityUsage(monthlyElectricityUsage)
                saveWeeklyMeatMeals(weeklyMeatMeals)
                saveShortHaulFlightsPerYear(shortHaulFlightsPerYear)
                saveLongHaulFlightsPerYear(longHaulFlightsPerYear)
                saveNewClothingItemsPerMonth(newClothingItemsPerMonth)
                saveRecyclesWaste(recyclesWaste)
            }
        }
    }
}