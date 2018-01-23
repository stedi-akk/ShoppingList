package com.stedi.shoppinglist.di

import com.stedi.shoppinglist.view.activity.ArchiveActivity
import com.stedi.shoppinglist.view.activity.BaseActivity
import com.stedi.shoppinglist.view.activity.EditListActivity
import com.stedi.shoppinglist.view.activity.MainActivity
import com.stedi.shoppinglist.view.dialogs.ConfirmDialog
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (BuildTypeModule::class)])
interface AppComponent {
    fun inject(activity: BaseActivity)

    fun inject(activity: MainActivity)

    fun inject(activity: EditListActivity)

    fun inject(activity: ArchiveActivity)

    fun inject(dialog: ConfirmDialog)
}