package com.awesome.greencyprushackathon.features.carbon_footprint.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.awesome.greencyprushackathon.features.carbon_footprint.presentation.components.DietStep
import com.awesome.greencyprushackathon.features.carbon_footprint.presentation.components.EnergyConsumptionStep
import com.awesome.greencyprushackathon.features.carbon_footprint.presentation.components.ResultStep
import com.awesome.greencyprushackathon.features.carbon_footprint.presentation.components.ShoppingAndWasteStep
import com.awesome.greencyprushackathon.features.carbon_footprint.presentation.components.TransportationStep

@Composable
fun CarbonFootprintScreen(viewModel: CarbonFootprintViewModel = viewModel(), modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when (viewModel.currentStep.intValue) {
            1 -> TransportationStep(viewModel)
            2 -> EnergyConsumptionStep(viewModel)
            3 -> DietStep(viewModel)
            4 -> ShoppingAndWasteStep(viewModel)
            5 -> ResultStep(viewModel)
            else -> Unit
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (viewModel.currentStep.intValue > 1) {
                Button(onClick = { viewModel.previousStep() }) {
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            if (viewModel.currentStep.intValue < 5) {
                Button(onClick = { viewModel.nextStep() }) {
                    Text("Next")
                }
            }
        }
    }
}