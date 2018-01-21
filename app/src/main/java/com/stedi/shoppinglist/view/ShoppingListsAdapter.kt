package com.stedi.shoppinglist.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.stedi.shoppinglist.model.ShoppingList

class ShoppingListsAdapter(private val listener: ClickListener) : RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder>() {
    private val list: MutableList<ShoppingList> = ArrayList()

    interface ClickListener {
        fun onListClicked()
    }

    fun set(list: List<ShoppingList>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TextView(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = "id=${list[position].id}"
    }

    override fun getItemCount() = list.size

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val textView: TextView = item as TextView
    }
}