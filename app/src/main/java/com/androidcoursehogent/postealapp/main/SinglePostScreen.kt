package com.androidcoursehogent.postealapp.main

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.androidcoursehogent.postealapp.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.androidcoursehogent.postealapp.DestinationScreen
import com.androidcoursehogent.postealapp.PostealappViewModel
import com.androidcoursehogent.postealapp.data.PostData
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinglePostScreen(navController: NavController, vm: PostealappViewModel, post: PostData) {

    val comments = vm.comments.value
    val updatedPost = vm.posts.value.find { it.postId == post.postId } ?: post
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = post.postId!!) {
        vm.getComments(post.postId!!)
        vm.observePostLikes(post.postId!!)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete Post") },
            text = { Text("Are you sure you want to delete this post?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        vm.deletePost(post.postId!!, navController)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        updatedPost.userId?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Back",
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                    )

                    if (updatedPost.userId == vm.userData.value?.userId) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Delete",
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier
                                    .clickable { showDeleteDialog = true }
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = "delete trash can icon",
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(horizontal = 4.dp),
                            )
                        }
                    }
                }

                CommonDivider()

                SinglePostDisplay(
                    navController = navController,
                    vm = vm,
                    post = updatedPost,
                    nbComments = comments.size
                )
            }
        }
    }

}

@Composable
fun SinglePostDisplay(

    navController: NavController,
    vm: PostealappViewModel,
    post: PostData,
    nbComments: Int
) {
    val userData = vm.userData.value
    var isLikedAnimationVisible by remember { mutableStateOf(false) }
    val scale = remember { Animatable(0f) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = CircleShape, modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = post.userImage),
                    contentDescription = "User profile picture"
                )
            }

            Text(text = post.username ?: "")
            Text(text = " -> ", modifier = Modifier.padding(8.dp))

            if (userData?.userId == post.userId) {
                // Current user's post, don't show anything
            } else if (userData?.following?.contains(post.userId) == true) {
                Text(
                    text = "Following",
                    color = Color.Gray,
                    modifier = Modifier.clickable { vm.onFollowClick(post.userId!!) })
            } else {
                Text(
                    text = "Follow",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable { vm.onFollowClick(post.userId!!) })
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp)
                .padding(vertical = 8.dp)
        ) {
            CommonImage(
                data = post.postImage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                vm.onLikePost(post)
                                isLikedAnimationVisible = true
                            }
                        )
                    },
                contentScale = ContentScale.Crop
            )

            LaunchedEffect(isLikedAnimationVisible) {
                if (isLikedAnimationVisible) {
                    scale.animateTo(
                        targetValue = 1.5f,
                        animationSpec = tween(durationMillis = 200)
                    )
                    scale.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 200)
                    )
                    isLikedAnimationVisible = false
                }
            }

            // Icono de like que aparece y desaparece
            if (isLikedAnimationVisible) {
                Image(
                    painter = painterResource(id = R.drawable.ic_fav),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    contentDescription = "Like animation",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(100.dp) // Tamaño del icono
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                            alpha = 0.5f
                        )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_fav),
                contentDescription = "user post image",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(Color.Red)
            )
            Text(
                text = " ${post.likes?.size ?: 0} likes",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = post.username ?: "", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = post.postDescription ?: "", modifier = Modifier.padding(start = 8.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "$nbComments comments",
                color = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.clickable {
                    post.postId?.let {
                        navController.navigate(DestinationScreen.CommentsScreen.createRoute(it))
                    }
                })
        }
    }
}
