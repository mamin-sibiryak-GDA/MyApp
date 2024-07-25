package com.example.myapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
        val movieName = findViewById<EditText>(R.id.editName)
        val movieGenre = findViewById<EditText>(R.id.editGenre)
        val movieDirector = findViewById<EditText>(R.id.editDirector)
        val movieCompany = findViewById<EditText>(R.id.editCompany)
        val button = findViewById<Button>(R.id.button)
        val TAG =
            this.javaClass.getSimpleName()
        button.setOnClickListener {
            val newMovie =
                Movie(
                    movieName.text.toString(),
                    movieGenre.text.toString(),
                    movieDirector.text.toString(),
                    movieCompany.text.toString()
                )
            Log.i(
                TAG,
                "" + newMovie
            )
            val intent = Intent()
            intent.putExtra("newItem", newMovie)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
