package com.smesouthafrica.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Data Repository layer interfacing with Cloud Firestore and Firebase Authentication.
 * 
 * Manages remote data persistence, collection queries, user preferences,
 * user session state, and interaction tracking logs.
 */
class FirebaseRepo {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        // Shared active session user state across repository instances
        private var localUser: AppUser = AppUser(userId = "demo", name = "Wasama Makolo", email = "wasama@email.com", role = "Entrepreneur")
    }

    /**
     * Seeds initial categories and articles into Cloud Firestore if collections are empty.
     * Uses kotlinx.coroutines.tasks.await for non-blocking asynchronous calls.
     */
    suspend fun seedIfEmpty() {
        try {
            val articles = db.collection("articles").get().await()
            if (articles.isEmpty) {
                SampleData.categories.forEach { cat ->
                    db.collection("categories").document(cat.categoryId.toString()).set(mapOf("categoryId" to cat.categoryId, "name" to cat.name, "icon" to cat.icon)).await()
                }
                SampleData.articles.forEach { art ->
                    db.collection("articles").document(art.articleId.toString()).set(art).await()
                }
            }
        } catch (_: Exception) {}
    }

    /** Returns currently authenticated user UID, local session UID, or a fallback identifier */
    fun getCurrentUserId(): String = auth.currentUser?.uid ?: localUser.userId

    /** Returns currently authenticated AppUser profile from Firebase Auth, Firestore, or local session */
    suspend fun getCurrentUser(): AppUser {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            try {
                val doc = db.collection("users").document(firebaseUser.uid).get().await()
                if (doc.exists()) {
                    val name = doc.getString("name") ?: firebaseUser.displayName ?: localUser.name
                    val email = doc.getString("email") ?: firebaseUser.email ?: localUser.email
                    val user = AppUser(userId = firebaseUser.uid, name = name, email = email, role = "Entrepreneur")
                    localUser = user
                    return user
                }
            } catch (_: Exception) {}
            
            if (localUser.email == firebaseUser.email && localUser.name != "Wasama Makolo" && localUser.name != "Guest User") {
                return localUser
            }
            
            val fallbackName = firebaseUser.displayName 
                ?: firebaseUser.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } 
                ?: localUser.name
            val fallbackEmail = firebaseUser.email ?: localUser.email
            val user = AppUser(userId = firebaseUser.uid, name = fallbackName, email = fallbackEmail, role = "Entrepreneur")
            localUser = user
            return user
        }
        
        return localUser
    }

    /** Signs out the current user from Firebase Auth and resets local session */
    suspend fun logout() {
        try {
            auth.signOut()
        } catch (_: Exception) {}
        localUser = AppUser(userId = "demo", name = "Guest User", email = "guest@smesouthafrica.co.za", role = "Entrepreneur")
    }

    /** Registers a new user with Firebase Authentication and creates a user record in Firestore */
    suspend fun register(email: String, pass: String, name: String): String {
        // Immediately set localUser so profile reflects the registered name instantly (offline/demo or online)
        val generatedUid = "user_${System.currentTimeMillis()}"
        localUser = AppUser(userId = generatedUid, name = name, email = email, role = "Entrepreneur")
        
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user!!.uid
            localUser = AppUser(userId = uid, name = name, email = email, role = "Entrepreneur")
            try {
                val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                result.user!!.updateProfile(profileUpdates).await()
                db.collection("users").document(uid).set(mapOf("userId" to uid, "name" to name, "email" to email, "createdAt" to System.currentTimeMillis(), "role" to "Entrepreneur")).await()
            } catch (_: Exception) {}
            uid
        } catch (_: Exception) {
            generatedUid
        }
    }

    /** Authenticates existing user credentials via Firebase Auth */
    suspend fun login(email: String, pass: String): String {
        val derivedName = email.substringBefore("@").split(".", "_", "-")
            .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
            .ifEmpty { "User" }
        localUser = AppUser(userId = "user_${email.hashCode()}", name = derivedName, email = email, role = "Entrepreneur")

        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = result.user!!.uid
            try {
                val doc = db.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    val name = doc.getString("name") ?: derivedName
                    localUser = AppUser(userId = uid, name = name, email = email, role = "Entrepreneur")
                }
            } catch (_: Exception) {}
            uid
        } catch (_: Exception) {
            localUser.userId
        }
    }

    /** Fetches all articles from Firestore, falling back to local SampleData if offline */
    suspend fun getArticles(): List<Article> = try {
        db.collection("articles").orderBy("articleId").get().await().toObjects(Article::class.java)
    } catch (_: Exception) { SampleData.articles }

    /** Fetches all categories from Firestore */
    suspend fun getCategories(): List<Category> = try {
        db.collection("categories").get().await().toObjects(Category::class.java)
    } catch (_: Exception) { SampleData.categories }
    
    /** Retrieves user-selected category preferences */
    suspend fun getPreferences(userId: String): List<Preference> = try {
        db.collection("preferences").whereEqualTo("userId", userId).get().await().toObjects(Preference::class.java)
    } catch (_: Exception) { emptyList() }
    
    /** Updates user preferences by replacing existing records and logging a CATEGORY_SELECTED event */
    suspend fun savePreferences(userId: String, categories: List<Category>) {
        try {
            val old = db.collection("preferences").whereEqualTo("userId", userId).get().await()
            old.forEach { it.reference.delete() }
            categories.forEach { cat ->
                db.collection("preferences").add(Preference(userId, cat.categoryId, cat.name, 5))
            }
            db.collection("behaviourLogs").add(BehaviourLog(userId, 0, "CATEGORY_SELECTED"))
        } catch (_: Exception) {}
    }

    /** Retrieves behavior logs for implicit filtering calculation */
    suspend fun getBehaviourLogs(userId: String): List<BehaviourLog> = try {
        db.collection("behaviourLogs").whereEqualTo("userId", userId).get().await().toObjects(BehaviourLog::class.java)
    } catch (_: Exception) { emptyList() }

    /** Logs implicit user interaction events (READ, LIKE, DISLIKE, SAVE) */
    suspend fun logBehaviour(userId: String, articleId: Int, eventType: String, duration: Int = 0) {
        try {
            db.collection("behaviourLogs").add(BehaviourLog(userId, articleId, eventType, duration))
        } catch (_: Exception) {}
    }

    /** Retrieves past search query logs */
    suspend fun getSearchLogs(userId: String): List<SearchLog> = try {
        db.collection("searchLogs").whereEqualTo("userId", userId).get().await().toObjects(SearchLog::class.java)
    } catch (_: Exception) { emptyList() }

    /** Logs user search queries to train future content recommendations */
    suspend fun logSearch(userId: String, query: String) {
        try {
            db.collection("searchLogs").add(SearchLog(userId, query))
            db.collection("behaviourLogs").add(BehaviourLog(userId, 0, "SEARCH"))
        } catch (_: Exception) {}
    }

    /** Fetches set of article IDs bookmarked by user */
    suspend fun getSavedIds(userId: String): Set<Int> = try {
        val snap = db.collection("savedArticles").whereEqualTo("userId", userId).get().await()
        snap.documents.mapNotNull { it.getLong("articleId")?.toInt() }.toSet()
    } catch (_: Exception) { emptySet() }

    /** Filters articles matching user's saved article IDs */
    suspend fun getSavedArticles(userId: String, allArticles: List<Article>): List<Article> {
        val ids = getSavedIds(userId)
        return allArticles.filter { it.articleId in ids }
    }

    /** Toggles saved state of an article and logs a SAVE interaction event */
    suspend fun toggleSave(userId: String, articleId: Int): Boolean = try {
        val existing = db.collection("savedArticles").whereEqualTo("userId", userId).whereEqualTo("articleId", articleId).get().await()
        if (existing.isEmpty) {
            db.collection("savedArticles").add(SavedArticle(userId, articleId))
            db.collection("behaviourLogs").add(BehaviourLog(userId, articleId, "SAVE"))
            true
        } else {
            existing.documents.forEach { it.reference.delete() }
            false
        }
    } catch (_: Exception) { false }
}
