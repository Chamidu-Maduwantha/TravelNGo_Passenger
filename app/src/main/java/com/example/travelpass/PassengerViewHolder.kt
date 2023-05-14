package com.example.travelpass

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class PassengerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val qrCodeImageView: ImageView = itemView.findViewById(R.id.image_view)


    fun bind(passenger: Passenger) {
        val qrCodeBitmap = generateQrCode(passenger)
        qrCodeImageView.setImageBitmap(qrCodeBitmap)
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