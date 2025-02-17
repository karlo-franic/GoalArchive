package com.example.goalarchive.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Pair
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import com.example.goalarchive.R
import com.example.goalarchive.data.database.AppDatabase
import com.example.goalarchive.data.database.dao.LeagueDao
import com.example.goalarchive.data.database.dao.LeaguePlayersDao
import com.example.goalarchive.data.database.dao.MatchDao
import com.example.goalarchive.data.database.dao.PlayerDao
import com.example.goalarchive.data.models.*
import com.example.goalarchive.databinding.ActivityCreateLeagueBinding
import kotlinx.coroutines.launch
import java.util.*

class CreateLeagueActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCreateLeagueBinding

    private var leaguePlayersId : Long = 0
    private var leagueId : Long = 0L

    private lateinit var database: AppDatabase
    private lateinit var leagueDao: LeagueDao
    private lateinit var playerDao: PlayerDao
    private lateinit var leaguePlayersDao: LeaguePlayersDao
    private lateinit var matchDao: MatchDao

    private val editTextPairs = mutableListOf<Pair<EditText, EditText>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateLeagueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database and DAOs
        database = AppDatabase.getDatabase(this)
        leagueDao = database.leagueDao()
        playerDao = database.playerDao()
        leaguePlayersDao = database.leaguePlayersDao()
        matchDao = database.matchDao()

        // Add the first pair of EditTexts
        addEditTextPair()

        val intent = Intent(this, TableActivity::class.java)

        binding.btnCreateTournament.setOnClickListener {
            // Convert pairs to a list of strings
            val dataList = editTextPairs.map { pair ->
                "${pair.first.text.toString()} - ${pair.second.text.toString()}"
            }.filter { it.split(" - ")[1].isNotEmpty() } // Only include pairs with non-empty clubs
                .toCollection(ArrayList())

            lifecycleScope.launch {
                val league = League(name = getCurrentTimestamp())
                leagueId = leagueDao.insertLeague(league)
                createTournament(dataList)

                if (leagueId != 0L) {
                    intent.putExtra("LeagueID", leagueId)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun addEditTextPair() {
        val rowLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 0)  // Adds spacing between rows
            }
            orientation = LinearLayout.HORIZONTAL
        }

        val editText1 = createEditText("Player Name...") // First EditText with Player Name hint
        val editText2 = createEditText("Club Name...") // Second EditText with Club Name hint

        // Add text change listener to detect input and add new row if needed
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty() && !alreadyHasNextRow(rowLayout)) {
                    addEditTextPair()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        editText1.addTextChangedListener(textWatcher)
        editText2.addTextChangedListener(textWatcher)

        // Add EditTexts to row layout
        rowLayout.addView(editText1)
        rowLayout.addView(editText2)

        // Add row to container
        binding.containerPlayerClub.addView(rowLayout)

        // Store the pair in the list
        editTextPairs.add(Pair(editText1, editText2))
    }

    private fun createEditText(hintText: String): EditText {
        return EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,  // Fixed width as per your request
                120   // Fixed height
            ).apply {
                weight = 1f
                setMargins(16, 16, 16, 16)  // Margin = 16dp
            }
            hint = hintText  // Set hint dynamically
            setPadding(8, 8, 8, 8)  // Padding = 2dp
            setTextColor(resources.getColor(android.R.color.black, theme))  // Text color = black
            setHintTextColor(Color.GRAY)
            setBackgroundResource(R.color.white)  // Background = white
        }
    }

    private fun alreadyHasNextRow(currentRow: View): Boolean {
        val index = binding.containerPlayerClub.indexOfChild(currentRow)
        return index < binding.containerPlayerClub.childCount - 1
    }


    private suspend fun createTournament(dataList : ArrayList<String>) {
        // Step 1: Insert players and leaguePlayers
        for (entry in dataList) {
            val playerName = entry.split(" - ").getOrNull(0) ?: "Unknown Player"
            val clubName = entry.split(" - ").getOrNull(1) ?: "Unknown Club"
            var tempPlayer = Player(name = playerName, club = clubName)
            val playerId = playerDao.insertPlayer(tempPlayer)

            val tempLeaguePlayers = LeaguePlayers(league_id = leagueId.toInt(), player_id = playerId.toInt())
            leaguePlayersId = leaguePlayersDao.insertLeaguePlayer(tempLeaguePlayers)
        }

        // Step 1: Create all possible matches for the league
        val listOfLeaguePlayersIds = leaguePlayersDao.getPlayersInLeague(leagueId.toInt())
        for (i in listOfLeaguePlayersIds.indices) {
            for (j in i + 1 until listOfLeaguePlayersIds.size) {

                if (j % 2 == 0) {
                    val match = Match(
                            player1_id = listOfLeaguePlayersIds[i],
                            player2_id = listOfLeaguePlayersIds[j],
                            league_id = leagueId.toInt()
                    )
                    matchDao.insertMatch(match)
                } else {
                    val match = Match(
                            player1_id = listOfLeaguePlayersIds[j],
                            player2_id = listOfLeaguePlayersIds[i],
                            league_id = leagueId.toInt()
                    )
                    matchDao.insertMatch(match)
                }
            }
        }
    }

}