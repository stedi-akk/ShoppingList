package com.stedi.shoppinglist.model.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.misc.TransactionManager
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.stedi.shoppinglist.model.ShoppingItem
import com.stedi.shoppinglist.model.ShoppingList
import java.sql.SQLException

class DatabaseShoppingRepository(context: Context, databaseName: String, databaseVersion: Int) : OrmLiteSqliteOpenHelper(context, databaseName, null, databaseVersion), ShoppingRepository {

    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, ShoppingItem::class.java)
            TableUtils.createTableIfNotExists(connectionSource, ShoppingList::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(database: SQLiteDatabase, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int) {
        try {
            TableUtils.dropTable<ShoppingItem, Int>(connectionSource, ShoppingItem::class.java, false)
            TableUtils.dropTable<ShoppingList, Int>(connectionSource, ShoppingList::class.java, false)
            onCreate(database, connectionSource)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    override fun save(list: ShoppingList) {
        TransactionManager.callInTransaction(getConnectionSource()) {
            deleteOldItemsIfExist(list)
            verifySaved(getDao(ShoppingList::class.java).createOrUpdate(list))
            val itemsDao: Dao<ShoppingItem, Int> = getDao(ShoppingItem::class.java)
            for (item in list.items) {
                item.fromList = list
                verifySaved(itemsDao.createOrUpdate(item))
            }
        }
    }

    @Synchronized
    override fun remove(list: ShoppingList) {
        TransactionManager.callInTransaction(getConnectionSource()) {
            verifyDeleted(getDao(ShoppingList::class.java).delete(list))
            val itemsDao: Dao<ShoppingItem, Int> = getDao(ShoppingItem::class.java)
            for (item in list.items) {
                verifyDeleted(itemsDao.delete(item))
            }
        }
    }

    @Synchronized
    override fun getNonAchieved(): List<ShoppingList> {
        return getDao(ShoppingList::class.java).queryBuilder().where().eq("achieved", false).query()
    }

    @Synchronized
    override fun getAchieved(): List<ShoppingList> {
        return getDao(ShoppingList::class.java).queryBuilder().where().eq("achieved", true).query()
    }

    private fun deleteOldItemsIfExist(list: ShoppingList) {
        if (list.id > 0) {
            val listDao: Dao<ShoppingList, Int> = getDao(ShoppingList::class.java)
            val dbList = listDao.queryForId(list.id)
            val itemsDao: Dao<ShoppingItem, Int> = getDao(ShoppingItem::class.java)
            for (item in dbList.items) {
                verifyDeleted(itemsDao.delete(item))
            }
        }
    }

    private fun verifySaved(status: Dao.CreateOrUpdateStatus) {
        if (!status.isCreated && !status.isUpdated) {
            throw SQLException("failed to save or update")
        }
    }

    private fun verifyDeleted(num: Int) {
        if (num != 1) {
            throw SQLException("failed to delete list")
        }
    }
}