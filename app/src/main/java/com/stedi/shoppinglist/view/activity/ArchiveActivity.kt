package com.stedi.shoppinglist.view.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.stedi.shoppinglist.R
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.other.dp2px
import com.stedi.shoppinglist.other.getAppComponent
import com.stedi.shoppinglist.other.showToast
import com.stedi.shoppinglist.presenter.interfaces.ArchivePresenter
import com.stedi.shoppinglist.view.ListSpaceDecoration
import com.stedi.shoppinglist.view.ShoppingListsAdapter
import javax.inject.Inject

class ArchiveActivity : BaseActivity(), ArchivePresenter.UIImpl, ShoppingListsAdapter.ClickListener {
    private val KEY_PRESENTER_STATE = "KEY_PRESENTER_STATE"

    @BindView(R.id.archive_activity_recycler_view)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.archive_activity_empty_view)
    lateinit var emptyView: View

    @Inject
    lateinit var presenter: ArchivePresenter

    private lateinit var adapter: ShoppingListsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAppComponent().inject(this)
        bus.register(this)

        setContentView(R.layout.archive_activity)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ListSpaceDecoration(dp2px(R.dimen.common_v_spacing).toInt(), dp2px(R.dimen.common_lr_spacing).toInt()))
        adapter = ShoppingListsAdapter(layoutInflater, this, false)
        recyclerView.adapter = adapter

        presenter.attach(this)
        savedInstanceState?.getSerializable(KEY_PRESENTER_STATE)?.apply {
            presenter.restore(this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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

    override fun onListClicked(list: ShoppingList) {
        EditListActivity.start(this, list)
    }

    override fun onLoaded(list: List<ShoppingList>) {
        adapter.set(list)
        refreshEmptyView()
    }

    override fun onFailedToLoad() {
        showToast(R.string.failed_to_load_lists)
        refreshEmptyView()
    }

    private fun refreshEmptyView() {
        emptyView.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onDeleteClicked(list: ShoppingList) {}
    override fun onBoughtClicked(list: ShoppingList) {}
}