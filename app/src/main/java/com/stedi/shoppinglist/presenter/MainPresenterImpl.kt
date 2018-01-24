package com.stedi.shoppinglist.presenter

import android.support.annotation.VisibleForTesting
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import com.stedi.shoppinglist.di.DefaultScheduler
import com.stedi.shoppinglist.di.UiScheduler
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import com.stedi.shoppinglist.other.toBooleanArray
import com.stedi.shoppinglist.presenter.interfaces.MainPresenter
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

    class DeleteListEvent(val list: ShoppingList, val t: Throwable? = null)

    class SaveAsAchievedEvent(val list: ShoppingList, val t: Throwable? = null)

    private var view: MainPresenter.UIImpl? = null

    @VisibleForTesting
    var fetching = false

    @VisibleForTesting
    var deleting = false

    @VisibleForTesting
    var saveAsAchieved = false

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

    override fun delete(list: ShoppingList) {
        if (deleting) {
            return
        }

        view?.showConfirmDelete(list)
    }

    override fun confirmDelete(list: ShoppingList) {
        if (deleting) {
            return
        }
        deleting = true

        Observable.fromCallable { repository.remove(list) }
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .subscribe({ bus.post(DeleteListEvent(list)) },
                        { bus.post(DeleteListEvent(list, t = it)) })
    }

    override fun saveAsAchieved(list: ShoppingList) {
        if (saveAsAchieved) {
            return
        }

        view?.showConfirmSaveAsAchieved(list)
    }

    override fun confirmSaveAsAchieved(list: ShoppingList) {
        if (saveAsAchieved) {
            return
        }
        saveAsAchieved = true

        val copy = list.copy()
        copy.modified = System.currentTimeMillis()
        copy.achieved = true
        copy.items.forEach { it.achieved = true }

        Observable.fromCallable { repository.save(copy) }
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .subscribe({ bus.post(SaveAsAchievedEvent(copy)) },
                        { bus.post(SaveAsAchievedEvent(list, it)) })
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
            view?.onLoaded(event.list.sortedByDescending { it.modified })
        }
    }

    @Subscribe
    fun onDeleteListEvent(event: DeleteListEvent) {
        if (!deleting) {
            return
        }
        deleting = false

        if (event.t != null) {
            event.t.printStackTrace()
            view?.onFailedToDelete(event.list)
        } else {
            view?.onDeleted(event.list)
        }
    }

    @Subscribe
    fun onSaveAsAchievedEvent(event: SaveAsAchievedEvent) {
        if (!saveAsAchieved) {
            return
        }
        saveAsAchieved = false

        if (event.t != null) {
            event.t.printStackTrace()
            view?.onFailedToSaveAsAchieved(event.list)
        } else {
            view?.onSavedAsAchieved(event.list)
        }
    }

    override fun restore(state: Serializable, newProcess: Boolean) {
        if (newProcess) {
            return
        }
        val array = state.toBooleanArray(3) ?: return
        fetching = array[0]
        deleting = array[1]
        saveAsAchieved = array[2]
    }

    override fun retain(): Serializable {
        return booleanArrayOf(fetching, deleting, saveAsAchieved)
    }
}