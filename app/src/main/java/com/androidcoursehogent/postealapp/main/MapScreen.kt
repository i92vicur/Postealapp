package com.androidcoursehogent.postealapp.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.media3.common.util.Log
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navController: NavController, onLocationSelected: (String) -> Unit) {

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.748817, -73.985428), 10f) // Ejemplo con una posición predeterminada
    }

    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var selectedLocationText by remember { mutableStateOf("No location selected") }

    LaunchedEffect(locationPermissionState.status) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    if (locationPermissionState.status.isGranted) {
        Column(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(zoomControlsEnabled = true),
                onMapClick = { latLng ->
                    markerPosition = latLng
                    selectedLocationText = "Selected location: ${latLng.latitude}, ${latLng.longitude}"
                }
            ) {
                markerPosition?.let { position ->
                    Marker(
                        state = MarkerState(position = position),
                        title = "Selected Location",
                        snippet = "Lat: ${position.latitude}, Lng: ${position.longitude}"
                    )
                }
            }

            markerPosition?.let {
                Button(
                    onClick = {
                        // Guarda la ubicación seleccionada en el `savedStateHandle`
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("location_data", "${it.latitude},${it.longitude}")
                        // Navega de vuelta a la pantalla de creación de post
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Select")
                }
            }

            // Mostrar la ubicación seleccionada debajo del mapa
            Text(
                text = selectedLocationText,
                color = Color.DarkGray,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        Text("Location permission is required to display the map.")
    }
}
