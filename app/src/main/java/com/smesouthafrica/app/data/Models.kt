package com.smesouthafrica.app.data

/**
 * Core Data Transfer Objects (DTOs) for the Recommendation System.
 * 
 * These models directly map to Cloud Firestore document collections:
 * - Articles -> 'articles' collection
 * - Categories -> 'categories' collection
 * - Preferences -> 'preferences' collection (user-selected interests)
 * - BehaviourLog -> 'behaviourLogs' collection (implicit interaction tracking)
 * - SearchLog -> 'searchLogs' collection (user query tracking)
 * - SavedArticle -> 'savedArticles' collection (bookmarks)
 */

data class Article(
    val articleId: Int = 0,
    val title: String = "",
    val description: String = "",
    val content: String = "",
    val categoryId: Int = 0,
    val categoryName: String = "",
    val imageEmoji: String = "📄",
    val readTime: String = "5 min",
    val author: String = "SME South Africa",
    val score: Int = 0 // Dynamic recommendation score computed client-side
)

data class Category(
    val categoryId: Int = 0,
    val name: String = "",
    val icon: String = "📁"
)

data class Preference(
    val userId: String = "",
    val categoryId: Int = 0,
    val categoryName: String = "",
    val weight: Int = 5 // User interest weight used in scoring
)

data class BehaviourLog(
    val userId: String = "",
    val articleId: Int = 0,
    val eventType: String = "", // e.g. "READ", "LIKE", "SAVE", "CATEGORY_SELECTED"
    val duration: Int = 0, // Reading time in seconds
    val timestamp: Long = System.currentTimeMillis()
)

