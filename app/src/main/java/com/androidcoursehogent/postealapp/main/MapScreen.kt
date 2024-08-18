package com.androidcoursehogent.postealapp.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import com.google.maps.android.compose.MarkerState
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navController: NavController, onLocationSelected: (String) -> Unit) {

    // Permissions for location
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val cameraPositionState = rememberCameraPositionState()

    Surface {
        if (locationPermissionState.status.isGranted) {
            val coroutineScope = rememberCoroutineScope()
            val markerState = remember { MarkerState(position = cameraPositionState.position.target) }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(zoomControlsEnabled = true),
                onMapClick = { latLng ->
                    // Clear previous markers and add a new one
                    coroutineScope.launch {
                        markerState.position = latLng
                        val location = "${latLng.latitude},${latLng.longitude}"
                        onLocationSelected(location)
                        navController.popBackStack()
                    }
                }
            ) {
                Marker(
                    state = markerState,
                    title = "Selected Location",
                    snippet = "Lat: ${markerState.position.latitude}, Lng: ${markerState.position.longitude}"
                )
            }
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }
}

