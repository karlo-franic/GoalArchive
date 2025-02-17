package com.example.goalarchive.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "match",
    indices = [Index("league_id"), Index("player1_id"), Index("player2_id")],
    foreignKeys = [
        ForeignKey(entity = Player::class, parentColumns = ["id"], childColumns = ["player1_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Player::class, parentColumns = ["id"], childColumns = ["player2_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = League::class, parentColumns = ["id"], childColumns = ["league_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class Match(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val player1_id: Int,
    val player2_id: Int,
    val player1_goals: Int = 0,
    val player2_goals: Int = 0,
    val league_id: Int,
    val played: Boolean = false
)
