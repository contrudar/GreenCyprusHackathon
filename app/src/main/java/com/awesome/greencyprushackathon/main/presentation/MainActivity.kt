package com.awesome.greencyprushackathon.main.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.awesome.greencyprushackathon.features.carbon_footprint.presentation.CarbonFootprintScreen
import com.awesome.greencyprushackathon.features.plant_shop.presentation.PlantShopScreen
import com.awesome.greencyprushackathon.features.profile.presentation.ProfileScreen
import com.awesome.greencyprushackathon.features.your_impact.model.Tree
import com.awesome.greencyprushackathon.features.your_impact.presentation.YourImpactScreen
import com.awesome.greencyprushackathon.features.your_impact.presentation.YourImpactViewModel
import com.awesome.greencyprushackathon.resources.Trees
import com.awesome.greencyprushackathon.ui.theme.GreenCyprusHackathonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreenCyprusHackathonTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        var selectedTab: MainTabs by remember { mutableStateOf(MainTabs.CarbonFootprint) }
        val yourImpactViewModel: YourImpactViewModel = viewModel()

        // State to store the currently selected tree
        var selectedTree by remember { mutableStateOf<Tree?>(null) }

        Scaffold(
            floatingActionButton = {
                if (selectedTab == MainTabs.YourImpact) {
                    val isMapMode by yourImpactViewModel.isMapMode.collectAsStateWithLifecycle()
                    FloatingActionButton(
                        modifier = Modifier.zIndex(1f),
                        onClick = { yourImpactViewModel.toggleMode() }
                    ) {
                        Icon(
                            imageVector = if (isMapMode) Icons.AutoMirrored.Filled.List else Icons.Rounded.Place,
                            contentDescription = "Toggle View"
                        )
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == MainTabs.CarbonFootprint,
                        onClick = { selectedTab = MainTabs.CarbonFootprint },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Carbon Footprint") },
                        label = { CenteredLabel("Carbon Footprint") },
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTabs.YourImpact,
                        onClick = { selectedTab = MainTabs.YourImpact },
                        icon = { Icon(Trees, contentDescription = "Your Impact") },
                        label = { CenteredLabel("Your Impact") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTabs.PlantShop,
                        onClick = { selectedTab = MainTabs.PlantShop },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Plant Shop") },
                        label = { CenteredLabel("Plant Shop") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == MainTabs.Profile,
                        onClick = { selectedTab = MainTabs.Profile },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { CenteredLabel("Profile") }
                    )
                }
            },
            content = { paddingValues: PaddingValues ->
                when (selectedTab) {
                    MainTabs.CarbonFootprint -> CarbonFootprintScreen(modifier = Modifier.padding(paddingValues))
                    MainTabs.YourImpact -> YourImpactScreen(
                        viewModel = yourImpactViewModel,
                        modifier = Modifier.padding(paddingValues),
                        onNavigateToMap = { tree ->
                            selectedTree = tree // Set the selected tree
                            yourImpactViewModel.setMapMode(isMap = true)
                        },
                        selectedTree = selectedTree,
                        onResetSelectedTree = { selectedTree = null }
                    )

                    MainTabs.PlantShop -> PlantShopScreen(modifier = Modifier.padding(paddingValues))
                    MainTabs.Profile -> ProfileScreen(modifier = Modifier.padding(paddingValues))
                }
            }
        )
    }

    @Composable
    fun CenteredLabel(label: String) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                label,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }

    enum class MainTabs {
        CarbonFootprint,
        YourImpact,
        PlantShop,
        Profile
    }
}
