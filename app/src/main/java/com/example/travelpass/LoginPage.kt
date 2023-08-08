package com.example.travelpass

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class LoginPage : AppCompatActivity() {
    lateinit var lemail:EditText
    lateinit var lpassword: EditText
    lateinit var btnlog : Button
    lateinit var reg : TextView
    lateinit var forget :  TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        auth = FirebaseAuth.getInstance()

        btnlog = findViewById(R.id.btnlog)
        lemail = findViewById(R.id.elog_email)
        lpassword = findViewById(R.id.elog_password)
        reg = findViewById(R.id.reg)
        forget = findViewById(R.id.forgetpass)

        btnlog.setOnClickListener {
            val email  = lemail.text.toString()
            val password = lpassword.text.toString()

            if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext,"email and password required",Toast.LENGTH_SHORT).show()
            }else{

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                    if (it.isSuccessful){

                        val sessionManager = SessionManager(this)
                        sessionManager.setLoggedIn(true)

                        val intent = Intent(this@LoginPage,home::class.java)
                        startActivity(intent)

                    }else{
                        Toast.makeText(applicationContext,"email or password incorrect",Toast.LENGTH_SHORT).show()
                    }
                }

            }


        }

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            prefs.edit().putString("uid", it).apply()
        }



        reg.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }


        forget.setOnClickListener {
            val intent = Intent(this,ForgotPassword::class.java)
            startActivity(intent)
        }


    }
}