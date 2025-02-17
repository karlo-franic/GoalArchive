package com.example.goalarchive.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.goalarchive.data.models.Match

@Dao
interface MatchDao {

    @Insert
    suspend fun insertMatch(match: Match)

    @Query("SELECT * FROM 'match' WHERE id = :matchId")
    suspend fun getMatchById(matchId: Int): Match?

    @Query("SELECT * FROM 'match'")
    suspend fun getAllMatches(): List<Match>

    @Query("SELECT * FROM 'match' WHERE player1_id = :playerId OR player2_id = :playerId")
    suspend fun getMatchesByPlayer(playerId: Int): List<Match>

    @Query("SELECT * FROM 'match' WHERE league_id = :leagueId")
    suspend fun getMatchesByLeague(leagueId: Int): List<Match>

    @Query("SELECT * FROM 'match' WHERE league_id = :leagueId AND played = 1")
    suspend fun getPlayedMatches(leagueId: Int): List<Match>

    @Query("SELECT * FROM 'match' WHERE league_id = :leagueId AND played = 0")
    suspend fun getUnplayedMatches(leagueId: Int): List<Match>

    @Query("UPDATE 'match' SET played = 1 WHERE id = :matchId")
    suspend fun markMatchAsPlayed(matchId: Int)

    @Query("""
        UPDATE `match`
        SET player1_goals = :player1Goals, player2_goals = :player2Goals, played = 1
        WHERE id = :matchId
    """)
    suspend fun updateMatch(matchId: Int, player1Goals: Int, player2Goals: Int)

    @Query("DELETE FROM 'match' WHERE league_id = :leagueId")
    suspend fun deleteMatchesByLeagueId(leagueId: Int)
}
