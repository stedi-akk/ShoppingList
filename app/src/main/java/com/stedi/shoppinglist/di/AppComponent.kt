package com.stedi.shoppinglist.di

import com.stedi.shoppinglist.view.BaseActivity
import com.stedi.shoppinglist.view.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (BuildTypeModule::class)])
interface AppComponent {
    fun inject(activity: BaseActivity)

    fun inject(activity: MainActivity)
}