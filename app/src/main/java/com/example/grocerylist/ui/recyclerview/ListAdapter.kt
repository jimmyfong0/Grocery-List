package com.example.grocerylist.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylist.R
import com.example.grocerylist.models.Item

class ListAdapter(
    private val groceryList: List<Item>
): RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var item = itemView.findViewById<TextView>(R.id.itemName)
        var quantity = itemView.findViewById<TextView>(R.id.quantity)
        var description = itemView.findViewById<TextView>(R.id.desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_card, parent, false)
        return ListViewHolder(v)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.item.text = groceryList[position].name
        holder.quantity.text = groceryList[position].quantity.toString()
        holder.description.text = groceryList[position].description
    }

    override fun getItemCount(): Int {
        return groceryList.size
    }


}