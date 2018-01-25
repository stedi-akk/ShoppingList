package com.stedi.shoppinglist.model.repository

import com.stedi.shoppinglist.model.ShoppingList

// helpful in manual tests
// decorator pattern is used to wrap any kind of ShoppingRepository
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