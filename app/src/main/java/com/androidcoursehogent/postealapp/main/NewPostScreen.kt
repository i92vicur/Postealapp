package com.androidcoursehogent.postealapp.main

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.androidcoursehogent.postealapp.PostealappViewModel
import com.androidcoursehogent.postealapp.data.PostData


@Composable
fun NewPostScreen(navController: NavController, vm: PostealappViewModel, encodedUri: String) {

    val imageUri by remember { mutableStateOf(encodedUri) }
    var description by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var location by remember { mutableStateOf<String?>(null) }

    Surface (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Cancel", modifier = Modifier.clickable { navController.popBackStack() })
                Text(text = "Post", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.clickable {
                    focusManager.clearFocus()
                    // Asegurarse de que la ubicación se pasa al crear el post
                    vm.onNewPost(Uri.parse(imageUri), description, location) {
                        navController.popBackStack()
                    }
                })
            }

            CommonDivider()

            Image(
                painter = rememberAsyncImagePainter(model = imageUri),
                contentDescription = "newPost",
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp),
                contentScale = ContentScale.FillWidth
            )

            Row(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    label = { Text(text = "Description") },
                    singleLine = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedTextColor = Color.Black,
                        focusedTextColor = Color.Black
                    )
                )
            }

            Button(
                onClick = {
                    // Aquí abrirías un selector de ubicaciones, usando Google Maps o Places API.
                    // Por simplicidad, aquí podrías simular la selección de una ubicación:
                    location = "Selected Location, City, Country"  // Ejemplo de cadena de ubicación
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Add location")
            }

            // Mostrar la ubicación seleccionada
            location?.let {
                Text(text = "Selected location: $it", modifier = Modifier.padding(16.dp))
            }
        }
    }

    val inProgress = vm.inProgress.value
    if (inProgress) CommonProgressSpinner()

}

