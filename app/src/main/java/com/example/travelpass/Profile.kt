package com.example.travelpass

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Profile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid


        val name = findViewById<TextView>(R.id.p_name)
        val nic = findViewById<TextView>(R.id.p_nic)

        val passengerRef = database.getReference("passenger").child(userId!!)
        passengerRef.get().addOnSuccessListener { snapshot ->
            val from = snapshot.child("username").value?.toString()
            val to = snapshot.child("nic").value?.toString()


            // Display the name and NIC as desired (e.g., set text on TextViews)
            name.text = from
            nic.text = to


        }.addOnFailureListener { exception ->
            // Handle error in retrieving data from Firebase Database
            Log.e(ContentValues.TAG, "Failed to retrieve data from Firebase Database", exception)
        }


        val reset = findViewById<TextView>(R.id.password_change)
        val logout = findViewById<TextView>(R.id.logout)
        val delete = findViewById<TextView>(R.id.delete)

        reset.setOnClickListener{
            val intent = Intent(this,PasswordChange::class.java)
            startActivity(intent)
        }

        logout.setOnClickListener {

            auth.signOut()
            // Redirect the user to the login activity or any desired destination
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()

        }

        delete.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("passenger").child(user?.uid!!)

            if (user != null) {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Delete Account")
                alertDialogBuilder.setMessage("Are you sure you want to delete your account?")
                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    // User confirmed, delete the account and associated data
                    userRef.removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // User details removal successful
                                user.delete()
                                    .addOnCompleteListener { authTask ->
                                        if (authTask.isSuccessful) {
                                            // Account deletion successful
                                            // Add your code here to handle the successful deletion
                                            // Redirect the user to the login page
                                            startActivity(Intent(this, SelectionPage::class.java))
                                            finish() // Optional: Close the current activity
                                        } else {
                                            // Account deletion failed
                                            val errorMessage = authTask.exception?.message
                                            // Display or log the error message
                                        }
                                    }
                            } else {
                                // User details removal failed
                                val errorMessage = task.exception?.message
                                // Display or log the error message
                            }
                        }
                }
                alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                    // User cancelled, do nothing
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()

            }
        }

    }
}