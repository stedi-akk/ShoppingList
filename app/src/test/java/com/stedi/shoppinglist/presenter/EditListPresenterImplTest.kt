package com.stedi.shoppinglist.presenter

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import com.stedi.shoppinglist.model.ShoppingItem
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import rx.schedulers.Schedulers

class EditListPresenterImplTest {
    private lateinit var repository: ShoppingRepository
    private lateinit var presenter: EditListPresenterImpl
    private lateinit var view: EditListPresenter.UIImpl

    @Before
    fun before() {
        repository = mock(ShoppingRepository::class.java)
        presenter = EditListPresenterImpl(repository, Schedulers.immediate(), Schedulers.immediate(), Bus(ThreadEnforcer.ANY))
        view = mock(EditListPresenter.UIImpl::class.java)
    }

    @Test
    fun testSaveList() {
        presenter.attach(view)

        val items = listOf(ShoppingItem(name = "name2", achieved = false), ShoppingItem(name = "name3", achieved = true))
        val list = ShoppingList(items = items, achieved = false)
        presenter.save(list)

        verify(view, times(1)).onSaved()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testSaveListFail() {
        presenter.attach(view)

        val list = ShoppingList()
        `when`(repository.save(list)).thenThrow(Exception("fail"))
        presenter.save(list)

        verify(view, times(1)).onFailedToSave()
        verifyNoMoreInteractions(view)
    }
}