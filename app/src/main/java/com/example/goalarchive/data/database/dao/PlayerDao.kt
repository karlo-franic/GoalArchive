package com.example.goalarchive.data.database.dao

import androidx.room.*
import com.example.goalarchive.data.models.Player

@Dao
interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player): Long

    @Query("SELECT * FROM player")
    suspend fun getAllPlayers(): List<Player>

    @RewriteQueriesToDropUnusedColumns
    @Query("""
        SELECT * 
        FROM player 
        INNER JOIN league_players ON Player.id = league_players.player_id 
        WHERE league_players.league_id = :leagueId
    """)
    suspend fun getAllPlayersByLeague(leagueId: Int): List<Player>

    @RewriteQueriesToDropUnusedColumns
    @Query("""
    SELECT 
        player.*, 
        (wins * 3 + draws) AS points, 
        (goal_scored - goal_conceded) AS goal_difference 
    FROM player 
    INNER JOIN league_players ON Player.id = league_players.player_id 
    WHERE league_players.league_id = :leagueId 
    ORDER BY points DESC, 
             goal_difference DESC, 
             goal_scored DESC
""")
    suspend fun getAllPlayersByLeagueSorted(leagueId: Int): List<Player>

    @Query("SELECT club FROM player WHERE id = :playerId")
    suspend fun getPlayerClubById(playerId: Int): String

    @Query("""
        UPDATE player
        SET 
            wins = wins + :winsIncrement, 
            losses = losses + :lossesIncrement, 
            draws = draws + :drawsIncrement, 
            goal_scored = goal_scored + :goalsScoredIncrement, 
            goal_conceded = goal_conceded + :goalsConcededIncrement
        WHERE id = :playerId
    """)
    suspend fun incrementPlayerStats(
            playerId: Int,
            winsIncrement: Int = 0,
            lossesIncrement: Int = 0,
            drawsIncrement: Int = 0,
            goalsScoredIncrement: Int = 0,
            goalsConcededIncrement: Int = 0
    )

    @Query("""
        DELETE FROM player
        WHERE id IN (
            SELECT player_id FROM league_players WHERE league_id = :leagueId
        )
    """)
    suspend fun deletePlayersByLeagueId(leagueId: Int)

}