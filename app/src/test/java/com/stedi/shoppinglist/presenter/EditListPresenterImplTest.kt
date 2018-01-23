package com.stedi.shoppinglist.presenter

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import com.stedi.shoppinglist.model.ShoppingItem
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import com.stedi.shoppinglist.other.toBoolean
import com.stedi.shoppinglist.presenter.interfaces.EditListPresenter
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
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
    fun testNewListIsNotEmpty() {
        assertFalse(presenter.newList().items.isEmpty())
    }

    @Test
    fun testSaveListEmpty() {
        presenter.attach(view)

        presenter.save(ShoppingList(items = emptyList<ShoppingItem>()))

        verify(view, times(1)).showErrorEmptyList()

        presenter.detach()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testRetainRestore() {
        presenter.saving = true

        val state = presenter.retain()
        assertTrue(state.toBoolean())

        presenter.saving = false

        presenter.restore(state)
        assertTrue(presenter.saving)
    }
}