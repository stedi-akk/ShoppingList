package com.stedi.shoppinglist.presenter

import android.support.annotation.VisibleForTesting
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import com.stedi.shoppinglist.di.DefaultScheduler
import com.stedi.shoppinglist.di.UiScheduler
import com.stedi.shoppinglist.model.ShoppingItem
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import com.stedi.shoppinglist.other.toBoolean
import com.stedi.shoppinglist.presenter.interfaces.EditListPresenter
import rx.Observable
import rx.Scheduler
import java.io.Serializable

class EditListPresenterImpl(
        private val repository: ShoppingRepository,

        @DefaultScheduler
        private val subscribeOn: Scheduler,

        @UiScheduler
        private val observeOn: Scheduler,

        private val bus: Bus) : EditListPresenter {

    // in case if current presenter will be dead, we can still get its late events by using event bus
    // it is wrong to cancel background thread jobs, just because of 'lifecycle'
    class SaveListEvent(val list: ShoppingList, val t: Throwable? = null)

    private var view: EditListPresenter.UIImpl? = null

    @VisibleForTesting
    var saving = false

    override fun attach(view: EditListPresenter.UIImpl) {
        this.view = view
        bus.register(this)
    }

    override fun detach() {
        bus.unregister(this)
        this.view = null
    }

    override fun prepare(list: ShoppingList?): ShoppingList {
        if (list == null) {
            return ShoppingList(items = mutableListOf(ShoppingItem()))
        }
        if (list.achieved) {
            view?.disableListEditing()
        }
        return list
    }

    override fun save(list: ShoppingList, checkIfItemsAchieved: Boolean) {
        if (saving) {
            return
        }

        if (list.items.isEmpty()) {
            view?.showErrorEmptyList()
            return
        }

        if (!checkIfItemsAchieved || list.items.any { !it.achieved }) {
            saveInternal(list, false)
        } else {
            view?.showSaveAsAchieved(list)
        }
    }

    override fun saveAsAchieved(list: ShoppingList) {
        if (saving) {
            return
        }

        if (list.items.isEmpty()) {
            view?.showErrorEmptyList()
            return
        }

        saveInternal(list, true)
    }

    private fun saveInternal(list: ShoppingList, asAchieved: Boolean) {
        saving = true

        val copy = list.copy()
        copy.modified = System.currentTimeMillis()
        if (asAchieved) {
            copy.achieved = true
            copy.items.forEach { it.achieved = true }
        }

        Observable.fromCallable { repository.save(copy) }
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .subscribe({ bus.post(SaveListEvent(copy)) },
                        { bus.post(SaveListEvent(list, it)) })
    }

    @Subscribe
    fun onSaveListEvent(event: SaveListEvent) {
        if (!saving) {
            return
        }
        saving = false

        if (event.t != null) {
            event.t.printStackTrace()
            view?.onFailedToSave(event.list)
        } else {
            view?.onSaved(event.list)
        }
    }

    override fun restore(state: Serializable, newProcess: Boolean) {
        if (newProcess) {
            // because the saved state is threads related
            // but this should be changed, if presenter will hold other kind of data
            return
        }
        saving = state.toBoolean()
    }

    override fun retain(): Serializable {
        return saving
    }
}