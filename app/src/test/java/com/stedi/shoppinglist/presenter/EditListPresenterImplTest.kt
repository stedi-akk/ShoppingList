package com.stedi.shoppinglist.presenter

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import com.stedi.shoppinglist.model.ShoppingItem
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import com.stedi.shoppinglist.other.toBoolean
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

        val items = emptyList<ShoppingItem>()
        val list = ShoppingList(items = items)
        presenter.save(list)

        verify(view, times(1)).showErrorEmptyList()

        presenter.detach()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testSaveList() {
        presenter.attach(view)

        val items = listOf(ShoppingItem(name = "name2", achieved = false), ShoppingItem(name = "name3", achieved = true))
        val list = ShoppingList(items = items)
        presenter.save(list)

        verify(view, times(1)).onSaved()

        presenter.detach()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testSaveListFail() {
        presenter.attach(view)

        val list = presenter.newList()
        `when`(repository.save(list)).thenThrow(Exception("fail"))
        presenter.save(list)

        verify(view, times(1)).onFailedToSave()

        presenter.detach()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testSaveListAchievedItems() {
        presenter.attach(view)

        val items = listOf(ShoppingItem(name = "a", achieved = true), ShoppingItem(name = "b", achieved = true))
        val list = ShoppingList(items = items)
        presenter.save(list)

        verify(view, times(1)).showSaveAsAchieved(list)
        presenter.saveAsAchieved(list)

        verify(view, times(1)).onSaved()
        assertTrue(list.achieved)
        list.items.forEach { assertTrue(it.achieved) }

        presenter.detach()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testSaveListAchievedItemsIgnore() {
        presenter.attach(view)

        val items = listOf(ShoppingItem(name = "a", achieved = true), ShoppingItem(name = "b", achieved = true))
        val list = ShoppingList(items = items)
        presenter.save(list)

        verify(view, times(1)).showSaveAsAchieved(list)
        presenter.save(list, false)

        verify(view, times(1)).onSaved()
        assertFalse(list.achieved)

        presenter.detach()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testRetainRestore() {
        presenter.saving = true

        val state = presenter.retain()
        assertTrue(state.toBoolean(false))

        presenter.saving = false

        presenter.restore(state)
        assertTrue(presenter.saving)
    }
}