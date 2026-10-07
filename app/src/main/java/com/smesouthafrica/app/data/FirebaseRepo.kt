package com.smesouthafrica.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Data Repository layer interfacing with Cloud Firestore and Firebase Authentication.
 * 
 * Manages remote data persistence, collection queries, user preferences,
 * and user interaction tracking logs (behaviour logs and search logs).
 */
class FirebaseRepo {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Seeds initial categories and articles into Cloud Firestore if collections are empty.
     * Uses kotlinx.coroutines.tasks.await for non-blocking asynchronous calls.
     */
    suspend fun seedIfEmpty() {
        val articles = db.collection("articles").get().await()
        if (articles.isEmpty) {
            SampleData.categories.forEach { cat ->
                db.collection("categories").document(cat.categoryId.toString()).set(mapOf("categoryId" to cat.categoryId, "name" to cat.name, "icon" to cat.icon)).await()
            }
            SampleData.articles.forEach { art ->
                db.collection("articles").document(art.articleId.toString()).set(art).await()
            }
        }
    }

    /** Returns currently authenticated user UID or a fallback demo identifier */
    fun getCurrentUserId(): String = auth.currentUser?.uid ?: "user_123_demo"
    
    /** Registers a new user with Firebase Authentication and creates a user record in Firestore */
    suspend fun register(email: String, pass: String, name: String): String {
        val result = auth.createUserWithEmailAndPassword(email, pass).await()
        val uid = result.user!!.uid
        db.collection("users").document(uid).set(mapOf("userId" to uid, "name" to name, "email" to email, "createdAt" to System.currentTimeMillis())).await()
        return uid
    }
    
    /** Authenticates existing user credentials via Firebase Auth */
    suspend fun login(email: String, pass: String): String {
        val result = auth.signInWithEmailAndPassword(email, pass).await()
        return result.user!!.uid
    }

    /** Fetches all articles from Firestore, falling back to local SampleData if offline */
    suspend fun getArticles(): List<Article> = try {
        db.collection("articles").orderBy("articleId").get().await().toObjects(Article::class.java)
    } catch (e: Exception) { SampleData.articles }

    /** Fetches all categories from Firestore */
    suspend fun getCategories(): List<Category> = try {
        db.collection("categories").get().await().toObjects(Category::class.java)
    } catch (e: Exception) { SampleData.categories }
    
    /** Retrieves user-selected category preferences */
    suspend fun getPreferences(userId: String): List<Preference> = 
        db.collection("preferences").whereEqualTo("userId", userId).get().await().toObjects(Preference::class.java)
    
    /** Updates user preferences by replacing existing records and logging a CATEGORY_SELECTED event */
    suspend fun savePreferences(userId: String, categories: List<Category>) {
        val old = db.collection("preferences").whereEqualTo("userId", userId).get().await()
        old.forEach { it.reference.delete() }
        categories.forEach { cat ->
            db.collection("preferences").add(Preference(userId, cat.categoryId, cat.name, 5))
        }
        db.collection("behaviourLogs").add(BehaviourLog(userId, 0, "CATEGORY_SELECTED"))
    }

    /** Retrieves behavior logs for implicit filtering calculation */
    suspend fun getBehaviourLogs(userId: String): List<BehaviourLog> = 
        db.collection("behaviourLogs").whereEqualTo("userId", userId).get().await().toObjects(BehaviourLog::class.java)

    /** Logs implicit user interaction events (READ, LIKE, DISLIKE, SAVE) */
    suspend fun logBehaviour(userId: String, articleId: Int, eventType: String, duration: Int = 0) {
        db.collection("behaviourLogs").add(BehaviourLog(userId, articleId, eventType, duration))
    }

    /** Retrieves past search query logs */
    suspend fun getSearchLogs(userId: String): List<SearchLog> = 
        db.collection("searchLogs").whereEqualTo("userId", userId).get().await().toObjects(SearchLog::class.java)

    /** Logs user search queries to train future content recommendations */
    suspend fun logSearch(userId: String, query: String) {
        db.collection("searchLogs").add(SearchLog(userId, query))
        db.collection("behaviourLogs").add(BehaviourLog(userId, 0, "SEARCH"))
    }

    /** Fetches set of article IDs bookmarked by user */
    suspend fun getSavedIds(userId: String): Set<Int> {
        val snap = db.collection("savedArticles").whereEqualTo("userId", userId).get().await()
        return snap.documents.mapNotNull { it.getLong("articleId")?.toInt() }.toSet()
    }

    /** Filters articles matching user's saved article IDs */
    suspend fun getSavedArticles(userId: String, allArticles: List<Article>): List<Article> {
        val ids = getSavedIds(userId)
        return allArticles.filter { it.articleId in ids }
    }

    /** Toggles saved state of an article and logs a SAVE interaction event */
    suspend fun toggleSave(userId: String, articleId: Int): Boolean {
        val existing = db.collection("savedArticles").whereEqualTo("userId", userId).whereEqualTo("articleId", articleId).get().await()
        return if (existing.isEmpty) {
            db.collection("savedArticles").add(SavedArticle(userId, articleId))
            db.collection("behaviourLogs").add(BehaviourLog(userId, articleId, "SAVE"))
            true
        } else {
            existing.documents.forEach { it.reference.delete() }
            false
        }
    }
}
