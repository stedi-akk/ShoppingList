package com.stedi.shoppinglist.presenter

import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import com.stedi.shoppinglist.di.DefaultScheduler
import com.stedi.shoppinglist.di.UiScheduler
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import com.stedi.shoppinglist.other.toBoolean
import rx.Observable
import rx.Scheduler
import java.io.Serializable

class MainPresenterImpl(
        private val repository: ShoppingRepository,

        @DefaultScheduler
        private val subscribeOn: Scheduler,

        @UiScheduler
        private val observeOn: Scheduler,

        private val bus: Bus) : MainPresenter {

    class FetchListsEvent(val list: List<ShoppingList> = emptyList(), val t: Throwable? = null)

    private var view: MainPresenter.UIImpl? = null

    private var fetching = false;

    override fun attach(view: MainPresenter.UIImpl) {
        this.view = view
        bus.register(this)
    }

    override fun detach() {
        bus.unregister(this)
        this.view = null
    }

    override fun fetchLists() {
        if (fetching) {
            return
        }

        fetching = true

        Observable.fromCallable { repository.getNonAchieved() }
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .subscribe({ bus.post(FetchListsEvent(list = it)) },
                        { bus.post(FetchListsEvent(t = it)) })
    }

    @Subscribe
    fun onFetchListsEvent(event: FetchListsEvent) {
        if (!fetching) {
            return
        }
        fetching = false

        val view = view ?: return

        if (event.t != null) {
            event.t.printStackTrace()
            view.onFailedToLoad()
        } else {
            view.onLoaded(event.list)
        }
    }

    override fun restore(state: Serializable) {
        fetching = state.toBoolean()
    }

    override fun retain(): Serializable {
        return fetching
    }
}