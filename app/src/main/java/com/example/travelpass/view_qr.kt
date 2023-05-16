package com.example.travelpass

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.CalendarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import android.text.SpannableString
import android.widget.ImageView
import androidx.core.net.ParseException
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.prolificinteractive.materialcalendarview.MaterialCalendarView


class view_qr : AppCompatActivity() {

    private lateinit var selectedDates: MutableList<String>
    private lateinit var database: FirebaseDatabase
    private lateinit var datesRef: DatabaseReference
    private lateinit var qrCodeImageView: ImageView

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var databaseRef: DatabaseReference


    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_qr)



        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid!!
        val databaseRef = FirebaseDatabase.getInstance().getReference("passenger").child(uid)
            .child("Travel date")

        calendarView = findViewById(R.id.calendarView)

// Set listener to retrieve data from Firebase
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear existing events
                calendarView.removeDecorators()

                // Iterate through the database snapshot
                for (childSnapshot in snapshot.children) {
                    val dateStr = childSnapshot.getValue(String::class.java)

                    // Parse date string to CalendarDay object
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = dateStr?.let { format.parse(it) }
                    val calendar = Calendar.getInstance()
                    if (date != null) {
                        calendar.time = date
                    }

                    // Add event decorator to mark the date on the calendar
                    val decorator = EventDecorator(calendar.time, this@view_qr)
                    calendarView.addDecorator(decorator)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
            }
        })

        auth = FirebaseAuth.getInstance()
        qrCodeImageView = findViewById(R.id.img_view)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val passenger = getPassenger(userId)
            if (passenger != null) {
                val qrCodeBitmap = generateQrCode(passenger)
                qrCodeImageView.setImageBitmap(qrCodeBitmap)
            }
        }

    }

    private fun getPassenger(userId: String): Passenger? {
        val databaseRef = FirebaseDatabase.getInstance().getReference("passenger").child(userId)
        var passenger: Passenger? = null

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    passenger = dataSnapshot.getValue(Passenger::class.java)
                    if (passenger != null) {
                        val qrCodeBitmap = generateQrCode(passenger!!)
                        qrCodeImageView.setImageBitmap(qrCodeBitmap)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error in retrieving passenger details
            }
        })
        return passenger
    }

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

    companion object {
        private const val EXTRA_USER_ID = "extra_user_id"

        fun createIntent(context: Context, userId: String): Intent {
            val intent = Intent(context, view_qr::class.java)
            intent.putExtra(EXTRA_USER_ID, userId)
            return intent
        }
    }
}

