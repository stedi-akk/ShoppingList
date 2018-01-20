package com.stedi.shoppinglist.other

import android.content.Context
import com.stedi.shoppinglist.App
import com.stedi.shoppinglist.di.AppComponent

fun Context.getAppComponent(): AppComponent {
    return if (this is App) {
        this.component
    } else {
        this.applicationContext.getAppComponent()
    }
}