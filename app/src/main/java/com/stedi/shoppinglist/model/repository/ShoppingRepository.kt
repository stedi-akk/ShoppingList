package com.stedi.shoppinglist.model.repository

import com.stedi.shoppinglist.model.ShoppingList

interface ShoppingRepository {
    @Throws(Exception::class)
    fun save(list: ShoppingList)

    @Throws(Exception::class)
    fun remove(list: ShoppingList)

    @Throws(Exception::class)
    fun getNonAchieved(): List<ShoppingList>

    @Throws(Exception::class)
    fun getAchieved(): List<ShoppingList>
}