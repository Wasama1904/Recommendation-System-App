package com.smesouthafrica.app.data

import androidx.room.*

@Dao
interface AppDao {
    @Query("SELECT * FROM articles") suspend fun getAllArticles(): List<Article>
    @Query("SELECT * FROM articles WHERE articleId = :id") suspend fun getArticle(id: Int): Article?
    @Query("SELECT * FROM categories") suspend fun getCategories(): List<Category>
    @Query("SELECT * FROM preferences WHERE userId = :uid") suspend fun getPrefs(uid: Int): List<Preference>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertPref(p: Preference)
    @Query("DELETE FROM preferences WHERE userId = :uid") suspend fun clearPrefs(uid: Int)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertLog(log: BehaviourLog)
    @Query("SELECT * FROM behaviour_logs WHERE userId = :uid ORDER BY timestamp DESC") suspend fun getLogs(uid: Int): List<BehaviourLog>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun saveArticle(s: SavedArticle)
    @Query("DELETE FROM saved_articles WHERE userId = :uid AND articleId = :aid") suspend fun unsave(uid: Int, aid: Int)
    @Query("SELECT * FROM saved_articles WHERE userId = :uid") suspend fun getSaved(uid: Int): List<SavedArticle>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertSearch(s: SearchLog)
    @Query("SELECT * FROM search_logs WHERE userId = :uid ORDER BY timestamp DESC") suspend fun getSearches(uid: Int): List<SearchLog>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertArticles(list: List<Article>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertCategories(list: List<Category>)
}

@Database(entities = [User::class, Category::class, Article::class, Preference::class, SavedArticle::class, BehaviourLog::class, SearchLog::class], version = 1)
abstract class AppDatabase: RoomDatabase() { abstract fun dao(): AppDao }

object SampleData {
    val categories = listOf(
        Category(1,"Funding"), Category(2,"Marketing"), Category(3,"Tax"),
        Category(4,"Technology"), Category(5,"Compliance"), Category(6,"Operations"),
        Category(7,"Finance"), Category(8,"Business Growth"), Category(9,"Starting a Business")
    )
    val articles = listOf(
        Article(1,"Funding options for small business owners in SA","Explore government and private funding.","Full content: South Africa offers SEFA, NEF, IDC funding... SEFA provides loans up to R5m for SMEs...",1,"Funding","💰","5 min"),
        Article(2,"How to promote your business on social media","Low-cost marketing strategies.","Full content: Use Instagram Reels, WhatsApp Business, and LinkedIn to reach clients...",2,"Marketing","📣","4 min"),
        Article(3,"Understanding SARS small business tax","Tax guide for 2025/2026.","Full content: Turnover tax vs small business corporation tax... Registration process...",3,"Tax","🧾","6 min"),
        Article(4,"Best tech tools for SMEs in 2026","Affordable SaaS tools.","Full content: Xero, Yoco, QuickBooks, Canva, and SME South Africa tools...",4,"Technology","💻","5 min"),
        Article(5,"POPIA compliance checklist for startups","Stay compliant.","Full content: POPIA requires consent, data mapping, and breach notification...",5,"Compliance","✅","7 min"),
        Article(6,"How to streamline daily operations","Operations management.","Full content: SOPs, checklists, and using Trello...",6,"Operations","⚙️","4 min"),
        Article(7,"How to manage your business cash flow","Finance essentials.","Full content: Cash flow = inflows - outflows. Track weekly...",7,"Finance","📊","5 min"),
        Article(8,"How to write a business plan that gets funding","Business growth.","Full content: Executive summary, market analysis, financial projections...",8,"Business Growth","📈","8 min"),
        Article(9,"Step-by-step: Register your business at CIPC","Starting a business.","Full content: Go to CIPC, reserve name, register PTY...",9,"Starting a Business","🚀","6 min"),
        Article(10,"SEFA vs NEF vs IDC: Which funding fits you?","Funding comparison.","Full content: SEFA for micro, NEF for black-owned, IDC for industrial...",1,"Funding","💸","5 min"),
        Article(11,"Email marketing for SMEs - R0 budget","Marketing guide.","Full content: Build list, use Mailchimp free tier, segment...",2,"Marketing","📧","4 min"),
        Article(12,"Cloud accounting: Xero vs QuickBooks","Technology review.","Full content: Xero cheaper locally, QuickBooks better for US clients...",4,"Technology","☁️","5 min")
    )
}
