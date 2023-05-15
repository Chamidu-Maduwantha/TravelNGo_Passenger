package com.example.travelpass

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.travelpass.databinding.ActivityCheckoutBinding
import com.example.travelpass.databinding.ActivityDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject


class Details : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var btnfinish:Button
    private lateinit var database:FirebaseDatabase
    private lateinit var spinner1: Spinner
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var priceTextView: TextView
    private lateinit var btnback:Button

    var paymentsheet: PaymentSheet? = null
    var paymentIntentClientSecret: String? = null
    var configuration: PaymentSheet.CustomerConfiguration? = null


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_details)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        fetchApi()
        val price = intent.getDoubleExtra("price", 0.0)


        val paymentSheet = PaymentSheet(this) { paymentSheetResult ->
            onPaymentSheetResult(paymentSheetResult)
        }



        btnfinish = findViewById(R.id.btnfinish)
        btnback = findViewById(R.id.btnback)

        btnback.setOnClickListener {
            val intent = Intent(this,home::class.java)
            startActivity(intent)
        }

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



        database.getReference("passenger").child(uid).child("username").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uname = snapshot.getValue(String::class.java)
                binding.edtname.text = uname
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })


        database.getReference("passenger").child(uid).child("address").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val address = snapshot.getValue(String::class.java)
                binding.edtAddress.text = address
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })




        //+++++Medium+++++
        spinner1 = findViewById(R.id.dropmedium)

        // Create an ArrayAdapter using a string array and a default spinner layout
        val adapterMed = ArrayAdapter.createFromResource(
            this,
            R.array.dropdown_items, // Array resource containing the items
            android.R.layout.simple_spinner_item
        )

        // Specify the layout to use when the dropdown list appears
        adapterMed.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinner1.adapter = adapterMed


        //+++From++++
        spinnerFrom = findViewById(R.id.dropFrom)

        // Create an ArrayAdapter using a string array and a default spinner layout
        val adapterFrom = ArrayAdapter.createFromResource(
            this,
            R.array.from_items, // Array resource containing the items
            android.R.layout.simple_spinner_item
        )

        // Specify the layout to use when the dropdown list appears
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinnerFrom.adapter = adapterFrom




        //+++++To++++++
        spinnerTo = findViewById(R.id.dropTo)

        // Create an ArrayAdapter using a string array and a default spinner layout
        val adapterTo = ArrayAdapter.createFromResource(
            this,
            R.array.to_items, // Array resource containing the items
            android.R.layout.simple_spinner_item
        )

        // Specify the layout to use when the dropdown list appears
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        spinnerTo.adapter = adapterTo

        spinner1.onItemSelectedListener = spinnerItemSelectedListener
        spinnerFrom.onItemSelectedListener = spinnerItemSelectedListener
        spinnerTo.onItemSelectedListener = spinnerItemSelectedListener

        btnfinish.setOnClickListener {
            // Get the selected values from the spinners
            val selectedMedium = spinner1.selectedItem.toString()
            val selectedFrom = spinnerFrom.selectedItem.toString()
            val selectedTo = spinnerTo.selectedItem.toString()

            // Update the values in the Firebase Realtime Database
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.uid?.let { userId ->
                val database = FirebaseDatabase.getInstance()
                val userRef = database.reference.child("passenger").child(userId)
                userRef.child("pass type").setValue(selectedMedium)
                userRef.child("from").setValue(selectedFrom)
                userRef.child("to").setValue(selectedTo)
            }


            paymentIntentClientSecret?.let { it1 -> paymentSheet.presentWithPaymentIntent(it1, PaymentSheet.Configuration("TravelNGo", configuration)) }
        }




    }


    private val spinnerItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            updateTotalPrice()
            val selectedItem = parent?.getItemAtPosition(position).toString()
            btnfinish.isEnabled = !selectedItem.isEmpty()
            /*val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.uid?.let { userId ->
                // Store the selected item for the current user in Firebase Realtime Database
                val database = FirebaseDatabase.getInstance()
                val userRef = database.reference.child("passenger").child(userId)
                when (parent?.id) {
                    R.id.dropmedium -> userRef.child("pass type").setValue(selectedItem)
                    R.id.dropFrom -> userRef.child("from").setValue(selectedItem)
                    R.id.dropTo -> userRef.child("to").setValue(selectedItem)
                    else -> {
                        throw IllegalArgumentException("Invalid view id: ${parent?.id}")
                    }
                }.addOnSuccessListener {
                    // Update successful
                    //Toast.makeText(applicationContext, "Item updated in database", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    // Error occurred while updating
                    //Toast.makeText(applicationContext, "Failed to update item in database", Toast.LENGTH_SHORT).show()
                }
            }*/

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            btnfinish.isEnabled = false
        }

    }



        private fun calculatePrice(medium: String, from: String, to: String): Double {
        return when (medium) {
            "Bus" -> calculateBusPrice(from, to)
            "Train" -> calculateTrainPrice(from, to)
            else -> 0.0
        }
    }

    private fun calculateBusPrice(from: String, to: String): Double {
        return when {
            from == "Colombo" && to == "Matara" || from == "Matara" && to == "Colombo" -> 550.00
            from == "Colombo" && to == "Polonnaruwa" || from == "Polonnaruwa" && to == "Colombo" -> 750.00
            else -> 0.0
        }
    }

    private fun calculateTrainPrice(from: String, to: String): Double {
        return when {
            from == "Colombo" && to == "Matara" || from == "Matara" && to == "Colombo" -> 300.00
            from == "Colombo" && to == "Polonnaruwa" || from == "Polonnaruwa" && to == "Colombo" -> 400.00

            else -> 0.0
        }
    }

    private fun updateTotalPrice() {
        val selectedMedium = spinner1.selectedItem.toString()
        val selectedFrom = spinnerFrom.selectedItem.toString()
        val selectedTo = spinnerTo.selectedItem.toString()

        val price = calculatePrice(selectedMedium, selectedFrom, selectedTo)
        binding.edtprice.text = " $price 0"
    }



    //Checkout

    private fun fetchApi(){
        val queue = Volley.newRequestQueue(this)
        val url = "https://travelpayment.000webhostapp.com/"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->

                val jsonObject = JSONObject(response)
                configuration = PaymentSheet.CustomerConfiguration(
                    jsonObject.getString("customer"),
                    jsonObject.getString("ephemeralKey")
                )
                paymentIntentClientSecret = jsonObject.getString("paymentIntent");
                PaymentConfiguration.init(getApplicationContext(), jsonObject.getString("publishableKey"));
            },
            Response.ErrorListener { error ->

            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["param"] = "abc"
                return params
            }
        }

        queue.add(stringRequest)
    }


    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()

                // Redirect to the home page
                val intent = Intent(this, home::class.java)
                startActivity(intent)
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(this, paymentSheetResult.error.message, Toast.LENGTH_SHORT).show()

                // Redirect to the home page
                val intent = Intent(this, home::class.java)
                startActivity(intent)
            }
            is PaymentSheetResult.Completed -> {
                Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()

                // Update Firebase Realtime Database status as active

                // Update Firebase Realtime Database status as active
                val databaseRef = FirebaseDatabase.getInstance().reference
                val uid = auth.currentUser?.uid!!
                val statusRef = databaseRef.child("passenger").child(uid).child("status")
                statusRef.setValue("active")

                // Redirect to the home page
                // Redirect to the home page
                val intent = Intent(this, home::class.java)
                startActivity(intent)

            }
        }
    }

}