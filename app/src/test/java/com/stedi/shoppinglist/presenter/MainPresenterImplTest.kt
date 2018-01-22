package com.stedi.shoppinglist.presenter

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import com.stedi.shoppinglist.model.ShoppingItem
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.model.repository.ShoppingRepository
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import rx.schedulers.Schedulers

class MainPresenterImplTest {
    private lateinit var repository: ShoppingRepository
    private lateinit var presenter: MainPresenterImpl
    private lateinit var view: MainPresenter.UIImpl

    @Captor
    lateinit var listCaptor: ArgumentCaptor<List<ShoppingList>>

    @Before
    fun before() {
        repository = mock(ShoppingRepository::class.java)
        presenter = MainPresenterImpl(repository, Schedulers.immediate(), Schedulers.immediate(), Bus(ThreadEnforcer.ANY))
        view = mock(MainPresenter.UIImpl::class.java)
        MockitoAnnotations.initMocks(this) // for captor
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
        list = listOf(ShoppingList(modified = 2L, items = items1, achieved = false), ShoppingList(modified = 1L, items = items2, achieved = false))
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

    @Test
    fun testSortedByDate() {
//        presenter.attach(view)
//
//        val list = listOf(ShoppingList(modified = 2L), ShoppingList(modified = 1L), ShoppingList(modified = 3L))
//        `when`(repository.getNonAchieved()).thenReturn(list)
//        presenter.fetchLists()
//
//        verify(view, times(1)).onLoaded(listCaptor.capture())
//        assertEquals(list.sortedByDescending { it.modified }, listCaptor.value)
//        verifyNoMoreInteractions(view)
    }
}