package com.stedi.shoppinglist.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "shopping_item")
class ShoppingItem(
        @DatabaseField(generatedId = true)
        var id: Int = 0,

        @DatabaseField(foreign = true)
        var fromList: ShoppingList? = null,

        @DatabaseField(columnName = "name", canBeNull = false)
        var name: String = "",

        @DatabaseField(columnName = "achieved")
        var achieved: Boolean = false)