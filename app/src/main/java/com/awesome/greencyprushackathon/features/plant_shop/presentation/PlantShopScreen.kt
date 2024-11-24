package com.awesome.greencyprushackathon.features.plant_shop.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.awesome.greencyprushackathon.network.model.StoreTree

@Composable
fun PlantShopScreen(viewModel: PlantShopViewModel = viewModel(), modifier: Modifier) {
    val storeTrees by viewModel.storeTrees.collectAsState()
    val notification by viewModel.notification.collectAsState()

    // Remember the SnackbarHostState so it doesn't get recreated on every recomposition
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar whenever notification changes
    LaunchedEffect(notification) {
        notification?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearNotification()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(storeTrees.size) { index ->
            val tree = storeTrees[index]
            StoreTreeCard(tree = tree, onBuyClicked = { viewModel.buyTree(tree.type) })
        }
    }

    // Notification Snackbar
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(16.dp)
    ) { snackbarData ->
        Snackbar(
            action = {
                TextButton(onClick = { viewModel.clearNotification() }) {
                    Text("Dismiss")
                }
            }
        ) { Text(snackbarData.visuals.message) }  // Directly use `it` (the notification message) here
    }
}

@Composable
fun StoreTreeCard(tree: StoreTree, onBuyClicked: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left-side content: Title, Description, Price, and Button
            Column(
                modifier = Modifier
                    .weight(1f) // Takes remaining space
                    .padding(end = 8.dp)
            ) {
                // Title
                Text(
                    text = tree.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Description
                Text(
                    text = tree.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price and Buy Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${tree.price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(onClick = onBuyClicked) {
                        Text("Buy")
                    }
                }
            }

            // Right-side: Tree Image
            AsyncImage(
                model = tree.photoUrl,
                contentDescription = "${tree.title} Image",
                modifier = Modifier
                    .size(100.dp) // Set fixed size for the image
                    .clip(MaterialTheme.shapes.medium)
                    .align(Alignment.CenterVertically), // Align with center vertically
                contentScale = ContentScale.Crop
            )
        }
    }
}

