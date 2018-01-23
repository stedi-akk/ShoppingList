package com.stedi.shoppinglist.view.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.squareup.otto.Subscribe
import com.stedi.shoppinglist.R
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.other.dp2px
import com.stedi.shoppinglist.other.getAppComponent
import com.stedi.shoppinglist.other.showToast
import com.stedi.shoppinglist.presenter.MainPresenter
import com.stedi.shoppinglist.view.dialogs.ConfirmDialog
import com.stedi.shoppinglist.view.ListSpaceDecoration
import com.stedi.shoppinglist.view.ShoppingListsAdapter
import javax.inject.Inject

class MainActivity : BaseActivity(), MainPresenter.UIImpl, ShoppingListsAdapter.ClickListener {
    private val KEY_PRESENTER_STATE = "KEY_PRESENTER_STATE"
    private val KEY_LIST_TO_DELETE = "KEY_LIST_TO_DELETE"

    private val REQUEST_DELETE_LIST = 123

    @BindView(R.id.main_activity_recycler_view)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.main_activity_empty_view)
    lateinit var emptyView: View

    @BindView(R.id.main_activity_fab)
    lateinit var fab: FloatingActionButton

    @Inject
    lateinit var presenter: MainPresenter

    private lateinit var adapter: ShoppingListsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAppComponent().inject(this)
        bus.register(this)

        setContentView(R.layout.main_activity)
        ButterKnife.bind(this)

        fab.hide(fabShowHideListener)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ListSpaceDecoration(dp2px(R.dimen.common_v_spacing).toInt(), dp2px(R.dimen.common_lr_spacing).toInt()))
        adapter = ShoppingListsAdapter(layoutInflater, this)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(recyclerScrollListener)

        presenter.attach(this)
        savedInstanceState?.getSerializable(KEY_PRESENTER_STATE)?.apply {
            presenter.restore(this)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.fetchLists()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
        bus.unregister(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_PRESENTER_STATE, presenter.retain())
    }

    @OnClick(R.id.main_activity_fab)
    fun onFabClick(v: View) {
        EditListActivity.start(this)
    }

    override fun onListClicked(list: ShoppingList) {
        EditListActivity.start(this, list)
    }

    override fun onDeleteClicked(list: ShoppingList) {
        presenter.delete(list)
    }

    override fun onDoneClicked(list: ShoppingList) {

    }

    override fun onLoaded(list: List<ShoppingList>) {
        fab.show(fabShowHideListener)
        adapter.set(list)
        refreshEmptyView()
    }

    override fun onDeleted(list: ShoppingList) {
        showToast(R.string.deleted, Toast.LENGTH_SHORT)
        presenter.fetchLists()
    }

    override fun showConfirmDelete(list: ShoppingList) {
        val bundle = Bundle()
        bundle.putParcelable(KEY_LIST_TO_DELETE, list)
        ConfirmDialog.newInstance(REQUEST_DELETE_LIST, messageId = R.string.confirm_delete, bundle = bundle)
                .show(supportFragmentManager)
    }

    override fun onFailedToLoad() {
        fab.show(fabShowHideListener)
        showToast(R.string.failed_to_load_lists)
        refreshEmptyView()
    }

    override fun onFailedToDelete(list: ShoppingList) {
        showToast(R.string.failed_to_delete_list)
    }

    @Subscribe
    fun onConfirmDialogCallback(callback: ConfirmDialog.Callback) {
        if (callback.confirmed && callback.requestCode == REQUEST_DELETE_LIST) {
            if (callback.bundle == null) {
                return
            }

            val listToDelete: ShoppingList = callback.bundle.getParcelable(KEY_LIST_TO_DELETE)
            presenter.confirmDelete(listToDelete)
        }
    }

    private fun refreshEmptyView() {
        emptyView.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private val recyclerScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) {
                fab.hide(fabShowHideListener)
            } else {
                fab.show(fabShowHideListener)
            }
        }
    }

    private val fabShowHideListener = object : FloatingActionButton.OnVisibilityChangedListener() {
        override fun onShown(fab: FloatingActionButton) {
            fab.visibility = View.VISIBLE
        }

        override fun onHidden(fab: FloatingActionButton) {
            fab.visibility = View.GONE
        }
    }
}