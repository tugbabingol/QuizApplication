package com.tugbabingol.quizapplication.Utils

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.tugbabingol.quizapplication.LoginActivity
import com.tugbabingol.quizapplication.MainActivity
import com.tugbabingol.quizapplication.R
import com.tugbabingol.quizapplication.ScoreBoardActivity

class BottomNavigation {
    private lateinit var auth : FirebaseAuth
    fun bottomNavigation(bottomNavigationView: BottomNavigationView){
        auth = FirebaseAuth.getInstance()
    /*kjnkbk*/
        /*bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_scoreboard -> {
                    val intent = Intent(this, ScoreBoardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_logout -> {
                    // Notifications seçeneği tıklandığında yapılacak işlemler
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }*/
    }
}