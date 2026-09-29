package com.smesouthafrica.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.smesouthafrica.app.data.AppDatabase
import com.smesouthafrica.app.data.SampleData
import com.smesouthafrica.app.ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "sme.db").allowMainThreadQueries().build()
        // Seed data - fulfills 10+ records requirement
        CoroutineScope(Dispatchers.IO).launch {
            if (db.dao().getAllArticles().isEmpty()) {
                db.dao().insertCategories(SampleData.categories)
                db.dao().insertArticles(SampleData.articles)
            }
        }
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF1E8E3E))) {
                val nav = rememberNavController()
                var userId by remember { mutableStateOf(1) } // simple single user for demo
                NavHost(navController = nav, startDestination = "splash") {
                    composable("splash") { SplashScreen { nav.navigate("login") } }
                    composable("login") { LoginScreen(onLogin = { nav.navigate("home") }, onRegister = { nav.navigate("register") }) }
                    composable("register") { RegisterScreen(onDone = { nav.navigate("interests") }) }
                    composable("interests") { InterestScreen(db = db, userId = userId, onDone = { nav.navigate("home") }) }
                    composable("home") { HomeScreen(db = db, userId = userId, nav = nav) }
                    composable("article/{id}") { backStack ->
                        val id = backStack.arguments?.getString("id")?.toIntOrNull() ?: 1
                        ArticleDetailScreen(db = db, userId = userId, articleId = id, nav = nav)
                    }
                    composable("search") { SearchScreen(db = db, userId = userId, nav = nav) }
                    composable("saved") { SavedScreen(db = db, userId = userId, nav = nav) }
                    composable("profile") { ProfileScreen(db = db, userId = userId, nav = nav) }
                }
            }
        }
    }
}
