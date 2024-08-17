package com.androidcoursehogent.postealapp.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import  androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.androidcoursehogent.postealapp.DestinationScreen
import com.androidcoursehogent.postealapp.PostealappViewModel
import com.androidcoursehogent.postealapp.R
import com.androidcoursehogent.postealapp.main.CheckSignedIn
import com.androidcoursehogent.postealapp.main.CommonProgressSpinner
import com.androidcoursehogent.postealapp.main.navigateTo

@Composable
fun SignupScreen(navController: NavController, vm: PostealappViewModel) {

    CheckSignedIn(vm = vm, navController = navController)

    val focus = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val usernameState = remember { mutableStateOf(TextFieldValue()) }
            val emailState = remember { mutableStateOf(TextFieldValue()) }
            val passState = remember { mutableStateOf(TextFieldValue()) }

            Row(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalArrangement = Arrangement.Center)
            {
                Image(
                    painter = painterResource(id = R.drawable.logo_portada_login),
                    contentDescription = "applogo",
                    modifier = Modifier
                        .width(320.dp)
                        .padding(8.dp)
                )
            }

            Text(
                text = "Signup",
                modifier = Modifier.padding(16.dp),
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = usernameState.value,
                onValueChange = {
                    val filteredUsername = it.text.replace(" ", "") //Elimina los espacios
                    usernameState.value = TextFieldValue(filteredUsername)
                },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Username") }
            )

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Email") }
            )

            OutlinedTextField(
                value = passState.value,
                onValueChange = { passState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = {
                    focus.clearFocus(force = true)
                    vm.onSignup(
                        usernameState.value.text,
                        emailState.value.text,
                        passState.value.text
                    )
                }, modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "SIGN UP", color = Color.White)
            }
            Text(
                text = "Already a user? Go to login ->",
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .padding(12.dp)
                    .clickable {
                        navigateTo(navController, DestinationScreen.Login)
                    }
            )
        }

        val isLoading = vm.inProgress.value
        if (isLoading) {
            CommonProgressSpinner()
        }

    }
}

