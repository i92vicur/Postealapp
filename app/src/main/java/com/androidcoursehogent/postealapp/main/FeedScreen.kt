package com.androidcoursehogent.postealapp.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidcoursehogent.postealapp.DestinationScreen
import com.androidcoursehogent.postealapp.PostealappViewModel
import com.androidcoursehogent.postealapp.R
import com.androidcoursehogent.postealapp.data.PostData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(navController: NavController, vm: PostealappViewModel) {

    val userDataLoading = vm.inProgress.value
    val userData = vm.userData.value

    val personalizedFeed = vm.postsFeed.value
    val personalizedFeedLoading = vm.postsFeedProgress.value

    val colorScheme = MaterialTheme.colorScheme

    //Para lo del loguito
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserImageCard(userImage = userData?.imageUrl)

            val logoResource = if (isDarkTheme) {
                R.drawable.app_name_dark_ic
            } else {
                R.drawable.app_name_ic
            }

            Image(
                painter = painterResource(logoResource),
                contentDescription = "app name logo",
                modifier = Modifier
                    .width(160.dp)
                    .padding(8.dp)
            )

            Image(
                painter = painterResource(R.drawable.ic_feed_app_logo),
                contentDescription = "app icon logo",
                modifier = Modifier
                    .height(60.dp)
                    .padding(8.dp)
                    .padding(end = 15.dp)
            )
        }
        PostsList(
            posts = personalizedFeed,
            modifier = Modifier.weight(1f),
            loading = personalizedFeedLoading or userDataLoading,
            navController = navController,
            vm = vm,
            currentUserId = userData?.userId ?: ""
        )

        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.FEED,
            navController = navController
        )
    }

}

@Composable
fun PostsList(
    posts: List<PostData>,
    modifier: Modifier,
    loading: Boolean,
    navController: NavController,
    vm: PostealappViewModel,
    currentUserId: String
) {
    Box(modifier = modifier) {
        LazyColumn {
            items(items = posts) { post ->
                Post(
                    post = post,
                    currentUserId = currentUserId,
                    vm
                ) {
                    navController.currentBackStackEntry?.savedStateHandle?.set("post", post)
                    navController.navigate(DestinationScreen.SinglePost.route)
                }
            }
        }

        if (loading) CommonProgressSpinner()
    }
}

@Composable
fun Post(post: PostData, currentUserId: String, vm: PostealappViewModel, onPosClick: () -> Unit) {

    val likeAnimation = remember { mutableStateOf(false) }
    val dislikeAnimation = remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(corner = CornerSize(4.dp)),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape, modifier = Modifier
                        .padding(4.dp)
                        .size(32.dp)
                ) {
                    CommonImage(data = post.userImage, contentScale = ContentScale.Crop)
                }
                Text(text = post.username ?: "", modifier = Modifier.padding(4.dp))
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
                    .pointerInput(Unit){
                        detectTapGestures(
                            onDoubleTap = {
                                if(post.likes?.contains(currentUserId) == true){
                                    dislikeAnimation.value = true
                                } else {
                                    likeAnimation.value = true
                                }
                                vm.onLikePost(post)
                            },
                            onTap = {
                                onPosClick.invoke()
                            }
                        )
                    }

                CommonImage(
                    data = post.postImage,
                    modifier = modifier,
                    contentScale = ContentScale.FillWidth
                )

                if(likeAnimation.value) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        likeAnimation.value = false
                    }
                    LikeAnimation()
                }

                if(dislikeAnimation.value) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000L)
                        dislikeAnimation.value = false
                    }
                    LikeAnimation(false)
                }
            }
        }
    }
}
