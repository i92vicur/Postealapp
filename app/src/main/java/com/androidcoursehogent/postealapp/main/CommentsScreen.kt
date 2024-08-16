package com.androidcoursehogent.postealapp.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text2.input.TextFieldCharSequence
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidcoursehogent.postealapp.PostealappViewModel
import com.androidcoursehogent.postealapp.data.CommentData
import org.w3c.dom.Comment

@Composable
fun CommentsScreen(navController: NavController, vm: PostealappViewModel, postId: String) {

    var commentText by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val comments = vm.comments.value
    val commentsProgress = vm.commentsProgress.value

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween) {

        if (commentsProgress) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CommonProgressSpinner()
            }
        } else if (comments.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 300.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "No comments available")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items = comments) { comment ->
                    CommentRow(comment)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.LightGray),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        vm.createComment(postId = postId, text = commentText)
                        commentText = ""
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "Comment")
            }
        }
    }

}

@Composable
fun CommentRow(comment: CommentData) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
    ) {
        Text(text = comment.username ?: "", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = comment.text ?: "", modifier = Modifier.padding(start = 8.dp))
    }

}
