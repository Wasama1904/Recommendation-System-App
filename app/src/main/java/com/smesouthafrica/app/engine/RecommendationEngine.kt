package com.smesouthafrica.app.engine

import com.smesouthafrica.app.data.*

object RecommendationEngine {
    fun scoreArticles(articles: List<Article>, prefs: List<Preference>, logs: List<BehaviourLog>, searches: List<SearchLog>, savedIds: Set<Int>): List<ScoredArticle> {
        return articles.map { article ->
            var score = 0
            val breakdown = mutableListOf<String>()
            if (prefs.any { it.categoryName == article.categoryName }) { score += 5; breakdown.add("Interest+5") }
            if (searches.any { it.query.contains(article.categoryName, ignoreCase = true) || article.title.contains(it.query, ignoreCase = true) }) { score += 4; breakdown.add("Search+4") }
            val readLogs = logs.filter { it.articleId == article.articleId && it.eventType == "READ" }
            if (readLogs.isNotEmpty()) { score += readLogs.size * 3; breakdown.add("Read x${readLogs.size}*3") }
            if (readLogs.any { it.duration > 60 }) { score += 4; breakdown.add("LongRead+4") }
            val prevCats = logs.mapNotNull { log -> articles.find { it.articleId == log.articleId }?.categoryName }.toSet()
            if (article.categoryName in prevCats) { score += 2; breakdown.add("SameCat+2") }
            if (article.articleId in savedIds) { score += 5; breakdown.add("Saved+5") }
            if (logs.any { it.articleId == article.articleId && it.eventType == "LIKE" }) { score += 5; breakdown.add("Liked+5") }
            ScoredArticle(article.copy(score = score), score)
        }.sortedByDescending { it.score }
    }
}
