package com.stedi.shoppinglist.presenter;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
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

    private ArgumentCaptor<List<ShoppingList>> captor;

    @Before
    public void before() {
        repository = mock(ShoppingRepository.class);
        presenter = new MainPresenterImpl(repository, Schedulers.immediate(), Schedulers.immediate(), new Bus(ThreadEnforcer.ANY));
        view = mock(MainPresenter.UIImpl.class);
        captor = ArgumentCaptor.forClass((Class) List.class);
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

        verify(view, times(1)).onLoaded(captor.capture());
        List<ShoppingList> captorList = captor.getValue();

        list.sort((o1, o2) -> {
            long o1Modified = o1.getModified();
            long o2Modified = o2.getModified();
            return o1Modified > o2Modified ? -1 : o1Modified == o2Modified ? 0 : 1;
        });

        assertEquals(list, captorList);

        verifyNoMoreInteractions(view);
    }
}
