package com.example.mcc_analytics

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mcc_analytics.Adapter.ProductAdminAdapter
import com.example.mcc_analytics.modle.Products
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_products.*
import java.util.*

class MyProductsActivity : AppCompatActivity(), ProductAdminAdapter.onProductsItemClickListener {

    lateinit var db: FirebaseFirestore
    var categoryImage:String?=null
    private var progressDialog: ProgressDialog?=null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var startTimeTrack: Long = 0
    var endTimeTrack: Long = 0
    var totalTimeTrack: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        startTimeTrack = Calendar.getInstance().timeInMillis

        db = Firebase.firestore
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        trackScreen("product  My Screen")

        showDialog()
            categoryImage=intent.getStringExtra("catImage")
            val catName=intent.getStringExtra("catName")
            Picasso.get().load(categoryImage).into(category_image)
            name_of_category.text=catName
            getProductsToCategory("$catName")

        category_back_button.setOnClickListener {
            endTimeTrack = Calendar.getInstance().timeInMillis
            totalTimeTrack = endTimeTrack - startTimeTrack

            val minutes: Long = totalTimeTrack / 1000 / 60
            val seconds = (totalTimeTrack / 1000 % 60)
            timeSpendInScreen("$minutes m $seconds s","123456789","ProductActivity")

            var i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

    }


    private fun getProductsToCategory(catName:String){
        val dataProduct = mutableListOf<Products>()

        db.collection("products").whereEqualTo("categoryName",catName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {

                        val id = document.id
                        val data = document.data
                        val name = data["name"] as String?
                        val image = data["image"] as String?
                        val price = data["price"] as Double?
                        val description = data["description"] as String?
                        val categoryName = data["categoryName"] as String?
                        dataProduct.add(
                            Products(id,image,name,description,categoryName)
                        )
                    }

                    rv_product.layoutManager = GridLayoutManager(this, 2)
                    rv_product.setHasFixedSize(true)
                    val productAdapter = ProductAdminAdapter(this, dataProduct,this)
                    rv_product.adapter = productAdapter

                }
                hideDialog()
            }
    }

    private fun showDialog() {

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading Your Products ......")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog(){
        if(progressDialog!!.isShowing){
            progressDialog!!.dismiss()
        }
    }

    private fun trackScreen(screenName:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    private fun selectContent(id:String, name:String, contentType:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
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

    override fun onItemClick(data: Products, position: Int) {
        selectContent(data.id!!,data.name!!,data.image!!)

        endTimeTrack = Calendar.getInstance().timeInMillis
        totalTimeTrack = endTimeTrack - startTimeTrack

        val minutes: Long = totalTimeTrack / 1000 / 60
        val seconds = (totalTimeTrack / 1000 % 60)
        timeSpendInScreen("$minutes m $seconds s","hala123456","ProductActivity")

        var i = Intent(this,ProductMyDetails::class.java)
        i.putExtra("ids",data.id)
        i.putExtra("names",data.name)
        i.putExtra("images",data.image)
        i.putExtra("descriptions",data.description)
        i.putExtra("categorys",data.categoryName)
        startActivity(i)
    }
}