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
import com.example.a2501529_lecturereactiontracker.data.entity.Session
import java.text.SimpleDateFormat
import java.util.*

class SessionAdapter(
    private val onClick: (Session) -> Unit
) : ListAdapter<Session, SessionAdapter.SessionViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Session>() {
        override fun areItemsTheSame(oldItem: Session, newItem: Session) =
            oldItem.sessionId == newItem.sessionId

        override fun areContentsTheSame(oldItem: Session, newItem: Session) =
            oldItem == newItem
    }

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtSessionDate: TextView = itemView.findViewById(R.id.txtSessionDate)
        val txtSessionDuration: TextView = itemView.findViewById(R.id.txtSessionDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_session, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = getItem(position)

        // simple date text
        val dateFormat = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.getDefault())
        val dateText = dateFormat.format(Date(session.startedTimestamp))
        holder.txtSessionDate.text = "Session on $dateText"

        // simple duration text
        val durationText = if (session.endedTimestamp != null) {
            formatDuration(session.startedTimestamp, session.endedTimestamp!!)
        } else {
            "In progress"
        }
        holder.txtSessionDuration.text = durationText

        // item click event
        holder.itemView.setOnClickListener {
            onClick(session)
        }
    }

    // basic duration formatter
    private fun formatDuration(start: Long, end: Long): String {
        val diff = end - start
        val totalSeconds = diff / 1000

        if (totalSeconds < 60) return "${totalSeconds} seconds"

        val totalMinutes = totalSeconds / 60
        val remainingSeconds = totalSeconds % 60
        if (totalMinutes < 60) return "${totalMinutes} minutes ${remainingSeconds} seconds"

        val hours = totalMinutes / 60
        val remainingMinutes = totalMinutes % 60

        return "${hours}h ${remainingMinutes}m"
    }
}
