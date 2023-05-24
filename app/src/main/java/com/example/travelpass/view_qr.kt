package com.example.travelpass


import android.content.ContentValues
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class view_qr : AppCompatActivity() {

    private lateinit var qrCodeImageView: ImageView
    private lateinit var calendarView: MaterialCalendarView


    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_qr)


        FirebaseApp.initializeApp(this)

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
        /*if (userId != null) {
            val passenger = getPassenger(userId)
            if (passenger != null) {
                val qrCodeBitmap = generateQrCode(passenger)
                qrCodeImageView.setImageBitmap(qrCodeBitmap)
            }
        }*/









        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("/qr_codes/$userId.jpg")

        val imageView: ImageView = findViewById(R.id.img_view)

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl: String = uri.toString()
            Picasso.get()
                .load(imageUrl)
                .into(imageView)
        }.addOnFailureListener { exception -> }



        val tfrom = findViewById<TextView>(R.id.From)
        val tto = findViewById<TextView>(R.id.To)
        val typ = findViewById<TextView>(R.id.pass)
        val act = findViewById<Button>(R.id.active)

        val database = FirebaseDatabase.getInstance()

        val passengerRef = database.getReference("passenger").child(userId!!)

        passengerRef.child("status").get().addOnSuccessListener { snapshot ->
            val status = snapshot.value?.toString()
            if (status == "active") {
                // Set button color to a specific color when status is "active"
                act.setBackgroundColor(Color.GREEN)
                act.setText("active")

            } else {
                // Set default button color when status is not "active"
                act.setBackgroundColor(Color.RED)
            }
        }.addOnFailureListener { exception ->
            // Handle error in retrieving status from Firebase Database
            Log.e(ContentValues.TAG, "Failed to retrieve status from Firebase Database", exception)
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
            Log.e(ContentValues.TAG, "Failed to retrieve data from Firebase Database", exception)
        }




    }

   /* private fun getPassenger(userId: String): Passenger? {
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
    }*/



    companion object {
        private const val EXTRA_USER_ID = "extra_user_id"


        /*fun createIntent(context: Context, userId: String): Intent {
            val intent = Intent(context, view_qr::class.java)
            intent.putExtra(EXTRA_USER_ID, userId)
            return intent
        }*/







    }
}

