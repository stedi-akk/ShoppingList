package com.stedi.shoppinglist.di

import android.content.Context
import com.squareup.otto.Bus
import com.stedi.shoppinglist.App
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import com.stedi.shoppinglist.other.LockedBus
import com.stedi.shoppinglist.presenter.*
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
    @Singleton
    fun provideBus(): Bus = LockedBus()

    @Provides
    @DefaultScheduler
    fun provideDefaultScheduler(): Scheduler = Schedulers.io()

    @Provides
    @UiScheduler
    fun provideAndroidScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    fun provideMainPresenter(repository: ShoppingRepository, @DefaultScheduler subscribeOn: Scheduler, @UiScheduler observeOn: Scheduler, bus: Bus): MainPresenter {
        return MainPresenterImpl(repository, subscribeOn, observeOn, bus)
    }

    @Provides
    fun provideEditListPresenter(repository: ShoppingRepository, @DefaultScheduler subscribeOn: Scheduler, @UiScheduler observeOn: Scheduler, bus: Bus): EditListPresenter {
        return EditListPresenterImpl(repository, subscribeOn, observeOn, bus)
    }

    @Provides
    fun provideArchivePresenter(repository: ShoppingRepository, @DefaultScheduler subscribeOn: Scheduler, @UiScheduler observeOn: Scheduler, bus: Bus): ArchivePresenter {
        return ArchivePresenterImpl(repository, subscribeOn, observeOn, bus)
    }
}
