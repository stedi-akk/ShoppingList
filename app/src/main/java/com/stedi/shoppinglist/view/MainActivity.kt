package com.stedi.shoppinglist.view

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.stedi.shoppinglist.R
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.other.dp2px
import com.stedi.shoppinglist.other.getAppComponent
import com.stedi.shoppinglist.other.showToast
import com.stedi.shoppinglist.presenter.MainPresenter
import javax.inject.Inject

class MainActivity : BaseActivity(), MainPresenter.UIImpl, ShoppingListsAdapter.ClickListener {
    private val KEY_PRESENTER_STATE = "KEY_PRESENTER_STATE"

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

        setContentView(R.layout.main_activity)
        ButterKnife.bind(this)

        fab.hide(fabShowHideListener)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(ListSpaceDecoration(dp2px(R.dimen.common_v_spacing).toInt(), dp2px(R.dimen.common_lr_spacing).toInt()))
        adapter = ShoppingListsAdapter(this)
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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_PRESENTER_STATE, presenter.retain())
    }

    override fun onLoaded(list: List<ShoppingList>) {
        fab.show(fabShowHideListener)
        adapter.set(list)
        refreshEmptyView()
    }

    override fun onFailedToLoad() {
        fab.show(fabShowHideListener)
        showToast(R.string.failed_to_load_lists)
        refreshEmptyView()
    }

    override fun onListClicked() {

    }

    @OnClick(R.id.main_activity_fab)
    fun onFabClick(v: View) {

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