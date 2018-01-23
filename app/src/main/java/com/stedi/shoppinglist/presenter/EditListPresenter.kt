package com.stedi.shoppinglist.presenter

import com.stedi.shoppinglist.model.ShoppingList

interface EditListPresenter : RetainedPresenter<EditListPresenter.UIImpl> {
    fun newList(): ShoppingList

    fun save(list: ShoppingList, checkIfItemsAchieved: Boolean = true)

    fun saveAsAchieved(list: ShoppingList)

    interface UIImpl : UI {
        fun onSaved(list: ShoppingList)

        fun showErrorEmptyList()

        fun showSaveAsAchieved(list: ShoppingList)

        fun onFailedToSave(list: ShoppingList)
    }
}