package com.example.goalarchive.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goalarchive.data.models.League

@Dao
interface LeagueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeague(league: League): Long

    @Query("SELECT * FROM league ORDER BY id DESC")
    suspend fun getAllLeagues(): List<League>

    @Query("SELECT * FROM league ORDER BY id DESC LIMIT 1")
    suspend fun getLastLeague(): League

    @Query("SELECT * FROM league WHERE id = :leagueId")
    suspend fun getLeagueById(leagueId: Int): League

    @Query("DELETE FROM League WHERE id = :leagueId")
    suspend fun deleteLeagueById(leagueId: Int)
}