package com.smesouthafrica.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private val SME_Green = Color(0xFF1E8E3E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(nav: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = SME_Green
                )
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFD4F0DA))) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "SME South Africa App - Help System",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "This app learns what you are interested in and recommends relevant business content.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            item {
                HelpSection(
                    "How does the recommendation work?",
                    "Score = Interest(+5) + Search(+4) + Read(+3) + Long Read(+4) + Same Category(+2) + Saved(+5) + Liked(+5).\n\nExample: You select Funding (+5), search 'funding' (+4), read Funding article 85s (+3+4) = Score 16 -> Shows on top."
                )
            }
            item {
                HelpSection(
                    "How to train your feed",
                    "1. Select interests in Profile > Edit Interests\n2. Search for topics you care about\n3. Read and save articles\n4. Tap refresh in Home\nYour feed will reorder automatically."
                )
            }
            item {
                HelpSection(
                    "Database info",
                    "Firestore Collections (mirrors MySQL tables):\n- articles: 12 docs\n- categories: 9 docs\n- preferences: your selected interests\n- behaviourLogs: CLICK, READ, LIKE, SAVE, SEARCH\n- searchLogs: your queries\n- savedArticles: your library\nAll in africa-south1 region."
                )
            }
            item {
                HelpSection(
                    "FAQ",
                    "Q: Feed not changing?\nA: Tap refresh icon top right after reading.\n\nQ: Score 0?\nA: No match with your behaviour yet. Read/search more.\n\nQ: How to change interests?\nA: Profile > Edit Interests"
                )
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("Contact & Support", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "For XISD6329 Task 2\nTeam: Wasama, Cameron, TinoTenda\nGitHub: Recommendation-System-App\nAzure: Same name\nFirebase: SME-Recommendation-System-App",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HelpSection(title: String, content: String) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Text(content, style = MaterialTheme.typography.bodySmall)
        }
    }
}
