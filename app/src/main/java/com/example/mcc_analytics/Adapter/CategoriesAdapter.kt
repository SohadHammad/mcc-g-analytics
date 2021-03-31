package com.example.googleanalyticsassignment.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.googleanalyticsassignment.modle.Category
import com.example.mcc_analytics.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.categories_item.view.*

class CategoriesAdapter(var activity: Context?, var data: MutableList<Category>, var clickListener: onCategoryItemClickListener): RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageCategory  =itemView.categoryImage
        val nameCategory=itemView.categoryName

        fun initialize(data: Category, action:onCategoryItemClickListener){
           // imageCategory.setImageURI(Uri.parse(data.imageCategory))
            Picasso.get().load(data.imageCategoryitem).into(imageCategory)

            nameCategory.text = data.nameCategoryitem

            itemView.setOnClickListener {
                action.onItemClick(data,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesAdapter.MyViewHolder {
        var View: View = LayoutInflater.from(activity).inflate(R.layout.categories_item,parent,false)
        val myHolder:MyViewHolder = MyViewHolder(View)
        return myHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.initialize(data.get(position), clickListener)
    }
    interface onCategoryItemClickListener{
        fun onItemClick(data:Category, position: Int)
    }
}