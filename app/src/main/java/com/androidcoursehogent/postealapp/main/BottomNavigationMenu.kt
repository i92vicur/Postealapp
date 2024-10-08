package com.androidcoursehogent.postealapp.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidcoursehogent.postealapp.DestinationScreen
import com.androidcoursehogent.postealapp.R

enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen) {

    FEED(R.drawable.ic_home, DestinationScreen.Feed),
    SEARCH(R.drawable.ic_search, DestinationScreen.Search),
    POSTS(R.drawable.ic_posts, DestinationScreen.MyPosts)

}

@Composable
fun BottomNavigationMenu(selectedItem: BottomNavigationItem, navController: NavController) {

    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 5.dp)
            .background(colorScheme.background)
    ) {
        for (item in BottomNavigationItem.values()) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = "BottomMenuOption",
                modifier = Modifier
                    .size(45.dp)
                    .padding(5.dp)
                    .weight(1f)
                    .clickable {
                        navigateTo(navController, item.navDestination)
                    },
                colorFilter = if (item == selectedItem) {
                    ColorFilter.tint(colorScheme.onSurface) // Usa el color del ícono seleccionado
                } else {
                    ColorFilter.tint(colorScheme.onSurfaceVariant) // Color para ícono no seleccionado
                }
            )
        }
    }
}