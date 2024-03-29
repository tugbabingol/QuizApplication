package com.tugbabingol.quizapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Kullanıcı zaten oturum açmış, ana ekrana yönlendir
            startActivity(Intent(this, MainActivity::class.java))
            finish() // LoginActivity'yi kapat
        }

    }

    fun sign_up(view:View){
        val intent = Intent(this,SignUpActivity::class.java)
        startActivity(intent)
    }

    fun login(view: View){

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        auth.signInWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val guncelKullanici = auth.currentUser?.email.toString()
                Toast.makeText(this,"Hoşgeldin: ${guncelKullanici}", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception->
            Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }


    }

    fun get_data(){

    }
}