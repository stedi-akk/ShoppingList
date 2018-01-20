package com.stedi.shoppinglist.model.repository

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.stedi.shoppinglist.TestConstants.TEST_DATABASE_NAME
import com.stedi.shoppinglist.model.ShoppingItem
import com.stedi.shoppinglist.model.ShoppingList
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseShoppingRepositoryTest {
    private lateinit var repository: DatabaseShoppingRepository

    @Before
    fun before() {
        repository = DatabaseShoppingRepository(InstrumentationRegistry.getTargetContext(), TEST_DATABASE_NAME, 1)
    }

    @After
    fun after() {
        if (InstrumentationRegistry.getTargetContext().deleteDatabase(TEST_DATABASE_NAME)) {
            println("$TEST_DATABASE_NAME database successfully deleted")
        } else {
            println("failed to delete $TEST_DATABASE_NAME")
        }
    }

    @Test
    fun testEmptyShoppingList() {
        // initially empty db
        assertTrue(repository.getAchieved().isEmpty())
        assertTrue(repository.getNonAchieved().isEmpty())

        // save new empty shopping list
        val emptyShoppingList = ShoppingList()
        repository.save(emptyShoppingList)
        assertTrue(emptyShoppingList.id == 1)
        assertTrue(repository.getAchieved().isEmpty())

        // check if saved list is the same
        var databaseList = repository.getNonAchieved()
        assertTrue(databaseList.size == 1)
        assertTrue(emptyShoppingList.id == databaseList[0].id)
        assertTrue(emptyShoppingList.modified == databaseList[0].modified)
        assertFalse(databaseList[0].achieved)
        assertTrue(databaseList[0].items.isEmpty())

        // modify the list and save again
        emptyShoppingList.modified = 1
        repository.save(emptyShoppingList)
        assertTrue(emptyShoppingList.id == 1)
        assertTrue(emptyShoppingList.modified == 1.toLong())
        assertTrue(repository.getAchieved().isEmpty())

        // check if db updated successfully
        databaseList = repository.getNonAchieved()
        assertTrue(databaseList.size == 1)
        assertTrue(emptyShoppingList.id == databaseList[0].id)
        assertTrue(emptyShoppingList.modified == databaseList[0].modified)
        assertFalse(databaseList[0].achieved)
        assertTrue(databaseList[0].items.isEmpty())

        // move the list into the 'achieved' state and save
        emptyShoppingList.achieved = true
        repository.save(emptyShoppingList)
        assertTrue(emptyShoppingList.id == 1)
        assertTrue(repository.getNonAchieved().isEmpty())

        // check if db updated successfully
        databaseList = repository.getAchieved()
        assertTrue(databaseList.size == 1)
        assertTrue(emptyShoppingList.id == databaseList[0].id)
        assertTrue(emptyShoppingList.modified == databaseList[0].modified)
        assertTrue(databaseList[0].achieved)
        assertTrue(databaseList[0].items.isEmpty())

        // remove it
        repository.remove(emptyShoppingList)

        // check if db updated successfully
        assertTrue(repository.getAchieved().isEmpty())
        assertTrue(repository.getNonAchieved().isEmpty())
    }

    @Test
    fun testNonEmptyShoppingList() {
        // initially empty db
        assertTrue(repository.getAchieved().isEmpty())
        assertTrue(repository.getNonAchieved().isEmpty())

        // save new shopping list
        val items = mutableListOf(ShoppingItem(name = "non achieved item", achieved = false), ShoppingItem(name = "achieved item", achieved = true))
        val shoppingList = ShoppingList(items = items)
        repository.save(shoppingList)
        assertTrue(shoppingList.id == 1)
        assertTrue(repository.getAchieved().isEmpty())

        // check if saved list is the same
        var databaseList = repository.getNonAchieved()
        assertTrue(databaseList.size == 1)
        assertTrue(shoppingList.id == databaseList[0].id)
        assertTrue(shoppingList.modified == databaseList[0].modified)
        assertFalse(databaseList[0].achieved)
        var listItems = databaseList[0].items.toList()
        assertTrue(listItems.size == 2)
        assertTrue(listItems[0].name == "non achieved item")
        assertFalse(listItems[0].achieved)
        assertTrue(listItems[1].name == "achieved item")
        assertTrue(listItems[1].achieved)

        // modify the list and save again
        items[0].achieved = true
        items[1].name = "changed"
        repository.save(shoppingList)
        assertTrue(shoppingList.id == 1)
        assertTrue(repository.getAchieved().isEmpty())

        // check if saved list is the same
        databaseList = repository.getNonAchieved()
        assertTrue(databaseList.size == 1)
        assertTrue(shoppingList.id == databaseList[0].id)
        assertTrue(shoppingList.modified == databaseList[0].modified)
        assertFalse(databaseList[0].achieved)
        listItems = databaseList[0].items.toList()
        assertTrue(listItems.size == 2)
        assertTrue(listItems[0].name == "non achieved item")
        assertTrue(listItems[0].achieved)
        assertTrue(listItems[1].name == "changed")
        assertTrue(listItems[1].achieved)

        // remove one item from the list and save
        items.removeAt(0)
        repository.save(shoppingList)
        assertTrue(shoppingList.id == 1)
        assertTrue(repository.getAchieved().isEmpty())

        // check if saved list is the same
        databaseList = repository.getNonAchieved()
        assertTrue(databaseList.size == 1)
        assertTrue(shoppingList.id == databaseList[0].id)
        assertTrue(shoppingList.modified == databaseList[0].modified)
        assertFalse(databaseList[0].achieved)
        listItems = databaseList[0].items.toList()
        assertTrue(listItems.size == 1)
        assertTrue(listItems[0].name == "changed")
        assertTrue(listItems[0].achieved)
    }
}