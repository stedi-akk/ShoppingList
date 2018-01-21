package com.stedi.shoppinglist.presenter

import com.stedi.shoppinglist.model.ShoppingList

interface EditListPresenter : RetainedPresenter<EditListPresenter.UIImpl> {

    fun newList(): ShoppingList

    fun save(list: ShoppingList)

    interface UIImpl : UI {
        fun onSaved()

        fun onFailedToSave()
    }
}