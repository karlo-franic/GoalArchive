package com.example.goalarchive.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.goalarchive.data.models.LeaguePlayers

@Dao
interface LeaguePlayersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaguePlayer(leaguePlayers: LeaguePlayers): Long

    @Query("SELECT player_id FROM league_players WHERE league_id = :leagueId")
    suspend fun getPlayersInLeague(leagueId: Int): List<Int>

    @Query("DELETE FROM league_players WHERE league_id = :leagueId")
    suspend fun deleteLeaguePlayersByLeagueId(leagueId: Int)
}