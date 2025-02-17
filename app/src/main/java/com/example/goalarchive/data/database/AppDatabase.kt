package com.example.goalarchive.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.goalarchive.data.database.dao.LeagueDao
import com.example.goalarchive.data.database.dao.LeaguePlayersDao
import com.example.goalarchive.data.database.dao.MatchDao
import com.example.goalarchive.data.database.dao.PlayerDao
import com.example.goalarchive.data.models.League
import com.example.goalarchive.data.models.LeaguePlayers
import com.example.goalarchive.data.models.Match
import com.example.goalarchive.data.models.Player
import kotlin.synchronized

@Database(entities = [League::class, Player::class, LeaguePlayers::class, Match::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun leagueDao(): LeagueDao
    abstract fun playerDao(): PlayerDao
    abstract fun leaguePlayersDao(): LeaguePlayersDao
    abstract fun matchDao(): MatchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(AppDatabase::class.java) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "league_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}