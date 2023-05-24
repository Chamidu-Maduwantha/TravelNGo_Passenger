package com.example.travelpass

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.util.*

class PassengerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val qrCodeImageView: ImageView = itemView.findViewById(R.id.image_view)
    private val storageRef = FirebaseStorage.getInstance().reference
    private val database = FirebaseDatabase.getInstance()
    private val click :Button =itemView.findViewById(R.id.btnview)
    private val act :Button =itemView.findViewById(R.id.active)
    private val tfrom:TextView = itemView.findViewById(R.id.tfrom)
    private val tto:TextView = itemView.findViewById(R.id.tto)
    private val typ:TextView = itemView.findViewById(R.id.type)
    fun bind(passengers: Passenger) {
        val qrCodeBitmap = generateQrCode(passengers)
        qrCodeImageView.setImageBitmap(qrCodeBitmap)


       /* val currentDate = Calendar.getInstance().time

        val expirationDate = Calendar.getInstance()
        expirationDate.time = passengers.createdDate
        expirationDate.add(Calendar.DAY_OF_MONTH, 30)*/

        val byteArrayOutputStream = ByteArrayOutputStream()
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

// Define the storage reference
        val storageRef = FirebaseStorage.getInstance().reference
        val qrCodeImageRef = storageRef.child("qr_codes/${passengers.userId}.jpg")

// Upload the byte array to Firebase Storage
        val uploadTask = qrCodeImageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // QR code image uploaded successfully
            Log.d(TAG, "QR code image uploaded successfully")

            // Retrieve the download URL for the image
            qrCodeImageRef.downloadUrl.addOnSuccessListener { uri ->
                // Load the QR code image using the downloaded URL
                Glide.with(itemView.context)
                    .load(uri)
                    .into(qrCodeImageView)
            }.addOnFailureListener { exception ->
                // Handle error in downloading QR code image
                Log.e(TAG, "Failed to retrieve download URL for QR code image", exception)
            }
        }.addOnFailureListener { exception ->
            // Handle error in uploading QR code image
            Log.e(TAG, "Failed to upload QR code image", exception)
        }



        val passengerRef = database.getReference("passenger").child(passengers.userId!!)
        passengerRef.child("status").get().addOnSuccessListener { snapshot ->
            val status = snapshot.value?.toString()
            if (status == "active") {
                // Set button color to a specific color when status is "active"
                act.setBackgroundColor(Color.GREEN)

            } else {
                // Set default button color when status is not "active"
                act.setBackgroundColor(Color.RED)
            }
        }.addOnFailureListener { exception ->
            // Handle error in retrieving status from Firebase Database
            Log.e(TAG, "Failed to retrieve status from Firebase Database", exception)
        }

        passengerRef.get().addOnSuccessListener { snapshot ->
            val from = snapshot.child("from").value?.toString()
            val to = snapshot.child("to").value?.toString()
            val type = snapshot.child("passType").value?.toString()

            // Display the name and NIC as desired (e.g., set text on TextViews)
            tfrom.text = from
            tto.text = to
            typ.text = type

        }.addOnFailureListener { exception ->
            // Handle error in retrieving data from Firebase Database
            Log.e(TAG, "Failed to retrieve data from Firebase Database", exception)
        }




        click.setOnClickListener {
            val intent = Intent(itemView.context, view_qr::class.java)
            itemView.context.startActivity(intent)
        }

        /*val qrCodeImageRef = storageRef.child("qr_codes/${passengers.userId}.jpg")
        qrCodeImageRef.downloadUrl.addOnSuccessListener { uri ->
            // Load the QR code image using the downloaded URL
            Glide.with(itemView.context)
                .load(uri)
                .into(qrCodeImageView)
        }.addOnFailureListener { exception ->
            // Handle error in downloading QR code image

        }*/







/*
        val isExpired = currentDate.after(expirationDate.time)

        if (isExpired) {
            // QR code has expired, handle accordingly (e.g., display expired message, hide QR code, etc.)
            val passengerRef = database.getReference("passenger").child(passengers.userId!!)
            passengerRef.child("status").setValue("expired")
        } else {
            // QR code is still valid, generate and display the QR code as before

        }*/
    }



    // this method generates the QR code bitmap using the passenger data
    private fun generateQrCode(passenger: Passenger): Bitmap {
        val qrCodeData = "${passenger.userId}"
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, 512, 512)
        val qrCodeBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                qrCodeBitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return qrCodeBitmap
    }
}