package com.example.travelpass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class PasswordChange : AppCompatActivity() {

    private lateinit var editTextCurrentPassword: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var buttonChangePassword: Button
    private  lateinit var btnback:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change)

        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        buttonChangePassword = findViewById(R.id.buttonChangePassword)
        btnback = findViewById(R.id.btnback)

        buttonChangePassword.setOnClickListener {
            val currentPassword = editTextCurrentPassword.text.toString()
            val newPassword = editTextNewPassword.text.toString()
            changePassword(currentPassword, newPassword)
        }

        btnback.setOnClickListener {
            val intent = Intent(this,home::class.java)
            startActivity(intent)
        }



    }


    private fun changePassword(currentPassword: String, newPassword: String) {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)

        user?.reauthenticate(credential)
            ?.addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { passwordUpdateTask ->
                            if (passwordUpdateTask.isSuccessful) {
                                // Password has been successfully updated
                                val dialogFragment = PasswordChangeDialogFragment.newInstance()
                                dialogFragment.show(supportFragmentManager, "PasswordChangeDialog")

                                /*val intent = Intent(this@PasswordChange, home::class.java)
                                startActivity(intent)
                                finish() */
                            } else {
                                // Password update failed, show an error message
                                // You can handle the error appropriately
                            }
                        }
                } else {
                    // Reauthentication failed, show an error message
                    // You can handle the error appropriately
                }
            }
    }


}