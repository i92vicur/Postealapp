
package com.androidcoursehogent.postealapp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidcoursehogent.postealapp.ui.theme.PostealappTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationToFeedScreen() {
        composeTestRule.setContent {
            PostealappTheme {
                val navController = rememberNavController()
                MainActivity(navController = navController)
            }
        }

        // Assert that the feed screen is displayed
        composeTestRule.onNodeWithText("Feed").assertExists()
    }
}
