package com.example.goalarchive.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goalarchive.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNewTournament.setOnClickListener {

            val intent = Intent(this, CreateLeagueActivity::class.java)
            startActivity(intent)
        }

        binding.btnContinueTournament.setOnClickListener {

            val intent = Intent(this, ContinueLeagueActivity::class.java)
            startActivity(intent)
        }
    }


}