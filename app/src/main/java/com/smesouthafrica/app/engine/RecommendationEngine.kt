package com.smesouthafrica.app.engine

import com.smesouthafrica.app.data.Article
import com.smesouthafrica.app.data.BehaviourLog
import com.smesouthafrica.app.data.Preference
import com.smesouthafrica.app.data.SearchLog
import com.smesouthafrica.app.data.ScoredArticle

object RecommendationEngine {
    fun scoreArticles(
        articles: List<Article>,
        prefs: List<Preference>,
        logs: List<BehaviourLog>,
        searches: List<SearchLog>,
        savedIds: Set<Int>
    ): List<ScoredArticle> {
        return articles.map { article ->
            var score = 0
            // Interest Match +5
            if (prefs.any { it.categoryName == article.categoryName }) score += 5
            // Search Match +4
            if (searches.any { it.query.contains(article.categoryName, ignoreCase = true) || article.title.contains(it.query, ignoreCase = true) }) score += 4
            // Reading History +3 per read, +4 if long read
            val readLogs = logs.filter { it.articleId == article.articleId && it.eventType == "READ" }
            score += readLogs.size * 3
            if (readLogs.any { it.duration > 60 }) score += 4
            // Same category as previous reads +2
            val prevCats = logs.mapNotNull { log -> articles.find { it.articleId == log.articleId }?.categoryName }.toSet()
            if (article.categoryName in prevCats) score += 2
            // Saved +5, Liked +5
            if (article.articleId in savedIds) score += 5
            if (logs.any { it.articleId == article.articleId && it.eventType == "LIKE" }) score += 5

            ScoredArticle(article, score)
        }.sortedByDescending { it.score }
    }
}
