package com.example.travelpass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var backTo :TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        backTo = findViewById(R.id.back)

        backTo.setOnClickListener {
            val intent = Intent(this,LoginPage::class.java)
            startActivity(intent)
        }

        val resetButton: Button = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            val email: String = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }


    }


    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }

}