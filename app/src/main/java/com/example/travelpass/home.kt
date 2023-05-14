package com.example.travelpass


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpass.databinding.ActivityDetailsBinding
import com.example.travelpass.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream

class home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var storage:FirebaseStorage
    private lateinit var passengerAdapter: PassengerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentUser: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val uid = auth.currentUser?.uid
        if (uid == null) {
            // If the user is not authenticated, go to the login activity
            startActivity(Intent(this, Details::class.java))
            finish()
            return
        }

        //showing name on top
        database.getReference("passenger").child(uid).child("username")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val namea = snapshot.getValue(String::class.java)
                    binding.nameh.text = namea
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to read value.", error.toException())
                }

            })


        userRef = database.getReference("User").child(uid)

        binding.btnverify.setOnClickListener {
            val intent = Intent(this, Details::class.java)
            startActivity(intent)
        }




        //Recycle view part



        recyclerView = findViewById(R.id.mRec)
        passengerAdapter = PassengerAdapter()
        recyclerView.adapter = passengerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}





