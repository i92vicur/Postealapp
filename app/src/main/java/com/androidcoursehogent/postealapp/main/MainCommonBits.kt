package com.androidcoursehogent.postealapp.main

import android.os.Parcelable
import com.androidcoursehogent.postealapp.R
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.media3.effect.Crop
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.androidcoursehogent.postealapp.DestinationScreen
import com.androidcoursehogent.postealapp.PostealappViewModel

@Composable
fun NotificationMessage(vm: PostealappViewModel) {

    val notifState = vm.popupNotification.value
    val notifMessage = notifState?.getContentOrNull()
    if (notifMessage != null) {
        Toast.makeText(LocalContext.current, notifMessage, Toast.LENGTH_LONG).show()
    }

}

@Composable
fun CommonProgressSpinner() {

    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) { }
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator()
    }

}

data class NavParameters(
    val name: String,
    val value: Parcelable
)

fun navigateTo(
    navController: NavController,
    dest: DestinationScreen,
    vararg parameters: NavParameters
) {

    for (parameter in parameters) {
        navController.currentBackStackEntry?.arguments?.putParcelable(
            parameter.name,
            parameter.value
        )
    }

    navController.navigate(dest.route) {
        popUpTo(dest.route)
        launchSingleTop = true
    }

}

@Composable
fun CheckSignedIn(vm: PostealappViewModel, navController: NavController) {

    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = vm.signedIn.value
    if (signedIn && !alreadyLoggedIn.value) {
        alreadyLoggedIn.value = true
        navController.navigate(DestinationScreen.Feed.route) {
            popUpTo(0)
        }
    }

}

@Composable
fun CommonImage(
    data: String?,
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {

    val painter = rememberAsyncImagePainter(model = data)

    Image(
        painter = painter,
        contentDescription = "Profile picture",
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    if (painter.state is AsyncImagePainter.State.Loading) {
        CommonProgressSpinner()
    }

}

@Composable
fun UserImageCard(
    userImage: String?, modifier: Modifier = Modifier
        .padding(10.dp)
        .size(58.dp)
) {

    Card(
        shape = RoundedCornerShape(16.dp), // Cambia CircleShape a RoundedCornerShape con el radio deseado
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ){
            if (userImage.isNullOrEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
            } else {
                CommonImage(data = userImage)
            }
        }
    }

}

@Composable
fun CommonDivider() {
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 8.dp, bottom = 8.dp)
    )
}

private enum class LikeIconSize {
    SMALL,
    LARGE
}

@Composable
fun LikeAnimation(like: Boolean = true) {

    var sizeState by remember { mutableStateOf(LikeIconSize.SMALL) }
    val transition = updateTransition(targetState = sizeState, label = "")
    val size by transition.animateDp(
        label = "",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }
    ) { state ->
        when (state) {
            LikeIconSize.SMALL -> 0.dp
            LikeIconSize.LARGE -> 150.dp
        }
    }

    Image(
        painter = painterResource(id = if (like) R.drawable.ic_fav else R.drawable.ic_dislike),
        contentDescription = "",
        modifier = Modifier.size(size = size),
        colorFilter = ColorFilter.tint(if(like) Color.Red else Color.Gray)
    )

    sizeState = LikeIconSize.LARGE

}