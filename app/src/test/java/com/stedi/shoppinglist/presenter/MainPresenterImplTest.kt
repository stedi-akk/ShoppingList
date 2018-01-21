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

class MainPresenterImplTest {
    private lateinit var repository: ShoppingRepository
    private lateinit var presenter: MainPresenterImpl
    private lateinit var view: MainPresenter.UIImpl

    @Before
    fun before() {
        repository = mock(ShoppingRepository::class.java)
        presenter = MainPresenterImpl(repository, Schedulers.immediate(), Schedulers.immediate(), Bus(ThreadEnforcer.ANY))
        view = mock(MainPresenter.UIImpl::class.java)
    }

    @Test
    fun testFetchLists() {
        presenter.attach(view)

        var list = emptyList<ShoppingList>()
        `when`(repository.getNonAchieved()).thenReturn(list)
        presenter.fetchLists()

        verify(view, times(1)).onLoaded(list)

        val items1 = listOf(ShoppingItem(name = "name1", achieved = false))
        val items2 = listOf(ShoppingItem(name = "name2", achieved = false), ShoppingItem(name = "name3", achieved = true))
        list = listOf(ShoppingList(modified = 1L, items = items1, achieved = false), ShoppingList(modified = 2L, items = items2, achieved = false))
        `when`(repository.getNonAchieved()).thenReturn(list)
        presenter.fetchLists()

        verify(view, times(1)).onLoaded(list)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun testFetchListsFail() {
        presenter.attach(view)

        `when`(repository.getNonAchieved()).thenThrow(Exception("fail"))
        presenter.fetchLists()

        verify(view, times(1)).onFailedToLoad()
        verifyNoMoreInteractions(view)
    }
}