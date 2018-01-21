package com.stedi.shoppinglist.model

import android.os.Parcel
import android.os.Parcelable
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import com.stedi.shoppinglist.other.toBoolean
import com.stedi.shoppinglist.other.toInt

@DatabaseTable(tableName = "shopping_list")
data class ShoppingList(
        @DatabaseField(generatedId = true)
        var id: Int = 0,

        @DatabaseField(columnName = "modified")
        var modified: Long = 0,

        @ForeignCollectionField(eager = true)
        var items: Collection<ShoppingItem> = emptyList(),

        @DatabaseField(columnName = "achieved")
        var achieved: Boolean = false) : Parcelable {

    override fun describeContents() = 1

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeLong(modified)
        dest.writeTypedList(items.toList())
        dest.writeInt(achieved.toInt())
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ShoppingList> {
            override fun createFromParcel(source: Parcel): ShoppingList {
                val id = source.readInt()
                val modified = source.readLong()
                val typedList = mutableListOf<ShoppingItem>()
                source.readTypedList(typedList, ShoppingItem.CREATOR)
                val achieved = source.readInt().toBoolean()
                return ShoppingList(id, modified, typedList, achieved)
            }

            override fun newArray(size: Int) = arrayOfNulls<ShoppingList>(size)
        }
    }
}