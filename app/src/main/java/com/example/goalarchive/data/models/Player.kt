package com.example.goalarchive.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val club: String,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val goal_scored: Int = 0,
    val goal_conceded: Int = 0
)
