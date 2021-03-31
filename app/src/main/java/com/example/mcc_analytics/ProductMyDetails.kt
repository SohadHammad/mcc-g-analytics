package com.example.mcc_analytics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product_details.*
import java.util.*

class ProductMyDetails : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var startTimeTrack: Long = 0
    var endTimeTrack: Long = 0
    var totalTimeTrack: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)


        startTimeTrack = Calendar.getInstance().timeInMillis

        db= Firebase.firestore
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        trackScreen("product  my details screen")

            val names = intent.getStringExtra("names")
            val images = intent.getStringExtra("images")
            val prices = intent.getDoubleExtra("prices",0.0)
            val descriptions = intent.getStringExtra("descriptions")

            Picasso.get().load(images).into(product_image)
            my_product_name.text = names
            my_product_price.text = prices.toString()
            my_product_description.text = descriptions


        back_to_products_button.setOnClickListener {
            endTimeTrack = Calendar.getInstance().timeInMillis
            totalTimeTrack = endTimeTrack - startTimeTrack

            val minutes: Long = totalTimeTrack / 1000 / 60
            val seconds = (totalTimeTrack / 1000 % 60)
            timeSpendInScreen("$minutes m $seconds s","123456789","ProductDetails")

            onBackPressed()
        }
}

    private fun trackScreen(screenName:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun timeSpendInScreen(time: String, userId:String, pageName:String){

        val time= hashMapOf("time" to time,"userId" to userId,"pageName" to pageName)
        db.collection("Time")
                .add(time)
                .addOnSuccessListener {documentReference ->
                    Log.e("sohad","time added successfully")
                }
                .addOnFailureListener {exception ->
                    Log.e("sohad", exception.message.toString())
                }
    }
}