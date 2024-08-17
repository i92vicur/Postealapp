package com.androidcoursehogent.postealapp.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import com.androidcoursehogent.postealapp.PostealappViewModel
import com.androidcoursehogent.postealapp.main.CommonProgressSpinner
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.androidcoursehogent.postealapp.DestinationScreen
import com.androidcoursehogent.postealapp.main.CommonDivider
import com.androidcoursehogent.postealapp.main.CommonImage
import com.androidcoursehogent.postealapp.main.navigateTo

@Composable
fun ProfileScreen(navController: NavController, vm: PostealappViewModel) {

    val isLoading = vm.inProgress.value

    if (isLoading) {
        CommonProgressSpinner()
    } else {
        val userData = vm.userData.value
        var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
        var username by rememberSaveable { mutableStateOf(userData?.username ?: "") }
        var bio by rememberSaveable { mutableStateOf(userData?.bio ?: "") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ){
            ProfileContent(vm = vm,
                name = name,
                username = username,
                bio = bio,
                onNameChange = { name = it },
                onUsernameChange = { username = it },
                onBioChange = { bio = it },
                onSave = { vm.updateProfileData(name, username, bio) },
                onBack = { navigateTo(navController = navController, DestinationScreen.MyPosts) },
                onLogout = {
                    vm.onLogout()
                    navigateTo(navController, DestinationScreen.Login)
                })
        }
    }

}

@Composable
fun ProfileContent(
    vm: PostealappViewModel,
    name: String,
    username: String,
    bio: String,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {

    val scrollState = rememberScrollState()

    val imageUrl = vm.userData?.value?.imageUrl

    var isDarkTheme by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.clickable { onBack.invoke() })
            Text(text = "Save", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.clickable { onSave.invoke() })
        }

        CommonDivider()

        ProfileImage(imageUrl = imageUrl, vm = vm)

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name", color = MaterialTheme.colorScheme.onTertiary, modifier = Modifier.width(100.dp))
            TextField(
                value = name, onValueChange = onNameChange, colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Username", color = MaterialTheme.colorScheme.onTertiary, modifier = Modifier.width(100.dp))
            TextField(
                value = username,
                onValueChange = onUsernameChange,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Bio", color = MaterialTheme.colorScheme.onTertiary, modifier = Modifier.width(100.dp))
            TextField(
                value = bio, onValueChange = onBioChange, colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black
                ),
                singleLine = false,
                modifier = Modifier.height(150.dp)
            )
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Select Theme", color = MaterialTheme.colorScheme.onTertiary)
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { isDarkTheme = it }
            )
            Text(if (isDarkTheme) "Dark" else "Light")

            Button(onClick = {
                vm.setTheme(isDarkTheme)},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent // Fondo transparente
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface)
            ) {
                Text("Save")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Logout", color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.clickable { onLogout.invoke() })
        }

    }

}


@Composable
fun ProfileImage(imageUrl: String?, vm: PostealappViewModel) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {vm.uploadProfileImage(uri)}
    }

    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { launcher.launch("image/*") },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageUrl)
            }
            Text(text = "Change profile picture")
        }

        val isLoading = vm.inProgress.value
        if (isLoading) CommonProgressSpinner()
    }

}

