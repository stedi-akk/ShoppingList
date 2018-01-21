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

class EditListPresenterImpl(
        private val repository: ShoppingRepository,

        @DefaultScheduler
        private val subscribeOn: Scheduler,

        @UiScheduler
        private val observeOn: Scheduler,

        private val bus: Bus) : EditListPresenter {

    class SaveListEvent(val t: Throwable? = null)

    private var view: EditListPresenter.UIImpl? = null

    private var saving = false

    override fun attach(view: EditListPresenter.UIImpl) {
        this.view = view
        bus.register(this)
    }

    override fun detach() {
        bus.unregister(this)
        this.view = null
    }

    override fun save(list: ShoppingList) {
        if (saving) {
            return
        }

        saving = true
        list.modified = System.currentTimeMillis()

        Observable.fromCallable { repository.save(list) }
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .subscribe({ bus.post(SaveListEvent()) },
                        { bus.post(SaveListEvent(it)) })
    }

    @Subscribe
    fun onSaveListEvent(event: SaveListEvent) {
        if (!saving) {
            return
        }
        saving = false

        val view = view ?: return

        if (event.t != null) {
            event.t.printStackTrace()
            view.onFailedToSave()
        } else {
            view.onSaved()
        }
    }

    override fun restore(state: Serializable) {
        saving = state.toBoolean()
    }

    override fun retain(): Serializable {
        return saving
    }
}