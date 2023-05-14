package com.example.travelpass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase

class SelectionPage : AppCompatActivity() {

    lateinit var getStart:Button
    lateinit var logi : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection_page)

        getStart = findViewById(R.id.button4)
        logi = findViewById(R.id.button3)


        getStart.setOnClickListener {
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
        }

        logi.setOnClickListener {
            val intent =Intent(this,LoginPage::class.java)
            startActivity(intent)
        }


    }
}