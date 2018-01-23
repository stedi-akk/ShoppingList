package com.stedi.shoppinglist.presenter;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.stedi.shoppinglist.model.ShoppingItem;
import com.stedi.shoppinglist.model.ShoppingList;
import com.stedi.shoppinglist.model.repository.ShoppingRepository;
import com.stedi.shoppinglist.presenter.interfaces.EditListPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

// because Kotlin does not allow null mocking for non-null arguments
public class EditListPresenterImplTestJava {
    private ShoppingRepository repository;
    private EditListPresenterImpl presenter;
    private EditListPresenter.UIImpl view;

    private ArgumentCaptor<ShoppingList> captor;

    @Before
    public void before() {
        repository = mock(ShoppingRepository.class);
        presenter = new EditListPresenterImpl(repository, Schedulers.immediate(), Schedulers.immediate(), new Bus(ThreadEnforcer.ANY));
        view = mock(EditListPresenter.UIImpl.class);
        captor = ArgumentCaptor.forClass(ShoppingList.class);
    }

    @Test
    public void testSaveList() {
        presenter.attach(view);

        ShoppingList list = presenter.newList();
        presenter.save(list, true);

        verify(view, times(1)).onSaved(any(ShoppingList.class));

        presenter.detach();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSaveListFail() throws Exception {
        presenter.attach(view);

        ShoppingList list = presenter.newList();
        doThrow(new Exception("fail")).when(repository).save(any(ShoppingList.class));
        presenter.save(list, true);

        verify(view, times(1)).onFailedToSave(list);

        presenter.detach();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSaveListAchievedItems() {
        presenter.attach(view);

        List<ShoppingItem> items = new ArrayList<>();
        items.add(new ShoppingItem(0, "a", true));
        items.add(new ShoppingItem(0, "b", true));
        ShoppingList list = new ShoppingList(0, 1L, items, false);
        presenter.save(list, true);

        verify(view, times(1)).showSaveAsAchieved(list);
        presenter.saveAsAchieved(list);

        verify(view, times(1)).onSaved(captor.capture());
        ShoppingList captorList = captor.getValue();

        assertTrue(captorList.getAchieved());
        for (ShoppingItem item : captorList.getItems()) {
            assertTrue(item.getAchieved());
        }

        presenter.detach();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSaveListAchievedItemsIgnore() {
        presenter.attach(view);

        List<ShoppingItem> items = new ArrayList<>();
        items.add(new ShoppingItem(0, "a", true));
        items.add(new ShoppingItem(0, "b", true));
        ShoppingList list = new ShoppingList(0, 1L, items, false);
        presenter.save(list, true);

        verify(view, times(1)).showSaveAsAchieved(list);
        presenter.save(list, false);

        verify(view, times(1)).onSaved(captor.capture());
        ShoppingList captorList = captor.getValue();

        assertFalse(captorList.getAchieved());

        presenter.detach();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSaveListAchievedItemsFail() throws Exception {
        presenter.attach(view);

        List<ShoppingItem> items = new ArrayList<>();
        items.add(new ShoppingItem(0, "a", true));
        items.add(new ShoppingItem(0, "b", true));
        ShoppingList list = new ShoppingList(0, 1L, items, false);
        doThrow(new Exception("fail")).when(repository).save(any(ShoppingList.class));
        presenter.save(list, true);

        verify(view, times(1)).showSaveAsAchieved(list);
        presenter.saveAsAchieved(list);

        verify(view, times(1)).onFailedToSave(captor.capture());
        ShoppingList captorList = captor.getValue();

        assertFalse(captorList.getAchieved());

        presenter.detach();
        verifyNoMoreInteractions(view);
    }
}
