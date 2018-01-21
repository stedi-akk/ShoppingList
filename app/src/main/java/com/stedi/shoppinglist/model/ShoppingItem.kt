package com.stedi.shoppinglist.model

import android.os.Parcel
import android.os.Parcelable
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import com.stedi.shoppinglist.other.toBoolean
import com.stedi.shoppinglist.other.toInt

@DatabaseTable(tableName = "shopping_item")
data class ShoppingItem(
        @DatabaseField(generatedId = true)
        var id: Int = 0,

        @DatabaseField(columnName = "name", canBeNull = false)
        var name: String = "",

        @DatabaseField(columnName = "achieved")
        var achieved: Boolean = false) : Parcelable {

    @DatabaseField(foreign = true)
    var fromList: ShoppingList? = null

    override fun describeContents() = 1

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeInt(achieved.toInt())
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ShoppingItem> {
            override fun createFromParcel(source: Parcel): ShoppingItem {
                val id = source.readInt()
                val name = source.readString()
                val achieved = source.readInt().toBoolean()
                return ShoppingItem(id, name, achieved)
            }

            override fun newArray(size: Int) = arrayOfNulls<ShoppingItem>(size)
        }
    }
}