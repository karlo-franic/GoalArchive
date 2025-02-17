package com.example.goalarchive.data.models

import androidx.room.Entity

@Entity(
    tableName = "league_players",
    primaryKeys = ["league_id", "player_id"]
)
data class LeaguePlayers(
    val league_id: Int,
    val player_id: Int
)
