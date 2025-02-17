package com.example.goalarchive.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.goalarchive.data.database.AppDatabase
import com.example.goalarchive.data.database.dao.MatchDao
import com.example.goalarchive.data.database.dao.PlayerDao
import com.example.goalarchive.data.models.Match
import com.example.goalarchive.databinding.ActivityEditMatchBinding
import com.example.goalarchive.databinding.ActivityTableBinding
import kotlinx.coroutines.launch

class EditMatchActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditMatchBinding

    private lateinit var database: AppDatabase
    private lateinit var playerDao: PlayerDao
    private lateinit var matchDao: MatchDao

    private var match: Match? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        playerDao = database.playerDao()
        matchDao = database.matchDao()

        lifecycleScope.launch {

            match = matchDao.getMatchById(intent.getIntExtra("id", 0))

            if (match != null) {
                binding.club1Name.text = playerDao.getPlayerClubById(match!!.player1_id)
                binding.club2Name.text = playerDao.getPlayerClubById(match!!.player2_id)
                binding.club1Score.setText(match!!.player1_goals.toString())
                binding.club2Score.setText(match!!.player2_goals.toString())
            }
        }

        binding.btnSaveResult.setOnClickListener {
            val intent = Intent(this, TableActivity::class.java)

            if (match != null) {
                val updatedPlayer1Goals = binding.club1Score.text.toString().toIntOrNull() ?: 0
                val updatedPlayer2Goals = binding.club2Score.text.toString().toIntOrNull() ?: 0
                val matchId = match!!.id

                lifecycleScope.launch {

                    if (match!!.played) {
                        // If the match was previously played, remove the old result
                        if (match!!.player1_goals > match!!.player2_goals) {
                            // Player 1 won, Player 2 lost
                            playerDao.incrementPlayerStats(
                                match!!.player1_id,
                                winsIncrement = -1,
                                goalsScoredIncrement = -match!!.player1_goals,
                                goalsConcededIncrement = -match!!.player2_goals
                            )
                            playerDao.incrementPlayerStats(
                                match!!.player2_id,
                                lossesIncrement = -1,
                                goalsScoredIncrement = -match!!.player2_goals,
                                goalsConcededIncrement = -match!!.player1_goals
                            )
                        } else if (match!!.player1_goals < match!!.player2_goals) {
                            // Player 2 won, Player 1 lost
                            playerDao.incrementPlayerStats(
                                match!!.player2_id,
                                winsIncrement = -1,
                                goalsScoredIncrement = -match!!.player2_goals,
                                goalsConcededIncrement = -match!!.player1_goals
                            )
                            playerDao.incrementPlayerStats(
                                match!!.player1_id,
                                lossesIncrement = -1,
                                goalsScoredIncrement = -match!!.player1_goals,
                                goalsConcededIncrement = -match!!.player2_goals
                            )
                        } else {
                            // It was a draw
                            playerDao.incrementPlayerStats(
                                match!!.player1_id,
                                drawsIncrement = -1,
                                goalsScoredIncrement = -match!!.player1_goals,
                                goalsConcededIncrement = -match!!.player2_goals
                            )
                            playerDao.incrementPlayerStats(
                                match!!.player2_id,
                                drawsIncrement = -1,
                                goalsScoredIncrement = -match!!.player2_goals,
                                goalsConcededIncrement = -match!!.player1_goals
                            )
                        }
                    }

                    // Update match in the database
                    matchDao.updateMatch(matchId, updatedPlayer1Goals, updatedPlayer2Goals)

                    // Now, add the new result
                    if (updatedPlayer1Goals > updatedPlayer2Goals) {
                        playerDao.incrementPlayerStats(match!!.player1_id, winsIncrement = 1, goalsScoredIncrement = updatedPlayer1Goals, goalsConcededIncrement = updatedPlayer2Goals)
                        playerDao.incrementPlayerStats(match!!.player2_id, lossesIncrement = 1, goalsScoredIncrement = updatedPlayer2Goals, goalsConcededIncrement = updatedPlayer1Goals)
                    } else if (updatedPlayer1Goals < updatedPlayer2Goals) {
                        playerDao.incrementPlayerStats(match!!.player2_id, winsIncrement = 1, goalsScoredIncrement = updatedPlayer2Goals, goalsConcededIncrement = updatedPlayer1Goals)
                        playerDao.incrementPlayerStats(match!!.player1_id, lossesIncrement = 1, goalsScoredIncrement = updatedPlayer1Goals, goalsConcededIncrement = updatedPlayer2Goals)
                    } else {
                        playerDao.incrementPlayerStats(match!!.player1_id, drawsIncrement = 1, goalsScoredIncrement = updatedPlayer1Goals, goalsConcededIncrement = updatedPlayer2Goals)
                        playerDao.incrementPlayerStats(match!!.player2_id, drawsIncrement = 1, goalsScoredIncrement = updatedPlayer2Goals, goalsConcededIncrement = updatedPlayer1Goals)
                    }

                    intent.putExtra("LeagueID", match!!.league_id.toLong())
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this@EditMatchActivity, "Error: Match not found", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }
        }
    }
}