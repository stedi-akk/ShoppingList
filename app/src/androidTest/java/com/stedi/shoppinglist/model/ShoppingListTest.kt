package com.stedi.shoppinglist.model

import android.os.Parcel
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShoppingListTest {
    @Test
    fun testParcelableSimple() {
        val list = ShoppingList()

        val parcel = Parcel.obtain()
        list.writeToParcel(parcel, 0)

        parcel.setDataPosition(0)

        val listFromParcel = ShoppingList.CREATOR.createFromParcel(parcel)
        assertEquals(list, listFromParcel)
    }

    @Test
    fun testParcelableComplex() {
        val items = listOf(ShoppingItem(1, "123", false), ShoppingItem(2, "name", true))
        val list = ShoppingList(1, 1L, items, false)

        val parcel = Parcel.obtain()
        list.writeToParcel(parcel, 0)

        parcel.setDataPosition(0)

        val listFromParcel = ShoppingList.CREATOR.createFromParcel(parcel)
        assertEquals(list, listFromParcel)
    }

    @Test
    fun testCopy() {
        var list = ShoppingList()

        var copy = list.copy()
        assertEquals(list, copy)

        val items = listOf(ShoppingItem(1, "123", false), ShoppingItem(2, "name", true))
        list = ShoppingList(1, 1L, items, false)

        copy = list.copy()
        assertEquals(list, copy)
    }
}