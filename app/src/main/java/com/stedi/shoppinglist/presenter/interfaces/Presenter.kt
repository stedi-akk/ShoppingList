package com.stedi.shoppinglist.presenter.interfaces

interface UI

interface Presenter<in V : UI> {
    fun attach(view: V)

    fun detach()
}

// in my opinion, presenters should be killed in the same way as activities,
// and should be restored in the same way as activities
interface RetainedPresenter<in V : UI> : Presenter<V> {
    /**
     * @param state restored state
     * @param newProcess true, if called after process kill (because some threads related state may not be valid, and should not be restored)
     */
    fun restore(state: java.io.Serializable, newProcess: Boolean = false)

    /**
     * presenter should return its state, to restore it later
     */
    fun retain(): java.io.Serializable
}
