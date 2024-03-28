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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        val currentUser =auth.currentUser



    }

    fun register(view:View){
        val nameText = findViewById<EditText>(R.id.nameText)
        val emailText = findViewById<EditText>(R.id.emailText)
        val passwordText = findViewById<EditText>(R.id.passwordText)

        val name = nameText.text.toString()
        val email = emailText.text.toString().trim()
        val password = passwordText.text.toString().trim()
        val currentUser = auth.currentUser?.email.toString()

        //email ve şifreleri veritabanına kaydedilde
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
           if(task.isSuccessful){
               val intent = Intent(this, LoginActivity::class.java)
               startActivity(intent)
           }
        }.addOnFailureListener { exception ->
             Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()

        }

        val user = hashMapOf<String,Any>()
        user["fullname"] = name
        user["email"] = email

        database.collection("User").add(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    finish()
                } else {
                    Toast.makeText(this, "İşlem başarısız: " + task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "İşlem başarısız: " + exception.localizedMessage, Toast.LENGTH_LONG).show()
            }

    }

    fun sign_in(view:View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}