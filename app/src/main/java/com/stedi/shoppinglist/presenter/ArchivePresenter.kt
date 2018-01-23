package com.stedi.shoppinglist.presenter

import com.stedi.shoppinglist.model.ShoppingList

interface ArchivePresenter : RetainedPresenter<ArchivePresenter.UIImpl> {
    fun fetchLists()

    interface UIImpl : UI {
        fun onLoaded(list: List<ShoppingList>)

        fun onFailedToLoad()
    }
}