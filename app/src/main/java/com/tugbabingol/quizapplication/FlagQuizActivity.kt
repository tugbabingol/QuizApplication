package com.tugbabingol.quizapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FlagQuizActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var correctCountry: String
    private var wrongAttempts: Int = 0
    private  var sayac: Int = 0
    private lateinit var imageFlag: ImageView
    private lateinit var secenekA: TextView
    private lateinit var secenekB: TextView
    private lateinit var secenekC: TextView
    private lateinit var secenekD: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_flag_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()

        imageFlag = findViewById(R.id.flag_imageView)
        secenekA = findViewById(R.id.SecenekA)
        secenekB = findViewById(R.id.SecenekB)
        secenekC = findViewById(R.id.SecenekC)
        secenekD = findViewById(R.id.SecenekD)

        startGame()

        secenekA.setOnClickListener {
            checkAnswer(secenekA)
        }

        secenekB.setOnClickListener {
            checkAnswer(secenekB)
        }

        secenekC.setOnClickListener {
            checkAnswer(secenekC)
        }

        secenekD.setOnClickListener {
            checkAnswer(secenekD)
        }
    }

    private fun startGame() {
        CoroutineScope(Dispatchers.Main).launch {
            val flagData = loadFlagData()
            if (flagData != null) {
                val (flagUrl, flagName) = flagData
                correctCountry = flagName

                loadFlagImage(flagUrl)
                setOptions(flagName)
            } else {
                Log.e("FlagQuizActivity", "Failed to fetch flag information")
            }
        }
    }

    private suspend fun loadFlagData(): Pair<String, String>? = withContext(Dispatchers.IO) {
        try {
            val documents = db.collection("Flags").get().await()
            val randomFlag = documents.documents.random()
            val flagUrl = randomFlag.getString("flagurl") ?: return@withContext null
            val flagName = randomFlag.getString("flagname") ?: return@withContext null
            return@withContext Pair(flagUrl, flagName)
        } catch (e: Exception) {
            Log.e("FlagQuizActivity", "Error fetching flag data: ${e.message}")
            return@withContext null
        }
    }

    private suspend fun loadFlagImage(flagUrl: String) = withContext(Dispatchers.Main) {
        Picasso.get().load(flagUrl).resize(500, 500).centerCrop().into(imageFlag, object : com.squareup.picasso.Callback {
            override fun onSuccess() {
                Log.d("Picasso", "Image loaded successfully")
            }

            override fun onError(e: Exception) {
                Log.e("Picasso", "Error loading image: $e")
            }
        })
    }

    private suspend fun setOptions(correctOption: String) = withContext(Dispatchers.IO) {
        val options = listOf(secenekA, secenekB, secenekC, secenekD)
        val randomIndex = Random.nextInt(0, options.size)
        options[randomIndex].text = correctOption

        for (i in options.indices) {
            if (i != randomIndex) {
                randomFlagNameGet(options[i])
            }
        }
    }

    private suspend fun randomFlagNameGet(textView: TextView) = withContext(Dispatchers.IO) {
        try {
            val documents = db.collection("Flags").get().await()
            val randomFlag = documents.documents.random()
            val countryName = randomFlag.getString("flagname") ?: ""
            withContext(Dispatchers.Main) {
                textView.text = countryName
            }
        } catch (e: Exception) {
            Log.e("FlagQuizActivity", "Failed to fetch random flag name: ${e.message}")
        }
    }

    private fun checkAnswer(selectedTextView: TextView) {



        if (selectedTextView.text == correctCountry) {
            sayac += 10

            Log.e("sayac", "sayac arttı")

            findViewById<TextView>(R.id.sayacTextView).text = sayac.toString() // TextView'de sayacı göster

            startGame()


        } else {
            wrongAttempts++
            if (wrongAttempts >= 3) {
                Log.d("FlagQuizActivity", "Oyunu kaybettiniz!")
                showGameOverDialog()
            } else {
                val heart3 = findViewById<ImageView>(R.id.heart3)
                val heart2 = findViewById<ImageView>(R.id.heart2)
                val heart1 = findViewById<ImageView>(R.id.heart1)
                if (wrongAttempts == 1) {
                    heart3.visibility = View.INVISIBLE
                } else if (wrongAttempts == 2) {
                    heart2.visibility = View.INVISIBLE
                } else {
                    heart1.visibility = View.INVISIBLE

                }
                Log.d("FlagQuizActivity", "Yanlış cevap!")
                startGame()
            }
        }
    }

    private fun showGameOverDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Game Over")
        Log.e("kontrol", "Error loading image: $sayac")
        builder.setMessage("Your game has ended. Your score is ${sayac}.Would you like to return to the main menu?")
        builder.setPositiveButton("Main Menu") { dialog, which ->
            // Ana menüye dönmek için gereken işlemleri yapın
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // İptal işlemi
            dialog.dismiss()
        }
        builder.setCancelable(false) // Kullanıcı diyalogu kapatamaz
        val dialog = builder.create()
        dialog.show()
    }
}
