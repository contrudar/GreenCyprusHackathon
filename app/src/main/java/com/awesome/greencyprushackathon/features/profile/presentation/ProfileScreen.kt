package com.awesome.greencyprushackathon.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.awesome.greencyprushackathon.resources.PersonCircle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    modifier: Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Profile Icon
        Icon(
            imageVector = PersonCircle,
            contentDescription = "Profile Icon",
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally),
            tint = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.treesNeeded == 0) {
            Text(
                text = "You need to complete the questionnaire about your carbon footprint to get started.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            ProfileProgressSection(
                treesBought = uiState.treesBought,
                treesNeeded = uiState.treesNeeded
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wallet Balance
        val formattedBalance = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }.format(uiState.walletBalance)

        Text(
            text = "Wallet Balance: €${formattedBalance}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Deposit Money
        var depositAmount by remember { mutableStateOf("") }
        OutlinedTextField(
            value = depositAmount,
            onValueChange = { depositAmount = it },
            label = { Text("Enter Amount") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val amount = depositAmount.toDoubleOrNull() ?: 0.0
                viewModel.depositMoney(amount)
                depositAmount = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Deposit")
        }
    }
}


@Composable
fun ProfileProgressSection(
    treesBought: Int,
    treesNeeded: Int,
    modifier: Modifier = Modifier
) {
    val progress = remember(treesBought, treesNeeded) {
        if (treesNeeded == 0) 0f else treesBought.toFloat() / treesNeeded.toFloat()
    }

    val progressPercentage = remember(treesBought, treesNeeded) {
        if (treesNeeded == 0) 0 else (100 - (treesBought.toFloat() / treesNeeded.toFloat() * 100)).toInt()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = "You're doing amazing! Just $progressPercentage% to go!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "With your efforts, you'll help reduce your carbon footprint to zero. Keep it up!",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp) // Increased height for better visibility
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray.copy(alpha = 0.2f)) // Background track
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress.coerceIn(0f, 1f)) // Ensure no overflow
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                    .background(Color.Green)
            )
        }

        // Progress Text
        Text(
            text = "$treesBought of $treesNeeded trees bought or ready to be planted",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
            color = Color.DarkGray
        )
    }
}


