package com.stedi.shoppinglist.presenter

import com.stedi.shoppinglist.model.ShoppingList

interface MainPresenter : RetainedPresenter<MainPresenter.UIImpl> {

    fun fetchLists()

    fun delete(list: ShoppingList)

    fun confirmDelete(list: ShoppingList)

    interface UIImpl : UI {
        fun onLoaded(list: List<ShoppingList>)

        fun onDeleted(list: ShoppingList)

        fun showConfirmDelete(list: ShoppingList)

        fun onFailedToLoad()

        fun onFailedToDelete(list: ShoppingList)
    }
}