package com.tugbabingol.quizapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
        //Firebase Storage referansı
        val storageRef = FirebaseStorage.getInstance().reference.child("flags")

        //Bayrak resimlerini listele
        storageRef.listAll().addOnSuccessListener { listResult ->
            val flags = listResult.items

            //Rastgele bir bayrak seç
            val randomflag = flags[Random.nextInt(0,flags.size)]
            //Seçilen bayrağı imageview e yükle
            randomflag.downloadUrl.addOnSuccessListener { uri ->
                //Picassso kütüphanesi ile resmi yükle
                Picasso.get().load(uri).into(imageView)
            }.addOnFailureListener { exception ->
                //Yükleme başarısız olursa hata mesajını döndür
                println("Bayrak Yükleme hatası: ${exception.message}")
            }
        }.addOnFailureListener { exception ->
            //Bayrakları listeleme başarısız olursa hata mesajını göster
            println("Bayrak listeleme hatası ${exception.message}")
        }

        //Bayrak adı ve zorluk seviyesi için veritabanından rastgele bir belge al
        db.collection("Flags").get().addOnSuccessListener { documents->
            val randomflag = documents.documents.random()
            val flagName = randomflag.getString("bayrakAdi")
            val difficultyLevel = randomflag.getString("zorlukSeviyesi")

            // Rastgele bayrak adını ve zorluk seviyesini rastgele şıklara yerleştir
            val options = listOf(secenekA, secenekB, secenekC, secenekD)
            val randomIndex = Random.nextInt(0, options.size)
            options[randomIndex].text = flagName

            // Geri kalan şıklara farklı bayrak adları yerleştir
            for (i in 0 until options.size) {
                if (i != randomIndex) {
                    options[i].text = randomFlagNameGet()
                }
            }
        }.addOnFailureListener { exception ->
            // Bayrak adı ve zorluk seviyesi alınırken hata olursa hata mesajını göster
            println("Bayrak bilgileri alınamadı: ${exception.message}")
        }

    }

    private fun randomFlagNameGet(): String {
        val bayrakAdlari = listOf("Bayrak 1", "Bayrak 2", "Bayrak 3", "Bayrak 4") // Firebase'deki bayrak adlarına göre güncelle
        return bayrakAdlari.random()
    }
}