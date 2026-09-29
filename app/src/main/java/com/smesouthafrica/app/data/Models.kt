package com.smesouthafrica.app.data

import androidx.room.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val name: String,
    val email: String,
    val passwordHash: String
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val categoryId: Int,
    val name: String
)

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey val articleId: Int,
    val title: String,
    val description: String,
    val content: String,
    val categoryId: Int,
    val categoryName: String,
    val imageEmoji: String,
    val readTime: String
)

@Entity(tableName = "preferences")
data class Preference(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val categoryId: Int,
    val categoryName: String,
    val weight: Int = 5
)

@Entity(tableName = "saved_articles")
data class SavedArticle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val articleId: Int
)

@Entity(tableName = "behaviour_logs")
data class BehaviourLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val articleId: Int,
    val eventType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val duration: Int = 0
)

@Entity(tableName = "search_logs")
data class SearchLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ScoredArticle(
    val article: Article,
    val score: Int
)
