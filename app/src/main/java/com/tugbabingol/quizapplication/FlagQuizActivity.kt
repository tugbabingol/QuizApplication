package com.tugbabingol.quizapplication


import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlin.random.Random

class FlagQuizActivity : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore

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
        val SecenekA = findViewById<TextView>(R.id.SecenekA)
        val SecenekB = findViewById<TextView>(R.id.SecenekB)
        val SecenekC = findViewById<TextView>(R.id.SecenekC)
        val SecenekD = findViewById<TextView>(R.id.SecenekD)

        bayrakBilgileriniGetir(imageFlag,SecenekA,SecenekB,SecenekC,SecenekD)

    }


    fun bayrakBilgileriniGetir(imageView: ImageView, secenekA: TextView, secenekB: TextView, secenekC: TextView, secenekD: TextView){
        db.collection("Flags").get().addOnSuccessListener { documents->
            val randomflag = documents.documents.random()
            val countryName = randomflag.getString("flagname")
            val flagUrl = randomflag.get("flagurl") as String





            Picasso.get().load(flagUrl).resize(700,700).centerCrop() // crop the image to fit the view
                .into(imageView, object : Callback {
                    override fun onSuccess(){
                        println("Başarılı")
                    }
                    override fun onError(e: Exception) {
                        // Error loading image
                        Log.e("Picasso", "Error loading image: $e")
                    }
                })


            // Rastgele bayrak adını rastgele şıklara yerleştir
            val options = listOf(secenekA, secenekB, secenekC, secenekD)
            val randomIndex = Random.nextInt(0, options.size)
            options[randomIndex].text = countryName

            for (i in 0 until options.size) {
                if (i != randomIndex) {
                    randomFlagNameGet(options[i])
                }
            }

        }.addOnFailureListener { exception ->
            // Bayrak adı alınırken hata olursa hata mesajını göster
            println("Bayrak bilgileri alınamadı: ${exception.message}")
        }

    }

    private fun randomFlagNameGet(textView: TextView) {

        db.collection("Flags").get().addOnSuccessListener { documents ->
            val randomFlag = documents.documents.random()
            val countryName = randomFlag.getString("flagname")
            textView.text = countryName
        }.addOnFailureListener { exception ->
            Log.e("FlagQuizActivity", "Rastgele bayrak adı alınamadı: ${exception.message}")
        }
    }
}