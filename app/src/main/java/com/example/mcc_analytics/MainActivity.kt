package com.example.mcc_analytics

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.googleanalyticsassignment.Adapter.CategoriesAdapter
import com.example.googleanalyticsassignment.modle.Category
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() , CategoriesAdapter.onCategoryItemClickListener {

    lateinit var db: FirebaseFirestore
    private var progressDialog: ProgressDialog?=null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    var startTimeTrack: Long = 0
    var endTimeTrack: Long = 0
    var totalTimeTrack: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startTimeTrack = Calendar.getInstance().timeInMillis

        db = Firebase.firestore
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        trackScreen("my category screen")

        showDialog()
        getAllCategoriesInMyScreen()
    }

    override fun onItemClick(data: Category, position: Int) {
        selectContent(data.id,data.nameCategoryitem!!,data.imageCategoryitem!!)
        endTimeTrack = Calendar.getInstance().timeInMillis
        totalTimeTrack = endTimeTrack - startTimeTrack

        val minutes: Long = totalTimeTrack / 1000 / 60
        val seconds = (totalTimeTrack / 1000 % 60)
        timeSpendInScreen("$minutes m $seconds s","123456789","MainActivity")


        var i = Intent(this,MyProductsActivity::class.java)
        i.putExtra("id",data.id)
        i.putExtra("catImage",data.imageCategoryitem)
        i.putExtra("catName",data.nameCategoryitem)
        startActivity(i)

    }

    private fun getAllCategoriesInMyScreen(){
        val categoryList= mutableListOf<Category>()
        db.collection("categories")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val id = document.id
                        val data = document.data
                        val myCategoryName = data["category_name"] as String?
                        val myCategoryImage = data["category_image"] as String?
                        categoryList.add(Category(id, myCategoryImage, myCategoryName))
                    }
                    rv_category_item?.layoutManager =
                        LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    rv_category_item.setHasFixedSize(true)
                    val categoriesAdapter = CategoriesAdapter(this, categoryList, this)
                    rv_category_item.adapter = categoriesAdapter
                }
                hideDialog()
            }
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading Your Categories .....")
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
                Log.e("sohad","time Successfully")
            }
            .addOnFailureListener {exception ->
                Log.e("sohad", exception.message.toString())
            }
    }
}