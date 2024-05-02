package com.tugbabingol.quizapplication

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlin.random.Random

class FlagQuizActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var correctCountry: String
    private var wrongAttempts: Int = 0

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

        val imageFlag = findViewById<ImageView>(R.id.flag_imageView)
        val secenekA = findViewById<TextView>(R.id.SecenekA)
        val secenekB = findViewById<TextView>(R.id.SecenekB)
        val secenekC = findViewById<TextView>(R.id.SecenekC)
        val secenekD = findViewById<TextView>(R.id.SecenekD)

        startGame(imageFlag, secenekA, secenekB, secenekC, secenekD)

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

    private fun startGame(imageView: ImageView, secenekA: TextView, secenekB: TextView, secenekC: TextView, secenekD: TextView) {
        db.collection("Flags").get().addOnSuccessListener { documents ->
            val randomFlag = documents.documents.random()
            correctCountry = randomFlag.getString("flagname") ?: ""
            val flagUrl = randomFlag.getString("flagurl") ?: ""

            Picasso.get().load(flagUrl).resize(500, 500).centerCrop()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        Log.d("Picasso", "Image loaded successfully")
                    }

                    override fun onError(e: Exception) {
                        Log.e("Picasso", "Error loading image: $e")
                    }
                })

            val options = listOf(secenekA, secenekB, secenekC, secenekD)
            val randomIndex = Random.nextInt(0, options.size)
            options[randomIndex].text = correctCountry

            for (i in options.indices) {
                if (i != randomIndex) {
                    randomFlagNameGet(options[i])
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("FlagQuizActivity", "Failed to fetch flag information: ${exception.message}")
        }
    }

    private fun randomFlagNameGet(textView: TextView) {
        db.collection("Flags").get().addOnSuccessListener { documents ->
            val randomFlag = documents.documents.random()
            val countryName = randomFlag.getString("flagname") ?: ""
            textView.text = countryName
        }.addOnFailureListener { exception ->
            Log.e("FlagQuizActivity", "Failed to fetch random flag name: ${exception.message}")
        }
    }

    fun checkAnswer(selectedTextView: TextView) {
        if (selectedTextView.text == correctCountry) {
            // Doğru cevap, yeni bir oyun başlat
            startGame(
                findViewById(R.id.flag_imageView),
                findViewById(R.id.SecenekA),
                findViewById(R.id.SecenekB),
                findViewById(R.id.SecenekC),
                findViewById(R.id.SecenekD)
            )
            wrongAttempts = 0
        } else {
            // Yanlış cevap
            wrongAttempts++
            if (wrongAttempts >= 3) {
                // Üç yanlış denemeden sonra oyunu bitir

                Log.d("FlagQuizActivity", "Oyunu kaybettiniz!")
                finish()
            } else {
                Log.d("FlagQuizActivity", "Yanlış cevap!")
            }
        }
    }
}
