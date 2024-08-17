package com.androidcoursehogent.postealapp.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidcoursehogent.postealapp.DestinationScreen
import com.androidcoursehogent.postealapp.PostealappViewModel

@Composable
fun SearchScreen(navController: NavController, vm: PostealappViewModel) {

    val searchedPostsLoading = vm.searchedPostProgress.value
    val searchedPosts = vm.searchedPosts.value
    var searchTerm by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        SearchBar(
            searchTerm = searchTerm,
            onSearchChange = { searchTerm = it },
            onSearch = { vm.searchPosts(searchTerm) }
        )
        PostList(
            isContextLoading = false,
            postsLoading = searchedPostsLoading,
            posts = searchedPosts,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
        ) { post ->
            // Guarda el PostData en el savedStateHandle antes de navegar
            navController.currentBackStackEntry?.savedStateHandle?.set("post", post)
            navController.navigate(DestinationScreen.SinglePost.route)
        }

        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.SEARCH,
            navController = navController
        )

    }

}

@Composable
fun SearchBar(searchTerm: String, onSearchChange: (String) -> Unit, onSearch: () -> Unit) {

    val focusManager = LocalFocusManager.current

    TextField(
        value = searchTerm,
        onValueChange = onSearchChange,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, CircleShape),
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                focusManager.clearFocus()
            }
        ),
        maxLines = 1,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        trailingIcon = {
            IconButton(onClick = {
                onSearch()
                focusManager.clearFocus()
            }) {
                Icon(imageVector = Icons.Filled.Search, tint = MaterialTheme.colorScheme.onSurface, contentDescription = "Search bar icon")
            }
        }
    )

}
