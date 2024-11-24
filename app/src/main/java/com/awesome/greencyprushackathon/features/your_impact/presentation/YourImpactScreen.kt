package com.awesome.greencyprushackathon.features.your_impact.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.awesome.greencyprushackathon.features.your_impact.model.Tree
import com.awesome.greencyprushackathon.features.your_impact.model.TreeStatus
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun YourImpactScreen(
    viewModel: YourImpactViewModel,
    modifier: Modifier = Modifier,
    onNavigateToMap: (Tree) -> Unit,
    selectedTree: Tree?,
    onResetSelectedTree: () -> Unit
) {
    val trees by viewModel.trees.collectAsStateWithLifecycle()
    val isMapMode by viewModel.isMapMode.collectAsStateWithLifecycle()

    // Camera position state for the map
    val cameraPositionState = remember {
        CameraPositionState(
            position = CameraPosition.fromLatLngZoom(
                LatLng(35.0450, 33.2299), // Default to Cyprus
                7.8f // Default zoom level
            )
        )
    }

    // If a tree is selected, zoom into its position
    selectedTree?.let {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0),
            15f
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadTrees()
    }

    DisposableEffect(Unit) {
        onDispose {
            onResetSelectedTree()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (isMapMode) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                trees.filter { it.latitude != null && it.longitude != null }.forEach { tree ->
                    Marker(
                        state = MarkerState(position = LatLng(tree.latitude ?: 0.0, tree.longitude ?: 0.0)),
                        title = tree.type,
                        snippet = "Planted on ${tree.plantedDate}",
                        icon = if (tree == selectedTree) {
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        } else {
                            BitmapDescriptorFactory.defaultMarker()
                        }
                    )
                }
            }
        } else {
            LazyColumn {
                items(trees) { tree ->
                    TreeListItem(tree = tree, onNavigateToMap = onNavigateToMap)
                }
            }
        }
    }
}

@Composable
fun TreeListItem(
    tree: Tree,
    onNavigateToMap: (Tree) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(CornerSize(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column for text content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = tree.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Id: ${tree.id}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = tree.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Status: ${tree.status.status}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (tree.status == TreeStatus.PLANTED) {
                    Text(
                        text = "Planted on: ${tree.plantedDate}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Age: ${calculateTreeAgeLegacy(tree.plantedDate)}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onNavigateToMap(tree) },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text("View on Map")
                    }
                }
            }

            Image(
                painter = rememberAsyncImagePainter(tree.photoUrl),
                contentScale = ContentScale.Crop,
                contentDescription = "Tree Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

fun calculateTreeAgeLegacy(plantedDate: String?): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Adjust format as needed
        val plantingDate = sdf.parse(plantedDate!!) ?: return "Unknown age"
        val currentDate = Calendar.getInstance()

        val plantingCalendar = Calendar.getInstance().apply { time = plantingDate }

        val years = currentDate.get(Calendar.YEAR) - plantingCalendar.get(Calendar.YEAR)
        val months = currentDate.get(Calendar.MONTH) - plantingCalendar.get(Calendar.MONTH)

        when {
            years > 0 -> "$years years${if (months > 0) " and $months months" else ""}"
            months > 0 -> "$months months"
            else -> "Less than a month"
        }
    } catch (e: Exception) {
        "Unknown age"
    }
}



