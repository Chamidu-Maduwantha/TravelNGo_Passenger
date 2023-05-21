package com.example.travelpass


import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpass.databinding.ActivityDetailsBinding
import com.example.travelpass.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
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
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navigationView: NavigationView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up the navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Handle navigation view item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle navigation view item clicks here
            when (menuItem.itemId) {

                R.id.logout -> {

                    auth.signOut()
                    // Redirect the user to the login activity or any desired destination
                    val intent = Intent(this, LoginPage::class.java)
                    startActivity(intent)
                    finish() // Op

                }

                R.id.profile ->{
                    val intent = Intent(this, Profile::class.java)
                    startActivity(intent)
                }


            }

            // Close the drawer after handling the item click
            drawerLayout.closeDrawers()

            true
        }









        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        recyclerView = findViewById(R.id.mRec)

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

        val statusRef = FirebaseDatabase.getInstance().getReference().child("passenger").child(uid).child("status")

        statusRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(String::class.java)

                val text1 = findViewById<TextView>(R.id.txt1)
                val text2 = findViewById<TextView>(R.id.txt2)
                val btncreate = findViewById<Button>(R.id.create)
                val bverify = findViewById<Button>(R.id.btnverify)
                val imag = findViewById<ImageView>(R.id.img)

                if (status == "active") {

                    text1.text = "Thank You"
                    text2.text = "Now You can use this QR as your travel pass"
                    imag.setImageResource(R.drawable.highwaybus)
                    bverify.visibility = View.GONE
                    btncreate.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if necessary
            }
        })

        userRef = database.getReference("User").child(uid)

        binding.btnverify.setOnClickListener {
            val intent = Intent(this, Details::class.java)
            startActivity(intent)
        }

        binding.create.setOnClickListener {
            val intent = Intent(this, Details::class.java)
            startActivity(intent)
        }


      binding.mRec.setOnClickListener{

          /*val viewQrDialog = ViewQrDialog(this)
          viewQrDialog.show()*/

          val intent = Intent(this, view_qr::class.java)
          startActivity(intent)

        }



        //Recycle view part






        val passengers = mutableListOf<Passenger>()


// Set up the RecyclerView with the PassengerAdapter
        val recyclerView: RecyclerView = findViewById(R.id.mRec)
        recyclerView.visibility = View.GONE
        val adapter = PassengerAdapter(passengers) // Replace `passengers` with your actual list of passengers
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)




        //set drawer icon

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout) // Replace `drawer_layout` with the actual ID of your DrawerLayout
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)


        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.black) // Replace `your_icon_color` with the desired color of the icon

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.drawer_icon) // Replace `your_icon` with the resource ID of your icon drawable
        }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }



    override fun onBackPressed() {
        // Disable back button functionality
        // Remove the super.onBackPressed() call
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
}





