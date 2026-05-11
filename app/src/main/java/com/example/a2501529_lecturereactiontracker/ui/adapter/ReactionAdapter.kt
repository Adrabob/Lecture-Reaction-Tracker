// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.ui.adapter

import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a2501529_lecturereactiontracker.R
import com.example.a2501529_lecturereactiontracker.data.entity.Reaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.TimeUnit

class ReactionAdapter :
    ListAdapter<Reaction, ReactionAdapter.ReactionViewHolder>(DiffCallback) {

    // Check if items same
    object DiffCallback : DiffUtil.ItemCallback<Reaction>() {
        override fun areItemsTheSame(oldItem: Reaction, newItem: Reaction) =
            oldItem.reactionsId == newItem.reactionsId

        override fun areContentsTheSame(oldItem: Reaction, newItem: Reaction) =
            oldItem == newItem
    }

    // ViewHolder holding UI refs
    class ReactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardReaction)
        val txtEmoji: TextView = itemView.findViewById(R.id.txtReactionEmoji)
        val txtTime: TextView = itemView.findViewById(R.id.txtReactionTime)
        val txtType: TextView = itemView.findViewById(R.id.txtReactionType)
        val imgNote: ImageView = itemView.findViewById(R.id.imgNoteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reaction, parent, false)
        return ReactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReactionViewHolder, position: Int) {
        val reaction = getItem(position)
        val context = holder.itemView.context

        // Format minutes/seconds display
        val minutes = TimeUnit.MILLISECONDS.toMinutes(reaction.chrTimestamp)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(reaction.chrTimestamp) % 60
        holder.txtTime.text = String.format("%02d:%02d", minutes, seconds)

        // Detect dark/light mode
        val isNightMode = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        // Set emoji and card tone
        val type = reaction.reactionType.lowercase()
        val textColor = if (isNightMode) Color.WHITE else Color.BLACK

        when (type) {
            "understood" -> {
                holder.txtEmoji.text = "🙂"
                holder.txtType.text = "Understood"
                val colorRes = if (isNightMode)
                    R.color.reaction_understood_dark else R.color.reaction_understood_light
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, colorRes))
            }
            "confused" -> {
                holder.txtEmoji.text = "😕"
                holder.txtType.text = "Confused"
                val colorRes = if (isNightMode)
                    R.color.reaction_confused_dark else R.color.reaction_confused_light
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, colorRes))
            }
            "lost" -> {
                holder.txtEmoji.text = "😐"
                holder.txtType.text = "Lost"
                val colorRes = if (isNightMode)
                    R.color.reaction_lost_dark else R.color.reaction_lost_light
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, colorRes))
            }
            else -> {
                // fallback card color
                holder.cardView.setCardBackgroundColor(if (isNightMode) Color.DKGRAY else Color.WHITE)
            }
        }

        holder.txtType.setTextColor(textColor)
        holder.txtTime.setTextColor(textColor)

        // Show note icon if note exists
        if (!reaction.reactionNotes.isNullOrEmpty()) {
            holder.imgNote.visibility = View.VISIBLE
            holder.itemView.isClickable = true
            holder.itemView.isFocusable = true
        } else {
            holder.imgNote.visibility = View.GONE
            holder.itemView.isClickable = false
            holder.itemView.isFocusable = false
        }

        // Show note in dialog when clicked
        holder.itemView.setOnClickListener {
            if (!reaction.reactionNotes.isNullOrEmpty()) {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Note")
                    .setMessage(reaction.reactionNotes)
                    .setPositiveButton("Close", null)
                    .show()
            }
        }
    }
}
