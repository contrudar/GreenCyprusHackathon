package com.awesome.greencyprushackathon.features.carbon_footprint.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awesome.greencyprushackathon.features.carbon_footprint.presentation.CarbonFootprintViewModel
import com.awesome.greencyprushackathon.resources.Trees

@Composable
fun DietStep(viewModel: CarbonFootprintViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Step 3: Diet and Food Consumption", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text("How many meals per week include meat?", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        SliderInput(
            value = viewModel.weeklyMeatMeals.intValue,
            onValueChange = { viewModel.weeklyMeatMeals.intValue = it },
            range = 0..21,
            label = "Meals with Meat"
        )
    }
}

@Composable
fun EnergyConsumptionStep(viewModel: CarbonFootprintViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Step 2: Energy Consumption", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text("What is your average monthly electricity consumption (kWh)?", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        SliderInput(
            value = viewModel.monthlyElectricityUsage.intValue,
            onValueChange = { viewModel.monthlyElectricityUsage.intValue = it },
            range = 0..1000,
            label = "Electricity (kWh)"
        )
    }
}

@Composable
fun ResultStep(viewModel: CarbonFootprintViewModel) {
    Column(
        modifier = Modifier
            .padding(24.dp) // Add padding around the whole section for better spacing
            .fillMaxWidth() // Ensure the column takes up full width
    ) {
        // Step title
        Text(
            text = "Step 5: Result",
            style = MaterialTheme.typography.headlineLarge, // Larger headline for better emphasis
            modifier = Modifier.padding(bottom = 16.dp) // Extra space below the title
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            text = "Total Carbon Footprint:",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground, // Ensure text is legible on background
        )
        Text(
            text = "${viewModel.carbonFootprint.intValue} kg CO₂ per year",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, fontSize = 48.sp),
            color = MaterialTheme.colorScheme.primary, // Use primary color for important value
        )

        Spacer(modifier = Modifier.height(32.dp)) // Add space between the two text elements

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            text = "Trees Needed To Be Planted to Offset:",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "${viewModel.treesNeeded.intValue}",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, fontSize = 48.sp),
                color = MaterialTheme.colorScheme.primary,
            )

            Icon(
                imageVector = Trees,
                contentDescription = "Tree Icon",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(56.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun ShoppingAndWasteStep(viewModel: CarbonFootprintViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Step 4: Shopping and Waste", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text("How many new clothing items do you buy per month?", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        SliderInput(
            value = viewModel.newClothingItemsPerMonth.intValue,
            onValueChange = { viewModel.newClothingItemsPerMonth.intValue = it },
            range = 0..30,
            label = "New Clothing Items"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Do you recycle and compost your waste?", style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = viewModel.recyclesWaste.value,
            onCheckedChange = { viewModel.recyclesWaste.value = it }
        )
    }
}

@Composable
fun SliderInput(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first
        )
        Text("Value: $value", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun TransportationStep(viewModel: CarbonFootprintViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Step 1: Transportation", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text("How many kilometers do you drive per week?", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        SliderInput(
            value = viewModel.weeklyKilometersDriven.intValue,
            onValueChange = { viewModel.weeklyKilometersDriven.intValue = it },
            range = 0..2000,
            label = "Weekly Kilometers"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "How many short-haul flights (less than 3 hours) do you take per year?",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        SliderInput(
            value = viewModel.shortHaulFlightsPerYear.intValue,
            onValueChange = { viewModel.shortHaulFlightsPerYear.intValue = it },
            range = 0..40,
            label = "Short-Haul Flights"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "How many long-haul flights (more than 3 hours) do you take per year?",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        SliderInput(
            value = viewModel.longHaulFlightsPerYear.intValue,
            onValueChange = { viewModel.longHaulFlightsPerYear.intValue = it },
            range = 0..20,
            label = "Long-Haul Flights"
        )
    }
}
