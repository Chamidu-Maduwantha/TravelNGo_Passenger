package com.example.travelpass

import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


class PassengerAdapter (private val passengers: MutableList<Passenger>): RecyclerView.Adapter<PassengerViewHolder>(){


    private val database = FirebaseDatabase.getInstance()


    init {
        // get a reference to the "passengers" node in Firebase Realtime Database
        val passengerRef = database.getReference("passenger")

        // add a ValueEventListener to fetch the data from Firebase
        passengerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the passengers list and populate it with the QR data of the current user
                passengers.clear()
                for (passengerSnapshot in snapshot.children) {
                    val passenger = passengerSnapshot.getValue(Passenger::class.java)
                    if (passenger?.userId == FirebaseAuth.getInstance().currentUser?.uid) {
                        passenger?.let { passengers.add(it) }
                    }
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // handle the error
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.qr_item, parent, false)
        return PassengerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
        val passenger = passengers[position]
        holder.bind(passenger)
    }

    override fun getItemCount(): Int {
        return passengers.size
    }


}