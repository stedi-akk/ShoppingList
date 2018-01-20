package com.stedi.shoppinglist.model.repository

import com.stedi.shoppinglist.model.ShoppingList

interface ShoppingRepository {
    fun save(list: ShoppingList)

    fun remove(list: ShoppingList)

    fun getNonAchieved(): List<ShoppingList>

    fun getAchieved(): List<ShoppingList>
}