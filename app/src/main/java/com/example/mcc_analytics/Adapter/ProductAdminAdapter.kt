package com.example.mcc_analytics.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mcc_analytics.modle.Products
import com.example.mcc_analytics.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.products_item_admin.view.*

class ProductAdminAdapter (var activity: Context?, var data: MutableList<Products>, var clickListener: ProductAdminAdapter.onProductsItemClickListener) : RecyclerView.Adapter<ProductAdminAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val productImage  =itemView.product_photo
        val name=itemView.my_product_name
        val price=itemView.my_product_price

        fun initialize(data: Products, action:onProductsItemClickListener){

            Picasso.get().load(data.image).into(productImage)
            name.text = data.name
           // price.text = data.price.toString()

            itemView.setOnClickListener {
                action.onItemClick(data,adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdminAdapter.MyViewHolder {
        var View: View = LayoutInflater.from(activity).inflate(R.layout.products_item_admin,parent,false)
        val myHolder:MyViewHolder = MyViewHolder(View)
        return myHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ProductAdminAdapter.MyViewHolder, position: Int) {
        holder.initialize(data.get(position), clickListener)
    }
    interface onProductsItemClickListener{
        fun onItemClick(data:Products, position: Int)
    }
}