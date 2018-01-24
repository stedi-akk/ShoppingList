package com.stedi.shoppinglist.presenter.interfaces

import com.stedi.shoppinglist.model.ShoppingList

interface ArchivePresenter : RetainedPresenter<ArchivePresenter.UIImpl> {
    fun fetchLists()

    fun clear(list: List<ShoppingList>)

    interface UIImpl : UI {
        fun onLoaded(list: List<ShoppingList>)

        fun onCleared()

        fun onFailedToLoad()

        fun onFailedToClear()
    }
}