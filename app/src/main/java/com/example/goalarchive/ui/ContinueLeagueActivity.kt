package com.example.goalarchive.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.goalarchive.data.database.AppDatabase
import com.example.goalarchive.data.database.dao.LeagueDao
import com.example.goalarchive.data.database.dao.LeaguePlayersDao
import com.example.goalarchive.data.database.dao.MatchDao
import com.example.goalarchive.data.database.dao.PlayerDao
import com.example.goalarchive.data.models.League
import com.example.goalarchive.data.models.Match
import com.example.goalarchive.databinding.ActivityContinueLeagueBinding
import com.example.goalarchive.databinding.ActivityEditMatchBinding
import kotlinx.coroutines.launch

class ContinueLeagueActivity : AppCompatActivity() {

    private lateinit var binding : ActivityContinueLeagueBinding

    private lateinit var database: AppDatabase
    private lateinit var leagueDao: LeagueDao
    private lateinit var playerDao: PlayerDao
    private lateinit var leaguePlayersDao: LeaguePlayersDao
    private lateinit var matchDao: MatchDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContinueLeagueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        leagueDao = database.leagueDao()
        playerDao = database.playerDao()
        leaguePlayersDao = database.leaguePlayersDao()
        matchDao = database.matchDao()

        lifecycleScope.launch {
            val leagues = leagueDao.getAllLeagues()

            displayLeagues(leagues)
        }

    }


    private fun displayLeagues(leagues: List<League>) {
        Log.d("LeagueList", "Number of Leagues: ${leagues.size}")
        binding.continueLeagueLayout.removeAllViews()

        val intent = Intent(this, TableActivity::class.java)
        val intentSame = Intent(this, ContinueLeagueActivity::class.java)

        lifecycleScope.launch {

            leagues.forEach { league ->
                val leagueNameTextView = TextView(this@ContinueLeagueActivity).apply {
                    text = league.name
                    textSize = 18f
                    setTextColor(Color.WHITE)
                    gravity = Gravity.START
                }

                val gamesRemaining = matchDao.getUnplayedMatches(league.id).size

                val gamesTextView = TextView(this@ContinueLeagueActivity).apply {
                    text = "Games remaining: " + gamesRemaining.toString()
                    textSize = 14f
                    setTextColor(Color.GRAY)
                    gravity = Gravity.START
                }

                val actionButton = Button(this@ContinueLeagueActivity).apply {
                    text = "Continue"
                    setBackgroundColor(Color.parseColor("#FF447700"))
                    setTextColor(Color.WHITE)

                    setOnClickListener {
                        intent.putExtra("LeagueID", league.id.toLong())
                        startActivity(intent)
                    }
                }

                val deleteButton = Button(this@ContinueLeagueActivity).apply {
                    text = "Delete"
                    setBackgroundColor(Color.parseColor("#FF993333"))
                    setTextColor(Color.WHITE)

                    setOnClickListener {
                        lifecycleScope.launch {
                            matchDao.deleteMatchesByLeagueId(league.id)
                            playerDao.deletePlayersByLeagueId(league.id)
                            leaguePlayersDao.deleteLeaguePlayersByLeagueId(league.id)
                            leagueDao.deleteLeagueById(league.id)

                            finish()
                            startActivity(intentSame)
                        }
                    }
                }

                val textRowLayout = LinearLayout(this@ContinueLeagueActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    setBackgroundColor(Color.parseColor("#88000000"))
                    setPadding(16, 8, 16, 8)

                    val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 4, 8, 4)
                    }
                    this.layoutParams = layoutParams

                    addView(leagueNameTextView)
                    addView(gamesTextView)
                }

                val leagueRowLayout = LinearLayout(this@ContinueLeagueActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setBackgroundColor(Color.parseColor("#88000000"))
                    setPadding(16, 8, 16, 8)

                    val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 4, 8, 4)
                    }
                    this.layoutParams = layoutParams

                    val textParams = LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                    )

                    addView(textRowLayout, textParams)
                    addView(actionButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                    addView(deleteButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                }

                binding.continueLeagueLayout.addView(leagueRowLayout)
            }
        }
    }
}