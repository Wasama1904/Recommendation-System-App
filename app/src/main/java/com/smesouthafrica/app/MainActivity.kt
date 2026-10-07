package com.smesouthafrica.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.smesouthafrica.app.data.FirebaseRepo
import com.smesouthafrica.app.ui.*
import com.smesouthafrica.app.ui.theme.SMESouthAfricaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Single-Activity Entry Point for SME South Africa App.
 *
 * Utilizes Jetpack Compose with a declarative NavHost component to handle screen-to-screen
 * navigation without requiring multiple Android Activities.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase services prior to rendering UI
        FirebaseApp.initializeApp(this)
        
        setContent {
            SMESouthAfricaTheme {
                val nav = rememberNavController()
                
                // Retain instance of Firebase Repository across recompositions
                val repo = remember { FirebaseRepo() }
                
                // Asynchronously seed sample business content on first launch off the main thread
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        repo.seedIfEmpty()
                    }
                }
                
                // Declarative Navigation Graph mapping route IDs to composable screens
                NavHost(navController = nav, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen { nav.navigate("login") { popUpTo("splash") { inclusive = true } } }
                    }
                    composable("login") {
                        LoginScreen(repo = repo, onLogin = { nav.navigate("home") { popUpTo("login") { inclusive = true } } }, onRegister = { nav.navigate("register") })
                    }
                    composable("register") {
                        RegisterScreen(repo = repo, onDone = { nav.navigate("interests") })
                    }
                    composable("interests") {
                        InterestScreen(repo = repo, onDone = { nav.navigate("home") { popUpTo("interests") { inclusive = true } } })
                    }
                    composable("home") {
                        HomeScreen(repo = repo, nav = nav)
                    }
                    composable("article/{id}") { backStack ->
                        val id = backStack.arguments?.getString("id")?.toIntOrNull() ?: 1
                        ArticleDetailScreen(repo = repo, articleId = id, nav = nav)
                    }
                    composable("search") {
                        SearchScreen(repo = repo, nav = nav)
                    }
                    composable("saved") {
                        SavedScreen(repo = repo, nav = nav)
                    }
                    composable("profile") {
                        ProfileScreen(repo = repo, nav = nav)
                    }
                }
            }
        }
    }
}
