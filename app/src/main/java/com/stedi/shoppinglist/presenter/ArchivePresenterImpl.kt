package com.stedi.shoppinglist.presenter

import android.support.annotation.VisibleForTesting
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import com.stedi.shoppinglist.di.DefaultScheduler
import com.stedi.shoppinglist.di.UiScheduler
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import com.stedi.shoppinglist.other.toBooleanArray
import com.stedi.shoppinglist.presenter.interfaces.ArchivePresenter
import rx.Observable
import rx.Scheduler
import java.io.Serializable

class ArchivePresenterImpl(
        private val repository: ShoppingRepository,

        @DefaultScheduler
        private val subscribeOn: Scheduler,

        @UiScheduler
        private val observeOn: Scheduler,

        private val bus: Bus) : ArchivePresenter {

    class FetchListsEvent(val list: List<ShoppingList> = emptyList(), val t: Throwable? = null)

    class ClearListsEvent(val t: Throwable? = null)

    private var view: ArchivePresenter.UIImpl? = null

    @VisibleForTesting
    var fetching = false

    @VisibleForTesting
    var cleaning = false

    override fun attach(view: ArchivePresenter.UIImpl) {
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

        Observable.fromCallable { repository.getAchieved() }
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .subscribe({ bus.post(FetchListsEvent(list = it)) },
                        { bus.post(FetchListsEvent(t = it)) })
    }

    override fun clear(list: List<ShoppingList>) {
        if (cleaning) {
            return
        }

        if (list.isEmpty()) {
            view?.onFailedToClear()
            return
        }

        cleaning = true

        Observable.fromCallable { list.forEach { repository.remove(it) } }
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .subscribe({ bus.post(ClearListsEvent()) },
                        { bus.post(ClearListsEvent(t = it)) })
    }

    @Subscribe
    fun onFetchListsEvent(event: FetchListsEvent) {
        if (!fetching) {
            return
        }
        fetching = false

        if (event.t != null) {
            event.t.printStackTrace()
            view?.onFailedToLoad()
        } else {
            view?.onLoaded(event.list)
        }
    }

    @Subscribe
    fun onClearListsEvent(event: ClearListsEvent) {
        if (!cleaning) {
            return
        }
        cleaning = false

        if (event.t != null) {
            event.t.printStackTrace()
            view?.onFailedToClear()
        } else {
            view?.onCleared()
        }
    }

    override fun restore(state: Serializable) {
        val array = state.toBooleanArray(2) ?: return
        fetching = array[0]
        cleaning = array[1]
    }

    override fun retain(): Serializable {
        return booleanArrayOf(fetching, cleaning)
    }
}