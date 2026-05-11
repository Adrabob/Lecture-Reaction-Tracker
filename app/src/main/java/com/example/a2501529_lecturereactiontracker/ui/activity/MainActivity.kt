// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.a2501529_lecturereactiontracker.ui.adapter.CourseAdapter
import com.example.a2501529_lecturereactiontracker.PremiumManager
import com.example.a2501529_lecturereactiontracker.R
import com.example.a2501529_lecturereactiontracker.data.database.SystemDatabase
import com.example.a2501529_lecturereactiontracker.data.entity.Course
import com.example.a2501529_lecturereactiontracker.repository.LectureRepo
import com.example.a2501529_lecturereactiontracker.viewmodel.MainViewModel
import com.example.a2501529_lecturereactiontracker.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: CourseAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Default theme on first start
        if (savedInstanceState == null) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_main)

        // Setup toolbar and title
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Courses"

        // Setup database, repo, viewmodel
        val db = SystemDatabase.getDatabase(this)
        val repository = LectureRepo(
            db.courseDao(),
            db.sessionDao(),
            db.reactionDao()
        )
        val factory = ViewModelFactory(repository)
        viewModel = factory.create(MainViewModel::class.java)

        // Setup RecyclerView grid
        val rv = findViewById<RecyclerView>(R.id.rvCourses)
        rv.layoutManager = GridLayoutManager(this, 2)

        // Adapter: click and long click
        adapter = CourseAdapter(
            onCourseClick = { course ->
                val intent = Intent(this, DetailsActivity::class.java)
                intent.putExtra("course_id", course.courseId)
                intent.putExtra("course_name", course.courseName)
                startActivity(intent)
            },
            onCourseLongClick = { course ->
                showDeleteCourseDialog(course)
            }
        )
        rv.adapter = adapter

        // Collect course list from flow
        lifecycleScope.launch {
            viewModel.allCourses.collect { list ->
                adapter.submitList(list)
            }
        }

        // Floating button for adding course
        val fabAddCourse: FloatingActionButton =
            findViewById(R.id.fabAddCourse)

        fabAddCourse.setOnClickListener {
            checkAndOpenAddCourse()
        }
    }

    // Inflate toolbar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Update theme menu item state
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val themeItem = menu?.findItem(R.id.action_theme)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDark = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        themeItem?.isChecked = isDark

        return super.onPrepareOptionsMenu(menu)
    }

    // Handle toolbar menu clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_premium -> {
                showPremiumDialog()
                true
            }
            R.id.action_theme -> {
                toggleTheme()
                true
            }
            R.id.action_reset_premium -> {
                PremiumManager.setPremium(this, false)
                Toast.makeText(this, "Restore Purchases", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Switch between dark and light
    private fun toggleTheme() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()

        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        // Activity will recreate automatically
    }

    // Check free limit and open AddCourse
    private fun checkAndOpenAddCourse() {
        val currentCourseCount = adapter.currentList.size
        val isPremium = PremiumManager.isPremium(this)

        if (!isPremium && currentCourseCount >= 2) {
            showPremiumDialog()
        } else {
            val intent = Intent(this, AddCourseActivity::class.java)
            startActivity(intent)
        }
    }

    // Show premium info dialog
    private fun showPremiumDialog() {
        val isPremium = PremiumManager.isPremium(this)

        if (isPremium) {
            AlertDialog.Builder(this)
                .setTitle("Premium Active")
                .setMessage("You have unlimited access! Thank you for your support.")
                .setPositiveButton("OK", null)
                .show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Unlock Unlimited Courses")
                .setMessage("Free version is limited to 2 courses.\n\nUpgrade to Premium for:\n• Unlimited Courses\n• No Ads\n")
                .setPositiveButton("Upgrade ($1.99)") { _, _ ->
                    PremiumManager.setPremium(this, true)
                    Toast.makeText(this, "Welcome to Premium!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    // Bottom sheet for course delete
    private fun showDeleteCourseDialog(course: Course) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.delete_sheet, null)

        val txtTitle = view.findViewById<TextView>(R.id.txtDeleteTitle)
        val btnCancel = view.findViewById<TextView>(R.id.btnCancelDelete)
        val btnConfirm = view.findViewById<TextView>(R.id.btnConfirmDelete)

        txtTitle.text = "Delete ${course.courseName}?"

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnConfirm.setOnClickListener {
            viewModel.deleteCourse(course)
            Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
}
