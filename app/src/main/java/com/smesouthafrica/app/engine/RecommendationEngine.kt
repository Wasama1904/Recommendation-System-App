package com.smesouthafrica.app.engine

import com.smesouthafrica.app.data.*

/**
 * Recommendation Engine implementing a Hybrid Collaborative / Content-Based Filtering Algorithm.
 *
 * Multi-Factor Scoring Formula:
 * - Interest Preference Match: +5 points
 * - Search Query Match:         +4 points
 * - Read Interaction Event:     +3 points per read event
 * - Long Read Duration (>60s):  +4 points
 * - Same Category Recency:      +2 points
 * - Article Bookmarked (Saved): +5 points
 * - Article Liked (Upvoted):    +5 points
 */
object RecommendationEngine {

    /**
     * Calculates recommendation scores for a list of articles based on user interests,
     * interaction history, search queries, and saved bookmarks.
     *
     * @param articles List of candidate articles
     * @param prefs User's selected explicit category preferences
     * @param logs User's implicit interaction logs (READ, LIKE, SAVE)
     * @param searches User's search query history
     * @param savedIds Set of article IDs bookmarked by user
     * @return List of [ScoredArticle] objects sorted in descending order of recommendation score
     */
    fun scoreArticles(
        articles: List<Article>,
        prefs: List<Preference>,
        logs: List<BehaviourLog>,
        searches: List<SearchLog>,
        savedIds: Set<Int>
    ): List<ScoredArticle> {
        return articles.map { article ->
            var score = 0
            val breakdown = mutableListOf<String>()

            // 1. Explicit Category Preference Match (+5)
            if (prefs.any { it.categoryName == article.categoryName }) {
                score += 5
                breakdown.add("Interest+5")
            }

            // 2. Search Query Match (+4)
            if (searches.any { it.query.contains(article.categoryName, ignoreCase = true) || article.title.contains(it.query, ignoreCase = true) }) {
                score += 4
                breakdown.add("Search+4")
            }

            // 3. Read Frequency (+3 per read event)
            val readLogs = logs.filter { it.articleId == article.articleId && it.eventType == "READ" }
            if (readLogs.isNotEmpty()) {
                score += readLogs.size * 3
                breakdown.add("Read x${readLogs.size}*3")
            }

            // 4. Dwell Time / Deep Read Engagement (>60s) (+4)
            if (readLogs.any { it.duration > 60 }) {
                score += 4
                breakdown.add("LongRead+4")
            }

            // 5. Category Affinity / Cross-Category Recency (+2)
            val prevCats = logs.mapNotNull { log -> articles.find { it.articleId == log.articleId }?.categoryName }.toSet()
            if (article.categoryName in prevCats) {
                score += 2
                breakdown.add("SameCat+2")
            }

            // 6. Saved / Bookmarked Status (+5)
            if (article.articleId in savedIds) {
                score += 5
                breakdown.add("Saved+5")
            }

            // 7. Positive Feedback / Explicit Like (+5)
            if (logs.any { it.articleId == article.articleId && it.eventType == "LIKE" }) {
                score += 5
                breakdown.add("Liked+5")
            }

            ScoredArticle(article.copy(score = score), score)
        }.sortedByDescending { it.score }
    }
}
