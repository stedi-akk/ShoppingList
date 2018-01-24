package com.stedi.shoppinglist.presenter.interfaces

interface UI

interface Presenter<in V : UI> {
    fun attach(view: V)

    fun detach()
}

interface RetainedPresenter<in V : UI> : Presenter<V> {
    fun restore(state: java.io.Serializable, newProcess: Boolean = false)

    fun retain(): java.io.Serializable
}