data class SearchLog(
    val userId: String = "",
    val query: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class SavedArticle(
    val userId: String = "",
    val articleId: Int = 0
)

/**
 * Wrapper object linking an [Article] with its calculated recommendation score.
 */
data class ScoredArticle(
    val article: Article,
    val score: Int
)

/**
 * Pre-seeded sample dataset ensuring 10+ records across categories and articles
 * as required by the project specifications.
 */
object SampleData {
    val categories = listOf(
        Category(1,"Funding","💰"), Category(2,"Marketing","📣"), Category(3,"Tax","🧾"),
        Category(4,"Technology","💻"), Category(5,"Compliance","✅"), Category(6,"Operations","⚙️"),
        Category(7,"Finance","📊"), Category(8,"Business Growth","📈"), Category(9,"Starting a Business","🚀")
    )
    val articles = listOf(
        Article(1,"Funding options for small business owners in SA","Explore SEFA, NEF, IDC and private funding options available to South African SMEs.","SEFA offers loans from R50k to R5m for SMEs with repayment 6-60 months. NEF focuses on 51% black-owned businesses with R250k-R75m. IDC funds industrial projects R1m-R1bn. Requirements: Business plan, 6-month bank statements, CIPC docs, ID, proof of address. Interest 8-12%. Apply via their websites or SEDA offices. Tip: Prepare cash flow forecast.",1,"Funding","💰","5 min","SME South Africa"),
        Article(2,"How to promote your business on social media in 2026","Low-cost marketing strategies that work with R0 budget.","Post 3x Reels per week between 6-8pm when township entrepreneurs are online. Use WhatsApp Business catalog to showcase products. LinkedIn articles build B2B credibility. Content ideas: behind-the-scenes, customer testimonials, tips. Use Canva free templates. Track engagement rate >3%.",2,"Marketing","📣","4 min","Marketing Team"),
        Article(3,"Understanding SARS small business tax - 2025/2026 guide","Turnover tax vs Small Business Corporation tax explained.","Turnover tax: 0% for first R335k turnover, then sliding 1-3%. SBC tax: 0% first R95k profit, 7% up to R365k, 21% up to R550k. Register via eFiling. Deadlines: 31 Oct for provisional. Keep invoices 5 years. Use Xero to auto-calculate. Consult tax practitioner for R500.",3,"Tax","🧾","6 min","SARS Expert"),
        Article(4,"Best tech tools for SMEs in 2026 under R500/month","Affordable SaaS tools every South African SME needs.","Yoco card machine R0/month + 2.95% per transaction. Xero accounting R350/mo with SARS eFiling integration. Canva Pro R149/mo for designs. QuickBooks R250/mo. Google Workspace R115/user. SME South Africa resource library free. All have mobile apps. Integrate via Zapier.",4,"Technology","💻","5 min","Tech Writer"),
        Article(5,"POPIA compliance checklist for startups","Stay compliant and avoid R10m fines.","POPIA requires: 1) Consent forms for data collection 2) Data mapping register 3) PAIA manual on website 4) Appoint Information Officer 5) Breach notification within 72hrs. Template available on SME SA. Penalty up to R10m or 10 years imprisonment. Action: Audit what personal info you collect.",5,"Compliance","✅","7 min","Legal Team"),
        Article(6,"How to streamline daily operations","Save 10 hours per week with SOPs.","Create Standard Operating Procedures for every repeat task (e.g., how to onboard client). Use Trello boards: To Do, Doing, Done. Daily 15-min standup with team. Measure time per task. Automate invoicing with Xero. Result: Less mistakes, can hire easier.",6,"Operations","⚙️","4 min","Operations Coach"),
        Article(7,"How to manage your business cash flow","Finance essentials every owner must know.","Cash flow = cash in - cash out. Track weekly in simple sheet. Rule: Keep 3 months expenses in separate account. Invoice within 24hrs, offer 5% early payment discount. Negotiate 30-day terms with suppliers but collect in 7 days. Use Yoco Capital for bridging.",7,"Finance","📊","5 min","Finance Expert"),
        Article(8,"How to write a business plan that gets funding","Investor-ready plan template.","Sections: 1) Executive summary (1 page compelling) 2) Market analysis with competitors 3) Marketing plan 4) Operations 5) 3-year financial projections (income, cash flow, balance). Use SME South Africa free template. Length 15-20 pages. Include photos. Have accountant review.",8,"Business Growth","📈","8 min","Funding Advisor"),
        Article(9,"Step-by-step: Register your business at CIPC in 5 days","Starting a business legal guide.","Step 1: Reserve name on CIPC website R50 (3 names). Step 2: Register Pty Ltd R175 - needs ID, address, 1 director. Step 3: Get tax number via eFiling. Step 4: Open business bank account. Step 5: Register for UIF if hiring. Total cost R225, time 5 days. Do it online, no lawyer needed.",9,"Starting a Business","🚀","6 min","CIPC Guide"),
        Article(10,"SEFA vs NEF vs IDC: Which funding fits your business?","Funding comparison with interest rates and requirements.","SEFA: R50k-R5m, any sector, any owner, needs 6 months trading. NEF: R250k-R75m, 51% black-owned, job creation focus. IDC: R1m-R1bn, manufacturing, green industries. SEFA fastest (2 weeks), IDC slowest (3 months). All require business plan and financials.",1,"Funding","💸","5 min","Funding Team"),
        Article(11,"Email marketing for SMEs - R0 budget to first 1000 subscribers","Build list without spending.","Collect emails via Yoco, website pop-up, WhatsApp. Use Mailchimp free up to 500 contacts. Send 1x weekly value email: 1 tip + 1 story + 1 offer. Subject line: question or benefit. Target open rate 25%, click 3%. Segment: customers vs leads. Never buy lists - POPIA violation.",2,"Marketing","📧","4 min","Email Expert"),
        Article(12,"Cloud accounting: Xero vs QuickBooks for SA SMEs","Honest review for 2026.","Xero: R350/mo, great local support, automatic SARS eFiling VAT201, bank feeds for all SA banks. QuickBooks: $15/mo (~R285), better if you have US clients, slightly better reports. Both have mobile apps for invoicing on the go. Winner for SA: Xero for compliance. Try free 30 days.",4,"Technology","☁️","5 min","Accounting Review")
    )
}
