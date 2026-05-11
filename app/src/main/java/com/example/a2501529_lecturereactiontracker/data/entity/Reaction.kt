// Student No: 2501529
package com.example.a2501529_lecturereactiontracker.data.entity
//This is our Reactions database entity.
//It creates table for our database.

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "reactions")
data class Reaction(
    @PrimaryKey(autoGenerate = true)
    val reactionsId: Long = 0, //Reactions database Id

    val sessionId: Long, //Every session contains one reaction so that we call the sessionId here
    val chrTimestamp: Long, //When the user push the reaction button this gives millisecond timestamp.
    val reactionType: String, //"Confused", "Understood", "Lost"
    val reactionNotes: String? = null //This is our optional reaction notes
)