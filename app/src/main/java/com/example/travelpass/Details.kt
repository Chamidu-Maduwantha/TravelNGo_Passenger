package com.example.travelpass

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.example.travelpass.databinding.ActivityDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.core.Context

class Details : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var btnfinish:Button
    private lateinit var database:FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_details)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnfinish = findViewById(R.id.btnfinish)

        auth = FirebaseAuth.getInstance()
        val uid =auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("passenger")

        database = FirebaseDatabase.getInstance()

        if (uid == null) {
            // If the user is not authenticated, go to the login activity
            startActivity(Intent(this, Details::class.java))
            finish()
            return
        }

        database.getReference("passenger").child(uid).child("nic").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nic = snapshot.getValue(String::class.java)
                binding.edtnic.text = nic
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })



        database.getReference("user").child(uid).child("username").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uname = snapshot.getValue(String::class.java)
                binding.edtname.text = uname
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })


        database.getReference("user").child(uid).child("address").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val address = snapshot.getValue(String::class.java)
                binding.edtAddress.text = address
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })


        /*btnfinish.setOnClickListener {

            val NIC = binding.edtnic.text.toString()
            val birthday = binding.edtbirth.text.toString()
            val address = binding.edtAddress.text.toString()

            val user = User(NIC,birthday,address)
            if(uid != null){

                databaseReference.child(uid).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful){

                        val intent = Intent(this@Details, home::class.java)
                        startActivity(intent)

                        Toast.makeText(this@Details,"Successful",Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(this@Details,"Failed to store data",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }*/


        /*// After the user submits the form, store their details in the database
        val uid = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("uid", "")
        FirebaseDatabase.getInstance().getReference("users").child(uid).setValue(Details)*/


    }
}