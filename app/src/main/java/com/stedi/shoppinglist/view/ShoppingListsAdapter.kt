package com.stedi.shoppinglist.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.stedi.shoppinglist.R
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.other.asDateFormat

class ShoppingListsAdapter(private val inflater: LayoutInflater,
                           private val listener: ClickListener,
                           private val extraFunctions: Boolean = true) : RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder>() {

    private val list: MutableList<ShoppingList> = ArrayList()

    init {
        setHasStableIds(true)
    }

    interface ClickListener {
        fun onListClicked(list: ShoppingList)

        fun onDeleteClicked(list: ShoppingList)

        fun onBoughtClicked(list: ShoppingList)
    }

    fun set(list: List<ShoppingList>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.shopping_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.shoppingList = list[position]

        holder.tvModified.text = list[position].modified.asDateFormat()

        holder.itemsContainer.removeAllViews()
        for (item in list[position].items) {
            val itemView = inflater.inflate(R.layout.shopping_item_simple, holder.itemsContainer, false)
            itemView.findViewById<CheckBox>(R.id.shopping_item_simple_cb).isChecked = item.achieved
            itemView.findViewById<TextView>(R.id.shopping_item_simple_tv).text = item.name
            holder.itemsContainer.addView(itemView)
        }

        holder.itemView.setOnClickListener(holder)

        if (extraFunctions) {
            holder.btnDelete.setOnClickListener(holder)
            holder.btnDone.setOnClickListener(holder)
        } else {
            holder.btnDelete.visibility = View.INVISIBLE
            holder.btnDone.visibility = View.GONE
        }
    }

    override fun getItemId(position: Int) = list[position].id.toLong()

    override fun getItemCount() = list.size

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item), View.OnClickListener {
        @BindView(R.id.shopping_list_item_btn_delete)
        lateinit var btnDelete: View

        @BindView(R.id.shopping_list_item_btn_bought)
        lateinit var btnDone: View

        @BindView(R.id.shopping_list_item_tv_modified)
        lateinit var tvModified: TextView

        @BindView(R.id.shopping_list_item_items_container)
        lateinit var itemsContainer: ViewGroup

        var shoppingList: ShoppingList? = null

        init {
            ButterKnife.bind(this, item)
        }

        override fun onClick(v: View) {
            val shoppingList = shoppingList ?: return
            when (v) {
                itemView -> listener.onListClicked(shoppingList)
                btnDelete -> listener.onDeleteClicked(shoppingList)
                btnDone -> listener.onBoughtClicked(shoppingList)
            }
        }
    }
}