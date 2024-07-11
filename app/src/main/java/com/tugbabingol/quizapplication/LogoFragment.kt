package com.tugbabingol.quizapplication

import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlin.random.Random


class LogoFragment : Fragment() {


    private lateinit var db: FirebaseFirestore
    private lateinit var correctBrand: String
    private var wrongAttempts: Int = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()



    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logo, container, false)



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val logoImageView: ImageView = view.findViewById(R.id.logo_imageView)
        val SecenekA: TextView = view.findViewById(R.id.SecenekA)
        val SecenekB: TextView = view.findViewById(R.id.SecenekB)
        val SecenekC: TextView = view.findViewById(R.id.SecenekC)
        val SecenekD: TextView = view.findViewById(R.id.SecenekD)

        startGame(logoImageView,SecenekA,SecenekB,SecenekC,SecenekD)

        SecenekA.setOnClickListener{
            checkAnswer(SecenekA)
        }

        SecenekB.setOnClickListener{
            checkAnswer(SecenekB)
        }
        SecenekC.setOnClickListener{
            checkAnswer(SecenekC)
        }
        SecenekD.setOnClickListener{
            checkAnswer(SecenekD)
        }


    }

    private fun startGame(imageView: ImageView, SecenekA:TextView, SecenekB:TextView, SecenekC:TextView, SecenekD: TextView){
        db.collection("Logo").get().addOnSuccessListener { documents ->
            val randomLogo = documents.documents.random()
            val logoUrl = randomLogo.getString("logourl") ?: ""


            Picasso.get().load(logoUrl).resize(500,500).centerCrop()
                .into(imageView, object: Callback{
                    override fun onSuccess() {
                        Log.d("Picasso", "Image loaded successfully")
                    }

                    override fun onError(e: Exception) {
                        Log.e("Picasso", "Error loading image: $e")
                    }
                })


            correctBrand = randomLogo.getString("logoname") ?: ""

            val options = listOf(SecenekA, SecenekB, SecenekC, SecenekD)
            val randomIndex = Random.nextInt(0, options.size)
            options[randomIndex].text = correctBrand

            for (i in options.indices) {
                if (i != randomIndex) {
                    randomBrandNameGet(options[i])
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("LogoFragment","Failed to fetch brand information: ${exception.message}")
        }
    }


    private fun randomBrandNameGet(textView: TextView){
        db.collection("Logo").get().addOnSuccessListener { documents ->
            val randomLogo = documents.documents.random()
            val brandName = randomLogo.getString("logoname") ?: ""
            textView.text = brandName
        }.addOnFailureListener { exception ->
            Log.e("LogoFragment", "Failed to fetch random brand name: ${exception.message}")
        }
    }


    fun checkAnswer(selectedTextView: TextView){


        if (selectedTextView.text == correctBrand){

            startGame(
                requireView().findViewById(R.id.logo_imageView),
                requireView().findViewById(R.id.SecenekA),
                requireView().findViewById(R.id.SecenekB),
                requireView().findViewById(R.id.SecenekC),
                requireView().findViewById(R.id.SecenekD),
            )
            wrongAttempts = 0



        }else{
            wrongAttempts++
            if (wrongAttempts >= 3) {

                // Üç yanlış denemeden sonra oyunu bitir

                Log.d("FlagQuizActivity", "Oyunu kaybettiniz!")
                activity?.onBackPressed()
            }else {
                val heart3 = requireView().findViewById<ImageView>(R.id.broken_heart3)
                val heart2 = requireView().findViewById<ImageView>(R.id.broken_heart2)
                val heart1 = requireView().findViewById<ImageView>(R.id.broken_heart)
                when (wrongAttempts) {
                    1 -> heart3.visibility = View.INVISIBLE
                    2 -> heart2.visibility = View.INVISIBLE
                    3 -> heart1.visibility = View.INVISIBLE
                }
                Log.d("FlagQuizActivity", "Yanlış cevap!")

                startGame(
                    requireView().findViewById(R.id.logo_imageView),
                    requireView().findViewById(R.id.SecenekA),
                    requireView().findViewById(R.id.SecenekB),
                    requireView().findViewById(R.id.SecenekC),
                    requireView().findViewById(R.id.SecenekD),
                )
            }
        }
    }



}