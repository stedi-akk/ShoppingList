package com.stedi.shoppinglist.other

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast
import com.stedi.shoppinglist.App
import com.stedi.shoppinglist.di.AppComponent
import java.io.Serializable

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