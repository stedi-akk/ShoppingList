package com.stedi.shoppinglist.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.squareup.otto.Subscribe
import com.stedi.shoppinglist.R
import com.stedi.shoppinglist.model.ShoppingItem
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.other.getAppComponent
import com.stedi.shoppinglist.other.showToast
import com.stedi.shoppinglist.presenter.EditListPresenter
import com.stedi.shoppinglist.view.dialogs.ConfirmDialog
import javax.inject.Inject

class EditListActivity : BaseActivity(), EditListPresenter.UIImpl {
    private val KEY_PRESENTER_STATE = "KEY_PRESENTER_STATE"
    private val KEY_PENDING_LIST = "KEY_PENDING_LIST"

    private val REQUEST_AS_ACHIEVED_LIST = 123

    @BindView(R.id.edit_list_activity_items_container)
    lateinit var itemsContainer: ViewGroup

    @Inject
    lateinit var presenter: EditListPresenter

    private lateinit var pendingList: ShoppingList

    companion object {
        private const val KEY_EXTRA_LIST = "KEY_EXTRA_LIST"

        fun start(context: Context, list: ShoppingList? = null) {
            val intent = Intent(context, EditListActivity::class.java)
            if (list != null) {
                intent.putExtra(KEY_EXTRA_LIST, list.copy())
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAppComponent().inject(this)
        bus.register(this)

        setContentView(R.layout.edit_list_activity)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.attach(this)

        savedInstanceState?.apply {
            getSerializable(KEY_PRESENTER_STATE)?.apply {
                presenter.restore(this)
            }
            getParcelable<ShoppingList>(KEY_PENDING_LIST)?.apply {
                pendingList = this
            }
        }

        if (!this::pendingList.isInitialized) {
            pendingList = intent.getParcelableExtra(KEY_EXTRA_LIST) ?: presenter.newList()
        }

        showTheList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        pendingList.items = getItemsFromView()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
        bus.unregister(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_PRESENTER_STATE, presenter.retain())
        outState.putParcelable(KEY_PENDING_LIST, pendingList)
    }

    @OnClick(R.id.edit_list_activity_items_container_add)
    fun onAddItemClick(v: View) {
        inflateNewContainerItem(layoutInflater)
    }

    @OnClick(R.id.edit_list_activity_btn_save)
    fun onSaveClick(v: View) {
        pendingList.items = getItemsFromView()
        presenter.save(pendingList)
    }

    override fun showSaveAsAchieved(list: ShoppingList) {
        ConfirmDialog.newInstance(REQUEST_AS_ACHIEVED_LIST, messageId = R.string.confirm_save_as_achieved, confirmId = R.string.yes, cancelId = R.string.no)
                .show(supportFragmentManager)
    }

    override fun onSaved() {
        finish()
    }

    override fun onFailedToSave() {
        showToast(R.string.failed_to_save_list)
    }

    override fun showErrorEmptyList() {
        showToast(R.string.please_add_items)
    }

    @Subscribe
    fun onConfirmDialogCallback(callback: ConfirmDialog.Callback) {
        if (callback.requestCode == REQUEST_AS_ACHIEVED_LIST) {
            if (callback.confirmed) {
                presenter.saveAsAchieved(pendingList)
            } else {
                presenter.save(pendingList, false)
            }
        }
    }

    private fun showTheList() {
        itemsContainer.removeAllViews()
        val inflater = layoutInflater
        for (item in pendingList.items) {
            val itemView = inflateNewContainerItem(inflater)
            itemView.findViewById<EditText>(R.id.shopping_item_et).apply {
                isSaveEnabled = false
                setText(item.name)
            }
            itemView.findViewById<CheckBox>(R.id.shopping_item_cb).apply {
                isSaveEnabled = false
                isChecked = item.achieved
            }
        }
    }

    private fun inflateNewContainerItem(inflater: LayoutInflater): View {
        val itemView = inflater.inflate(R.layout.shopping_item, itemsContainer, false)
        itemView.findViewById<View>(R.id.shopping_item_btn_delete).setOnClickListener {
            itemsContainer.removeView(itemView)
        }
        itemsContainer.post { itemsContainer.addView(itemView) }
        return itemView
    }

    private fun getItemsFromView(): List<ShoppingItem> {
        val items = ArrayList<ShoppingItem>()
        for (i in 0 until itemsContainer.childCount) {
            val itemView = itemsContainer.getChildAt(i)
            val itemName = itemView.findViewById<EditText>(R.id.shopping_item_et).text.toString().trim()
            if (itemName.isEmpty()) {
                continue
            }
            val itemAchieved = itemView.findViewById<CheckBox>(R.id.shopping_item_cb).isChecked
            items.add(ShoppingItem(name = itemName, achieved = itemAchieved))
        }
        return items
    }
}