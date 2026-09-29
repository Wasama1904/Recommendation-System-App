package com.smesouthafrica.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smesouthafrica.app.data.*
import com.smesouthafrica.app.engine.RecommendationEngine
import kotlinx.coroutines.*

@Composable fun SplashScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) { delay(1500); onFinish() }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("SME", style = MaterialTheme.typography.displayLarge)
            Text("SOUTH AFRICA", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp)); Text("Business made easier.")
        }
    }
}

@Composable fun LoginScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }; var pass by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Welcome Back", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) { Text("Login") }
        TextButton(onClick = onRegister) { Text("Don't have an account? Create Account") }
    }
}

@Composable fun RegisterScreen(onDone: () -> Unit) {
    var name by remember { mutableStateOf("") }; var email by remember { mutableStateOf("") }; var pass by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp)); Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Create Account") }
    }
}

@Composable fun InterestScreen(db: AppDatabase, userId: Int, onDone: () -> Unit) {
    val categories = SampleData.categories
    val selected = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("What are you interested in?", style = MaterialTheme.typography.headlineSmall)
        Text("Select all that apply - this powers your recommendations")
        Spacer(Modifier.height(16.dp))
        categories.forEach { cat ->
            Row(Modifier.fillMaxWidth().clickable {
                if (cat.name in selected) selected.remove(cat.name) else selected.add(cat.name)
            }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = cat.name in selected, onCheckedChange = {
                    if (it) selected.add(cat.name) else selected.remove(cat.name)
                }); Text(cat.name)
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch(Dispatchers.IO) {
                db.dao().clearPrefs(userId)
                selected.forEach { name ->
                    val c = categories.find { it.name == name }!!
                    db.dao().insertPref(Preference(userId = userId, categoryId = c.categoryId, categoryName = c.name))
                    db.dao().insertLog(BehaviourLog(userId = userId, articleId = 0, eventType = "CATEGORY_SELECTED"))
                }
            }; onDone()
        }, modifier = Modifier.fillMaxWidth()) { Text("Continue -> Personalised Feed") }
    }
}

