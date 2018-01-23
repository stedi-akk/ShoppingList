package com.stedi.shoppinglist.presenter

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import com.stedi.shoppinglist.model.ShoppingItem
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import com.stedi.shoppinglist.other.toBoolean
import com.stedi.shoppinglist.presenter.interfaces.ArchivePresenter
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import rx.schedulers.Schedulers

class ArchivePresenterImplTest {
    private lateinit var repository: ShoppingRepository
    private lateinit var presenter: ArchivePresenterImpl
    private lateinit var view: ArchivePresenter.UIImpl

    @Before
    fun before() {
        repository = mock(ShoppingRepository::class.java)
        presenter = ArchivePresenterImpl(repository, Schedulers.immediate(), Schedulers.immediate(), Bus(ThreadEnforcer.ANY))
        view = mock(ArchivePresenter.UIImpl::class.java)
    }

    @Test
    fun testFetchLists() {
        presenter.attach(view)

        var list = emptyList<ShoppingList>()
        `when`(repository.getAchieved()).thenReturn(list)
        presenter.fetchLists()

        verify(view, times(1)).onLoaded(list)

        val items1 = listOf(ShoppingItem(name = "name1", achieved = true))
        val items2 = listOf(ShoppingItem(name = "name2", achieved = true), ShoppingItem(name = "name3", achieved = true))
        list = listOf(ShoppingList(modified = 2L, items = items1, achieved = true), ShoppingList(modified = 1L, items = items2, achieved = true))
        `when`(repository.getAchieved()).thenReturn(list)
        presenter.fetchLists()

        verify(view, times(1)).onLoaded(list)

        presenter.detach()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testFetchListsFail() {
        presenter.attach(view)

        `when`(repository.getAchieved()).thenThrow(Exception("fail"))
        presenter.fetchLists()

        verify(view, times(1)).onFailedToLoad()

        presenter.detach()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testRetainRestore() {
        presenter.fetching = true

        val state = presenter.retain()
        Assert.assertTrue(state.toBoolean())

        presenter.fetching = false

        presenter.restore(state)
        Assert.assertTrue(presenter.fetching)
    }
}