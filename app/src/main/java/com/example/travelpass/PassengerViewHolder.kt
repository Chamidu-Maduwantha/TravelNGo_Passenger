package com.example.travelpass

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.ImageView
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

    fun bind(passengers: Passenger) {
        val qrCodeBitmap = generateQrCode(passengers)
        qrCodeImageView.setImageBitmap(qrCodeBitmap)


        /*val currentDate = Calendar.getInstance().time

        val expirationDate = Calendar.getInstance()
        expirationDate.time = passengers.createdDate
        expirationDate.add(Calendar.DAY_OF_MONTH, 30)*/


        val qrCodeImageRef = storageRef.child("qr_codes/${passengers.userId}.jpg")
        qrCodeImageRef.downloadUrl.addOnSuccessListener { uri ->
            // Load the QR code image using the downloaded URL
            Glide.with(itemView.context)
                .load(uri)
                .into(qrCodeImageView)
        }.addOnFailureListener { exception ->
            // Handle error in downloading QR code image

        }







        /*val isExpired = currentDate.after(expirationDate.time)

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