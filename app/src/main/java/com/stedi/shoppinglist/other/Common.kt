package com.stedi.shoppinglist.other

import android.content.Context
import android.support.annotation.DimenRes
import android.support.annotation.StringRes
import android.util.TypedValue
import android.widget.Toast
import com.stedi.shoppinglist.App
import com.stedi.shoppinglist.di.AppComponent
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

fun Context.getAppComponent(): AppComponent {
    return if (this is App) {
        this.component
    } else {
        this.applicationContext.getAppComponent()
    }
}

fun Context.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, resId, duration).show()
}

fun Serializable.toBoolean(default: Boolean = false): Boolean {
    return this as? Boolean ?: default
}

fun Serializable.toBooleanArray(size: Int): BooleanArray? {
    if (this is BooleanArray) {
        if (this.size != size) {
            return null
        }
        return this
    }
    return null
}

fun Context.dp2px(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.dp2px(@DimenRes resId: Int): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelOffset(resId).toFloat(), resources.displayMetrics)
}

fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

fun Int.toBoolean(): Boolean {
    return this == 1
}

fun Long.asDateFormat(): String {
    return LazyCommon.dateFormat.format(Date(this))
}

private object LazyCommon {
    val dateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("dd.MM.yyyy HH:mm")
    }
}