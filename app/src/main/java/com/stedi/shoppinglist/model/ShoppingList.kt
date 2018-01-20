package com.stedi.shoppinglist.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "shopping_list")
class ShoppingList(
        @DatabaseField(generatedId = true)
        var id: Int = 0,

        @DatabaseField(columnName = "modified")
        var modified: Long = 0,

        @ForeignCollectionField(eager = true)
        var items: Collection<ShoppingItem> = emptyList(),

        @DatabaseField(columnName = "achieved")
        var achieved: Boolean = false)