// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.ui.activity

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a2501529_lecturereactiontracker.R
import com.example.a2501529_lecturereactiontracker.ui.activity.SessionDetailActivity
import com.example.a2501529_lecturereactiontracker.ui.adapter.SessionAdapter
import com.example.a2501529_lecturereactiontracker.data.database.SystemDatabase
import com.example.a2501529_lecturereactiontracker.repository.LectureRepo
import com.example.a2501529_lecturereactiontracker.viewmodel.DetailsViewModel
import com.example.a2501529_lecturereactiontracker.viewmodel.ViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var adapter: SessionAdapter
    private var courseId: Long = -1L

    // Swipe delete visuals
    private lateinit var deleteIcon: Drawable
    private val swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#D32F2F"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Load delete icon
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete)!!
        deleteIcon.setTint(Color.WHITE)

        // Basic UI elements
        val detailsToolbar = findViewById<MaterialToolbar>(R.id.detailsToolbar)
        val buttonStartSession = findViewById<MaterialButton>(R.id.buttonStartSession)
        val rvSessions = findViewById<RecyclerView>(R.id.rvSessions)

        setSupportActionBar(detailsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val courseName = intent.getStringExtra("course_name") ?: "Details"
        supportActionBar?.title = courseName

        courseId = intent.getLongExtra("course_id", -1)
        if (courseId == -1L) { finish(); return }

        // Build ViewModel with repository
        val db = SystemDatabase.Companion.getDatabase(this)
        val repository = LectureRepo(db.courseDao(), db.sessionDao(), db.reactionDao())
        val factory = ViewModelFactory(repository)
        viewModel = factory.create(DetailsViewModel::class.java)

        // Session list adapter
        adapter = SessionAdapter { session ->
            val intent = Intent(this, SessionDetailActivity::class.java)
            intent.putExtra("session_id", session.sessionId)
            intent.putExtra("course_name", courseName)
            intent.putExtra("session_start", session.startedTimestamp)
            startActivity(intent)
        }

        rvSessions.layoutManager = LinearLayoutManager(this)
        rvSessions.adapter = adapter

        // Swipe handler with red background
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            // Called when swipe finished
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val sessionToDelete = adapter.currentList[position]

                // Delete from database
                viewModel.deleteSession(sessionToDelete)

                // Show undo snackbar
                Snackbar.make(rvSessions, "Session deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        // Restore session on undo
                        lifecycleScope.launch {
                            viewModel.restoreSession(sessionToDelete)
                        }
                    }
                    .show()
            }

            // Draw background and icon
            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) { // Swipe right
                    swipeBackground.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(
                        itemView.left + iconMargin,
                        itemView.top + iconMargin,
                        itemView.left + iconMargin + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMargin
                    )
                } else { // Swipe left
                    swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(
                        itemView.right - iconMargin - deleteIcon.intrinsicWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.bottom - iconMargin
                    )
                }

                swipeBackground.draw(c)
                c.save()
                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                else
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                deleteIcon.draw(c)
                c.restore()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(rvSessions)

        // Observe session list for course
        lifecycleScope.launch {
            viewModel.getSessions(courseId).collect { sessions ->
                adapter.submitList(sessions)
            }
        }

        // Start new session button
        buttonStartSession.setOnClickListener {
            lifecycleScope.launch {
                val sessionId = viewModel.startSession(courseId)
                val intent = Intent(this@DetailsActivity, TrackingActivity::class.java)
                intent.putExtra("session_id", sessionId)
                intent.putExtra("course_name", courseName)
                startActivity(intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
