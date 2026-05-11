// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.a2501529_lecturereactiontracker.R
import com.example.a2501529_lecturereactiontracker.data.database.SystemDatabase
import com.example.a2501529_lecturereactiontracker.repository.LectureRepo
import com.example.a2501529_lecturereactiontracker.viewmodel.TrackingViewModel
import com.example.a2501529_lecturereactiontracker.viewmodel.ViewModelFactory
import com.example.a2501529_lecturereactiontracker.PremiumManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class TrackingActivity : AppCompatActivity() {

    private lateinit var viewModel: TrackingViewModel
    private lateinit var chronometer: Chronometer
    private var sessionId: Long = -1L
    private var isRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        // Get session id from intent
        sessionId = intent.getLongExtra("session_id", -1)
        if (sessionId == -1L) {
            finish()
            return
        }

        // Setup top toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.trackingToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            showExitDialog()
        }

        // Handle system back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })

        val courseName = intent.getStringExtra("course_name") ?: "Tracking"
        supportActionBar?.title = "$courseName – Tracking"

        // Build database and viewmodel
        val db = SystemDatabase.getDatabase(this)
        val repo = LectureRepo(db.courseDao(), db.sessionDao(), db.reactionDao())
        val factory = ViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(TrackingViewModel::class.java)

        viewModel.sessionId = sessionId

        // Bind UI elements
        chronometer = findViewById(R.id.trackingChronometer)
        val btnUnderstood = findViewById<MaterialButton>(R.id.buttonUnderstood)
        val btnConfused = findViewById<MaterialButton>(R.id.buttonConfused)
        val btnLost = findViewById<MaterialButton>(R.id.buttonLost)
        val btnFinish = findViewById<MaterialButton>(R.id.buttonFinishSessionTracking)

        // Setup chronometer state
        if (viewModel.chronometerBaseTime == 0L) {
            viewModel.chronometerBaseTime = SystemClock.elapsedRealtime()
            viewModel.chronometerRunning = true
        }
        chronometer.base = viewModel.chronometerBaseTime

        if (viewModel.chronometerRunning) {
            chronometer.start()
            isRunning = true
        } else {
            chronometer.stop()
            isRunning = false
        }

        // Reaction buttons click
        btnUnderstood.setOnClickListener { saveReaction("understood", null) }
        btnConfused.setOnClickListener { showNoteDialog("confused") }
        btnLost.setOnClickListener { showNoteDialog("lost") }

        // Finish session button
        btnFinish.setOnClickListener {
            finishAndSaveSession()
        }
    }

    override fun onPause() {
        super.onPause()
        // Save current timer state
        viewModel.chronometerBaseTime = chronometer.base
        viewModel.chronometerRunning = isRunning
    }

    // Save single reaction event
    private fun saveReaction(type: String, note: String?) {
        val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base
        lifecycleScope.launch {
            viewModel.saveReaction(elapsedMillis, type, note)
        }
    }

    private fun showNoteDialog(type: String) {
        if (isFinishing || isDestroyed) return

        val input = EditText(this@TrackingActivity)
        input.hint = "Optional note..."

        // Track which button pressed
        var isButtonClicked = false

        val dialog = AlertDialog.Builder(this@TrackingActivity)
            .setTitle("Add Note")
            .setView(input)
            // Save note if provided
            .setPositiveButton("Save Note") { _, _ ->
                isButtonClicked = true
                val note = input.text.toString().trim()
                saveReaction(type, if (note.isNotEmpty()) note else null)
            }
            // Ignore text and save empty
            .setNegativeButton("Cancel") { _, _ ->
                isButtonClicked = true
                saveReaction(type, null)
            }
            .create()

        // Auto-save text on dismiss
        dialog.setOnDismissListener {
            if (!isButtonClicked) {
                val note = input.text.toString().trim()
                saveReaction(type, if (note.isNotEmpty()) note else null)
            }
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    // Finish session and persist
    private fun finishAndSaveSession() {
        lifecycleScope.launch {
            val endTime = System.currentTimeMillis()
            viewModel.finishSession(endTime)

            // Show fake ad for free users
            if (!PremiumManager.isPremium(this@TrackingActivity)) {
                val adIntent = Intent(this@TrackingActivity, FakeAdActivity::class.java)
                startActivity(adIntent)
            }
            finish()
        }
    }

    // Confirm leaving current session
    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Finish session?")
            .setMessage("Do you want to finish or discard this session?")
            .setPositiveButton("Finish & Save") { _, _ ->
                finishAndSaveSession()
            }
            .setNegativeButton("Discard") { _, _ ->
                lifecycleScope.launch {
                    viewModel.discardSession()

                    // Show ad also when discarding
                    if (!PremiumManager.isPremium(this@TrackingActivity)) {
                        val adIntent = Intent(this@TrackingActivity, FakeAdActivity::class.java)
                        startActivity(adIntent)
                    }
                    finish()
                }
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        showExitDialog()
        return true
    }
}
