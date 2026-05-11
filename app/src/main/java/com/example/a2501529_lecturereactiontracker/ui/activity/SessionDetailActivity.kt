// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.ui.activity

import android.content.Intent
import android.view.Menu
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a2501529_lecturereactiontracker.PremiumManager
import com.example.a2501529_lecturereactiontracker.data.database.SystemDatabase
import com.example.a2501529_lecturereactiontracker.data.entity.Reaction
import com.example.a2501529_lecturereactiontracker.repository.LectureRepo
import com.example.a2501529_lecturereactiontracker.ui.adapter.ReactionAdapter
import com.example.a2501529_lecturereactiontracker.viewmodel.SessionDetailViewModel
import com.example.a2501529_lecturereactiontracker.viewmodel.ViewModelFactory
import com.example.a2501529_lecturereactiontracker.R
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SessionDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: SessionDetailViewModel
    private lateinit var rvReactions: RecyclerView
    private lateinit var adapter: ReactionAdapter
    private var sessionId: Long = -1L

    // To store data for sharing feature
    private var currentReactions: List<Reaction> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_detail)

        // Toolbar setup
        val toolbar = findViewById<MaterialToolbar>(R.id.sessionDetailToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get extras
        val courseName = intent.getStringExtra("course_name") ?: "Session"
        sessionId = intent.getLongExtra("session_id", -1)
        val sessionStart = intent.getLongExtra("session_start", 0L)

        if (sessionId == -1L) { finish(); return }

        supportActionBar?.title = "$courseName – ${formatSessionDate(sessionStart)}"

        // Setup ViewModel & DB
        val db = SystemDatabase.getDatabase(this)
        val repo = LectureRepo(db.courseDao(), db.sessionDao(), db.reactionDao())
        val factory = ViewModelFactory(repo)
        viewModel = factory.create(SessionDetailViewModel::class.java)

        // Setup RecyclerView
        rvReactions = findViewById(R.id.rvReactions)
        adapter = ReactionAdapter()
        rvReactions.layoutManager = LinearLayoutManager(this)
        rvReactions.adapter = adapter


        // Fake ad
        val bannerContainer = findViewById<View>(R.id.fakeBannerContainer)
        if (PremiumManager.isPremium(this)) {
            bannerContainer.visibility = View.GONE // Hide ad for premium users
        } else {
            bannerContainer.visibility = View.VISIBLE // Show ad for free users
        }

        // Observe Data
        lifecycleScope.launch {
            viewModel.getReactions(sessionId).collect { list ->
                currentReactions = list
                adapter.submitList(list)

                // empty state check
                val txtEmpty = findViewById<android.widget.TextView>(R.id.txtEmptyReactions)
                if (list.isEmpty()) {
                    txtEmpty.visibility = android.view.View.VISIBLE
                    rvReactions.visibility = android.view.View.GONE
                } else {
                    txtEmpty.visibility = android.view.View.GONE
                    rvReactions.visibility = android.view.View.VISIBLE
                }
            }
        }
    }

    // Create Options Menu (Share Icon)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_session_detail, menu)
        return true
    }

    // Handle Menu Clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                shareSessionReport()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // This function is for the share session report with whatsapp or e-mail
    private fun shareSessionReport() {
        if (currentReactions.isEmpty()) {
            Toast.makeText(this, "Nothing to share yet!", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate statistics
        val total = currentReactions.size
        val understood = currentReactions.count { it.reactionType.equals("understood", ignoreCase = true) }
        val confused = currentReactions.count { it.reactionType.equals("confused", ignoreCase = true) }
        val lost = currentReactions.count { it.reactionType.equals("lost", ignoreCase = true) }

        // Simple score calculation
        val score = if (total > 0) (understood * 100) / total else 0

        val courseName = supportActionBar?.title ?: "My Lesson"

        // Build the message
        val shareBody = """
            Lecture Report: $courseName
            
            Understanding Score: %$score
            
            🙂 Understood: $understood
            😕 Confused: $confused
            😐 Lost: $lost
            
            Tracked via Lecture Reaction Tracker App
        """.trimIndent()

        // Create Implicit Intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "My Lecture Stats")
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }

        startActivity(Intent.createChooser(shareIntent, "Share Report via"))
    }

    private fun formatSessionDate(timestamp: Long): String {
        if (timestamp == 0L) return "Unknown"
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}