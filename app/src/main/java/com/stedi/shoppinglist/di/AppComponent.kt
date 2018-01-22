package com.stedi.shoppinglist.di

import com.stedi.shoppinglist.view.BaseActivity
import com.stedi.shoppinglist.view.ConfirmDialog
import com.stedi.shoppinglist.view.EditListActivity
import com.stedi.shoppinglist.view.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (BuildTypeModule::class)])
interface AppComponent {
    fun inject(activity: BaseActivity)

    fun inject(activity: MainActivity)

    fun inject(activity: EditListActivity)

    fun inject(dialog: ConfirmDialog)
}