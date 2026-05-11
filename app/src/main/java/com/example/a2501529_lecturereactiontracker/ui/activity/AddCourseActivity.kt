// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.ui.activity

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.a2501529_lecturereactiontracker.R
import com.example.a2501529_lecturereactiontracker.data.database.SystemDatabase
import com.example.a2501529_lecturereactiontracker.repository.LectureRepo
import com.example.a2501529_lecturereactiontracker.viewmodel.MainViewModel
import com.example.a2501529_lecturereactiontracker.viewmodel.ViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class AddCourseActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        //Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.addCourseToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Course"

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // --- SETUP DB + REPOSITORY + VIEWMODEL ---
        val db = SystemDatabase.Companion.getDatabase(this)
        val repository = LectureRepo(
            db.courseDao(),
            db.sessionDao(),
            db.reactionDao()
        )

        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        // --- UI ELEMENTS ---
        val editTextName = findViewById<EditText>(R.id.editTextCourseName)
        val buttonSave = findViewById<MaterialButton>(R.id.saveButton)

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString().trim()

            if (name.isNotEmpty()) {
                viewModel.insertCourse(name)
                finish()    // return to MainActivity
            } else {
                editTextName.error = "Course name cannot be empty"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}