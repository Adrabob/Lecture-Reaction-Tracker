// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a2501529_lecturereactiontracker.R
import com.example.a2501529_lecturereactiontracker.data.entity.Course

class CourseAdapter(
    private val onCourseClick: (Course) -> Unit,
    private val onCourseLongClick: (Course) -> Unit
) : ListAdapter<Course, CourseAdapter.CourseViewHolder>(DiffCallback) {

    // Compare course items
    object DiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course) =
            oldItem.courseId == newItem.courseId

        override fun areContentsTheSame(oldItem: Course, newItem: Course) =
            oldItem == newItem
    }

    // Holds course name view
    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtCourseName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = getItem(position)
        holder.txtName.text = course.courseName

        // Simple tap action
        holder.itemView.setOnClickListener {
            onCourseClick(course)
        }

        // Long press action
        holder.itemView.setOnLongClickListener {
            onCourseLongClick(course)
            true
        }
    }
}
