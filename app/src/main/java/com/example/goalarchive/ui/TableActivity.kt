package com.example.goalarchive.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.goalarchive.R
import com.example.goalarchive.data.database.AppDatabase
import com.example.goalarchive.data.database.dao.LeagueDao
import com.example.goalarchive.data.database.dao.LeaguePlayersDao
import com.example.goalarchive.data.database.dao.MatchDao
import com.example.goalarchive.data.database.dao.PlayerDao
import com.example.goalarchive.data.models.Match
import com.example.goalarchive.databinding.ActivityTableBinding
import kotlinx.coroutines.launch

class TableActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTableBinding

    private lateinit var database: AppDatabase
    private lateinit var leagueDao: LeagueDao
    private lateinit var playerDao: PlayerDao
    private lateinit var leaguePlayersDao: LeaguePlayersDao
    private lateinit var matchDao: MatchDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        leagueDao = database.leagueDao()
        playerDao = database.playerDao()
        leaguePlayersDao = database.leaguePlayersDao()
        matchDao = database.matchDao()

        lifecycleScope.launch {
            var matches = matchDao.getMatchesByLeague(intent.getLongExtra("LeagueID", 0L).toInt())

            populateTable()

            displayMatches(matches)
        }
    }

    private fun populateTable() {

        lifecycleScope.launch {
            val listOfSortedPlayers = playerDao.getAllPlayersByLeagueSorted(intent.getLongExtra("LeagueID", 0L).toInt())

            var position = 1
            var rowIndex = 0

            for (entry in listOfSortedPlayers) {
                addTableRow(position, entry.club, (entry.wins+entry.draws+entry.losses), entry.goal_scored, entry.goal_conceded, (entry.goal_scored-entry.goal_conceded), (3*entry.wins+entry.draws), rowIndex)
                position++
                rowIndex++
            }
        }
    }

    private fun addTableRow(pos: Int, club: String, games: Int, goalsScored: Int, goalsConceded: Int, goalsBalance: Int, points: Int, rowIndex: Int) {
        val row = TableRow(this)

        // Alternate row background color based on row index (even or odd)
        if (rowIndex % 2 == 0) {
            row.setBackgroundColor(Color.parseColor("#80FFFFFF"))
        } else {
            row.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val values = listOf(pos.toString(), club, games.toString(), goalsScored.toString(), goalsConceded.toString(), goalsBalance.toString(), points.toString())

        for (value in values) {
            val textView = TextView(this).apply {
                text = value
                setPadding(8, 8, 8, 8)
                textSize = 16f
                setTextColor(Color.parseColor("#000000"))
            }
            row.addView(textView)
        }
        binding.tableLayout.addView(row)
    }


    private fun displayMatches(matches: List<Match>) {
        Log.d("MatchList", "Number of matches: ${matches.size}")
        binding.listOfMatchesLayout.removeAllViews()

        val intent = Intent(this, EditMatchActivity::class.java)

        lifecycleScope.launch {

            matches.forEach { match ->
                val player1Club = playerDao.getPlayerClubById(match.player1_id)
                val player2Club = playerDao.getPlayerClubById(match.player2_id)

                val matchTextView = TextView(this@TableActivity).apply {
                    text = if (match.played) {
                        "${player1Club}  ${match.player1_goals} - ${match.player2_goals}  ${player2Club}"
                    } else {
                        "${player1Club}   -   ${player2Club}"
                    }
                    textSize = 16f
                    setTextColor(Color.WHITE)
                }

                val actionButton = Button(this@TableActivity).apply {
                    text = if (match.played) "Edit" else "Enter"

                    setBackgroundColor(
                            if (match.played) Color.parseColor("#FF808080")
                            else Color.parseColor("#FF447700")
                    )
                    setTextColor(Color.WHITE)

                    setOnClickListener {
                        intent.putExtra("id", match.id)
                        intent.putExtra("LeagueID", match.league_id)
                        startActivity(intent)
                    }
                }

                // Create a horizontal layout to hold the match text and button
                val matchRowLayout = LinearLayout(this@TableActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setBackgroundColor(Color.parseColor("#88000000"))
                    setPadding(16, 8, 16, 8)

                    // Set margins using LayoutParams
                    val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 4, 8, 4)
                    }
                    this.layoutParams = layoutParams

                    addView(matchTextView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
                    addView(actionButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                }
                binding.listOfMatchesLayout.addView(matchRowLayout)
            }
        }
    }


}