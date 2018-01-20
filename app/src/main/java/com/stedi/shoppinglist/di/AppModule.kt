package com.stedi.shoppinglist.di

import android.content.Context
import com.stedi.shoppinglist.App
import dagger.Module
import dagger.Provides
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    @AppContext
    fun provideAppContext(): Context = app

    @Provides
    @DefaultScheduler
    fun provideDefaultScheduler(): Scheduler = Schedulers.io()

    @Provides
    @UiScheduler
    fun provideAndroidScheduler(): Scheduler = AndroidSchedulers.mainThread()
}
