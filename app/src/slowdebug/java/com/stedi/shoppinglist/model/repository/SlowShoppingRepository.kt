package com.stedi.shoppinglist.model.repository

import com.stedi.shoppinglist.model.ShoppingList

class SlowShoppingRepository(private val target: ShoppingRepository) : ShoppingRepository {
    override fun save(list: ShoppingList) {
        Thread.sleep(3000)
        target.save(list)
    }

    override fun remove(list: ShoppingList) {
        Thread.sleep(3000)
        target.remove(list)
    }

    override fun getNonAchieved(): List<ShoppingList> {
        Thread.sleep(3000)
        return target.getNonAchieved()
    }

    override fun getAchieved(): List<ShoppingList> {
        Thread.sleep(3000)
        return target.getAchieved()
    }
}