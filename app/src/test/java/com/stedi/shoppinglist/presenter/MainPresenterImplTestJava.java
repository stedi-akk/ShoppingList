package com.stedi.shoppinglist.presenter;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.stedi.shoppinglist.model.ShoppingItem;
import com.stedi.shoppinglist.model.ShoppingList;
import com.stedi.shoppinglist.model.repository.ShoppingRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

// because Kotlin does not allow null mocking for non-null arguments
public class MainPresenterImplTestJava {
    private ShoppingRepository repository;
    private MainPresenterImpl presenter;
    private MainPresenter.UIImpl view;

    private ArgumentCaptor<ShoppingList> captor;
    private ArgumentCaptor<List<ShoppingList>> listCaptor;

    @Before
    public void before() {
        repository = mock(ShoppingRepository.class);
        presenter = new MainPresenterImpl(repository, Schedulers.immediate(), Schedulers.immediate(), new Bus(ThreadEnforcer.ANY));
        view = mock(MainPresenter.UIImpl.class);
        captor = ArgumentCaptor.forClass(ShoppingList.class);
        listCaptor = ArgumentCaptor.forClass((Class) List.class);
    }

    @Test
    public void testFetchListsSortedByDate() throws Exception {
        presenter.attach(view);

        List<ShoppingList> list = new ArrayList<>();
        list.add(new ShoppingList(0, 2L, Collections.emptyList(), false));
        list.add(new ShoppingList(0, 1L, Collections.emptyList(), false));
        list.add(new ShoppingList(0, 3L, Collections.emptyList(), false));
        when(repository.getNonAchieved()).thenReturn(list);
        presenter.fetchLists();

        verify(view, times(1)).onLoaded(listCaptor.capture());
        List<ShoppingList> captorList = listCaptor.getValue();

        list.sort((o1, o2) -> {
            long o1Modified = o1.getModified();
            long o2Modified = o2.getModified();
            return o1Modified > o2Modified ? -1 : o1Modified == o2Modified ? 0 : 1;
        });

        assertEquals(list, captorList);

        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSaveAsAchieved() throws Exception {
        presenter.attach(view);

        List<ShoppingItem> items = new ArrayList<>();
        items.add(new ShoppingItem(0, "a", true));
        items.add(new ShoppingItem(0, "b", false));
        ShoppingList list = new ShoppingList(0, 1L, items, false);
        presenter.saveAsAchieved(list);

        verify(view, times(1)).showConfirmSaveAsAchieved(list);
        presenter.confirmSaveAsAchieved(list);

        verify(view, times(1)).onSavedAsAchieved(captor.capture());
        ShoppingList captorList = captor.getValue();

        assertTrue(captorList.getAchieved());
        for (ShoppingItem item : captorList.getItems()) {
            assertTrue(item.getAchieved());
        }

        presenter.detach();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSaveAsAchievedFail() throws Exception {
        presenter.attach(view);

        List<ShoppingItem> items = new ArrayList<>();
        items.add(new ShoppingItem(0, "a", true));
        items.add(new ShoppingItem(0, "b", false));
        ShoppingList list = new ShoppingList(0, 1L, items, false);
        doThrow(new Exception("fail")).when(repository).save(any(ShoppingList.class));
        presenter.saveAsAchieved(list);

        verify(view, times(1)).showConfirmSaveAsAchieved(list);
        presenter.confirmSaveAsAchieved(list);

        verify(view, times(1)).onFailedToSaveAsAchieved(list);

        presenter.detach();
        verifyNoMoreInteractions(view);
    }
}
