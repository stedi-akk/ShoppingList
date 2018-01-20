package com.stedi.shoppinglist

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.stedi.shoppinglist.di.AppComponent
import com.stedi.shoppinglist.di.AppModule
import com.stedi.shoppinglist.di.DaggerAppComponent

class App : Application() {
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}