@Composable fun HomeScreen(db: AppDatabase, userId: Int, nav: NavController) {
    var articles by remember { mutableStateOf<List<ScoredArticle>>(emptyList()) }
    var savedIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    LaunchedEffect(Unit) {
        val all = db.dao().getAllArticles()
        val prefs = db.dao().getPrefs(userId)
        val logs = db.dao().getLogs(userId)
        val searches = db.dao().getSearches(userId)
        val saved = db.dao().getSaved(userId).map { it.articleId }.toSet()
        savedIds = saved
        articles = RecommendationEngine.scoreArticles(all, prefs, logs, searches, saved)
    }
    Scaffold(bottomBar = { BottomBar(nav) }) { pad ->
        LazyColumn(Modifier.padding(pad).padding(16.dp)) {
            item {
                Text("Good morning 👋", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("🔍 Search business content") }, modifier = Modifier.fillMaxWidth().clickable { nav.navigate("search") }, enabled = false)
                Spacer(Modifier.height(16.dp))
                Text("Recommended for You", style = MaterialTheme.typography.titleMedium)
                Text("Based on your interests + behaviour", style = MaterialTheme.typography.bodySmall)
            }
            items(articles) { scored ->
                Card(Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {
                    CoroutineScope(Dispatchers.IO).launch { db.dao().insertLog(BehaviourLog(userId = userId, articleId = scored.article.articleId, eventType = "CLICK")) }
                    nav.navigate("article/${scored.article.articleId}")
                }) {
                    Column(Modifier.padding(16.dp)) {
                        Text("${scored.article.imageEmoji} ${scored.article.categoryName} • Score: ${scored.score} • ${scored.article.readTime}")
                        Text(scored.article.title, style = MaterialTheme.typography.titleMedium)
                        Text(scored.article.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable fun ArticleDetailScreen(db: AppDatabase, userId: Int, articleId: Int, nav: NavController) {
    var article by remember { mutableStateOf<Article?>(null) }
    var isSaved by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(articleId) {
        article = db.dao().getArticle(articleId)
        isSaved = db.dao().getSaved(userId).any { it.articleId == articleId }
        scope.launch(Dispatchers.IO) {
            db.dao().insertLog(BehaviourLog(userId = userId, articleId = articleId, eventType = "READ", duration = 85))
        }
    }
    Scaffold(bottomBar = { BottomBar(nav) }) { pad ->
        article?.let { a ->
            LazyColumn(Modifier.padding(pad).padding(16.dp)) {
                item {
                    Text("← Back", modifier = Modifier.clickable { nav.popBackStack() })
                    Spacer(Modifier.height(8.dp))
                    Text(a.categoryName, color = Color(0xFF1E8E3E))
                    Text(a.title, style = MaterialTheme.typography.headlineSmall)
                    Text("${a.readTime} • SME South Africa", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(16.dp))
                    Text(a.content, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    Row {
                        Button(onClick = {
                            scope.launch(Dispatchers.IO) { db.dao().insertLog(BehaviourLog(userId = userId, articleId = a.articleId, eventType = "LIKE")) }
                        }) { Text("👍 Useful") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            scope.launch(Dispatchers.IO) {
                                if (isSaved) db.dao().unsave(userId, a.articleId) else db.dao().saveArticle(SavedArticle(userId = userId, articleId = a.articleId))
                            }; isSaved = !isSaved
                        }) { Text(if (isSaved) "★ Saved" else "♡ Save") }
                    }
                    Spacer(Modifier.height(16.dp)); Text("You may also like", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable fun SearchScreen(db: AppDatabase, userId: Int, nav: NavController) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(SampleData.articles) }
    val scope = rememberCoroutineScope()
    Scaffold(bottomBar = { BottomBar(nav) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            OutlinedTextField(value = query, onValueChange = {
                query = it
                results = if (it.isBlank()) SampleData.articles else SampleData.articles.filter { a -> a.title.contains(it, true) || a.categoryName.contains(it, true) }
            }, label = { Text("🔍 Search articles") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    db.dao().insertSearch(SearchLog(userId = userId, query = query))
                    db.dao().insertLog(BehaviourLog(userId = userId, articleId = 0, eventType = "SEARCH"))
                }
            }) { Text("Search - this trains recommendations") }
            LazyColumn {
                items(results) { art ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { nav.navigate("article/${art.articleId}") }) {
                        Column(Modifier.padding(12.dp)) { Text("${art.imageEmoji} ${art.categoryName}"); Text(art.title) }
                    }
                }
            }
        }
    }
}

@Composable fun SavedScreen(db: AppDatabase, userId: Int, nav: NavController) {
    var savedArticles by remember { mutableStateOf<List<Article>>(emptyList()) }
    LaunchedEffect(Unit) {
        val savedIds = db.dao().getSaved(userId).map { it.articleId }
        savedArticles = db.dao().getAllArticles().filter { it.articleId in savedIds }
    }
    Scaffold(bottomBar = { BottomBar(nav) }) { pad ->
        LazyColumn(Modifier.padding(pad).padding(16.dp)) {
            item { Text("Saved Articles", style = MaterialTheme.typography.headlineSmall) }
            items(savedArticles) { art ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { nav.navigate("article/${art.articleId}") }) {
                    Column(Modifier.padding(12.dp)) { Text(art.title) }
                }
            }
        }
    }
}

@Composable fun ProfileScreen(db: AppDatabase, userId: Int, nav: NavController) {
    var prefs by remember { mutableStateOf<List<Preference>>(emptyList()) }
    LaunchedEffect(Unit) { prefs = db.dao().getPrefs(userId) }
    Scaffold(bottomBar = { BottomBar(nav) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Text("Profile", style = MaterialTheme.typography.headlineSmall)
            Text("Wasama Makolo\nwasama@email.com")
            Spacer(Modifier.height(16.dp))
            Text("My Interests")
            prefs.forEach { Text("✓ ${it.categoryName}") }
            Spacer(Modifier.height(16.dp))
            Button(onClick = { nav.navigate("interests") }) { Text("Edit Interests") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { nav.navigate("login") }) { Text("Logout") }
            Spacer(Modifier.height(24.dp))
            Text("How recommendations work:", style = MaterialTheme.typography.titleSmall)
            Text("Score = Interest(+5) + Search(+4) + Read(+3) + Saved(+5) + SameCat(+2)", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable fun BottomBar(nav: NavController) {
    NavigationBar {
        NavigationBarItem(icon = { Text("🏠") }, label = { Text("Home") }, selected = false, onClick = { nav.navigate("home") })
        NavigationBarItem(icon = { Text("🔍") }, label = { Text("Explore") }, selected = false, onClick = { nav.navigate("search") })
        NavigationBarItem(icon = { Text("♡") }, label = { Text("Saved") }, selected = false, onClick = { nav.navigate("saved") })
        NavigationBarItem(icon = { Text("👤") }, label = { Text("Profile") }, selected = false, onClick = { nav.navigate("profile") })
    }
}
