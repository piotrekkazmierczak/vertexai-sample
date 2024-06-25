package org.dune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.dune.feature.chat.ChatRoute
import org.dune.feature.functionscalling.FunctionsChatRoute
import org.dune.feature.photo.PhotoReasoningRoute
import org.dune.feature.text.SummarizeRoute
import org.dune.ui.ButlerianTheme

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ButlerianTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "menu") {
                        composable("menu") {
                            MenuScreen(onItemClicked = { routeId ->
                                navController.navigate(routeId)
                            })
                        }
                        composable("summarize") {
                            SummarizeRoute()
                        }
                        composable("photo_reasoning") {
                            PhotoReasoningRoute()
                        }
                        composable("chat") {
                            ChatRoute()
                        }
                        composable("functions_chat") {
                            FunctionsChatRoute()
                        }
                    }
                }
            }
        }
    }
}