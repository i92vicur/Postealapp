package com.androidcoursehogent.postealapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidcoursehogent.postealapp.auth.LoginScreen
import com.androidcoursehogent.postealapp.auth.ProfileScreen
import com.androidcoursehogent.postealapp.auth.SignupScreen
import com.androidcoursehogent.postealapp.data.PostData
import com.androidcoursehogent.postealapp.main.CommentsScreen
import com.androidcoursehogent.postealapp.main.FeedScreen
import com.androidcoursehogent.postealapp.main.MyPostsScreen
import com.androidcoursehogent.postealapp.main.NewPostScreen
import com.androidcoursehogent.postealapp.main.NotificationMessage
import com.androidcoursehogent.postealapp.main.SearchScreen
import com.androidcoursehogent.postealapp.main.SinglePostScreen
import com.androidcoursehogent.postealapp.ui.theme.PostealappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PostealappTheme {

                PostealallApp()

            }
        }
    }
}

sealed class DestinationScreen(val route: String) {
    //Quitar el data si no fufa
    data object Signup : DestinationScreen("signup")
    data object Login : DestinationScreen("login")
    data object Feed : DestinationScreen("feed")
    data object Search : DestinationScreen("search")
    data object MyPosts : DestinationScreen("myposts")
    data object Profile : DestinationScreen("profile")
    data object NewPost : DestinationScreen("newpost/{imageUri}") {
        fun createRoute(uri: String) = "newpost/$uri"
    }

    data object SinglePost : DestinationScreen("singlepost")
    data object CommentsScreen : DestinationScreen("comments/{postId}"){
        fun createRoute(postId: String) = "comments/$postId"
    }
}

@Composable
fun PostealallApp() {
    val vm = hiltViewModel<PostealappViewModel>()
    val navController = rememberNavController()

    val isDarkTheme by vm.isDarkTheme.observeAsState(false)

    NotificationMessage(vm = vm)

    PostealappTheme(darkTheme = isDarkTheme){

        NavHost(navController = navController, startDestination = DestinationScreen.Signup.route) {
            composable(DestinationScreen.Signup.route) {
                SignupScreen(navController = navController, vm = vm)
            }
            composable(DestinationScreen.Login.route) {
                LoginScreen(navController = navController, vm = vm)
            }
            composable(DestinationScreen.Feed.route) {
                FeedScreen(navController = navController, vm = vm)
            }
            composable(DestinationScreen.Search.route) {
                SearchScreen(navController = navController, vm = vm)
            }
            composable(DestinationScreen.MyPosts.route) {
                MyPostsScreen(navController = navController, vm = vm)
            }
            composable(DestinationScreen.Profile.route) {
                ProfileScreen(navController = navController, vm = vm)
            }
            composable(DestinationScreen.NewPost.route) { navBackStachEntry ->
                val imageUri = navBackStachEntry.arguments?.getString("imageUri")
                imageUri?.let {
                    NewPostScreen(navController = navController, vm = vm, encodedUri = it)
                }
            }
            composable(DestinationScreen.SinglePost.route) {
                val post = navController.previousBackStackEntry?.savedStateHandle?.get<PostData>("post")
                post?.let {
                    SinglePostScreen(navController = navController, post = it, vm = vm)
                }
            }

            composable(DestinationScreen.CommentsScreen.route){ navBackStackEntry ->
                val postId = navBackStackEntry.arguments?.getString("postId")
                postId?.let { CommentsScreen(navController = navController, vm = vm, postId = it) }
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PostealappTheme {

    }
}