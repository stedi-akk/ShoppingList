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
import com.stedi.shoppinglist.presenter.interfaces.EditListPresenter
import com.stedi.shoppinglist.view.dialogs.ConfirmDialog
import javax.inject.Inject

class EditListActivity : BaseActivity(), EditListPresenter.UIImpl {
    private val KEY_PRESENTER_STATE = "KEY_PRESENTER_STATE"
    private val KEY_PENDING_LIST = "KEY_PENDING_LIST"

    private val REQUEST_AS_ACHIEVED_LIST = 123

    @BindView(R.id.edit_list_activity_items_container)
    lateinit var itemsContainer: ViewGroup

    @BindView(R.id.edit_list_activity_btn_save)
    lateinit var btnSave: View

    @BindView(R.id.edit_list_activity_items_container_btn_add_more)
    lateinit var btnAddMore: View

    @Inject
    lateinit var presenter: EditListPresenter

    private val inflater: LayoutInflater by lazy {
        layoutInflater
    }

    private lateinit var pendingList: ShoppingList

    private var editingDisabled = false

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

        val extraList: ShoppingList? = intent.getParcelableExtra(KEY_EXTRA_LIST)
        setTitle(if (extraList == null) R.string.new_list else R.string.edit_list)

        presenter.attach(this)

        savedInstanceState?.apply {
            getSerializable(KEY_PRESENTER_STATE)?.apply {
                presenter.restore(this, createdAfterProcessKill)
            }
            getParcelable<ShoppingList>(KEY_PENDING_LIST)?.apply {
                pendingList = this
            }
        }

        if (!this::pendingList.isInitialized) {
            pendingList = presenter.prepare(extraList)
        } else {
            presenter.prepare(pendingList)
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
        pendingList.items = getItemsFromView(true)
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

    @OnClick(R.id.edit_list_activity_items_container_btn_add_more)
    fun onAddMoreClick(v: View) {
        val item = inflateNewContainerItem()
        item.findViewById<EditText>(R.id.shopping_item_et).requestFocus()
    }

    @OnClick(R.id.edit_list_activity_btn_save)
    fun onSaveClick(v: View) {
        pendingList.items = getItemsFromView(false)
        presenter.save(pendingList)
    }

    override fun disableListEditing() {
        setTitle(R.string.view_list)
        btnAddMore.visibility = View.GONE
        btnSave.visibility = View.GONE
        editingDisabled = true
    }

    override fun showSaveAsAchieved(list: ShoppingList) {
        ConfirmDialog.newInstance(
                REQUEST_AS_ACHIEVED_LIST,
                messageId = R.string.confirm_save_as_achieved,
                confirmId = R.string.yes,
                cancelId = R.string.no).show(supportFragmentManager)
    }

    override fun onSaved(list: ShoppingList) {
        finish()
    }

    override fun onFailedToSave(list: ShoppingList) {
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
        var focusRequested = false
        for (item in pendingList.items) {
            val itemView = inflateNewContainerItem()
            itemView.findViewById<EditText>(R.id.shopping_item_et).apply {
                isSaveEnabled = false
                setText(item.name)
                isFocusable = !editingDisabled
                if (!focusRequested && isFocusable) {
                    requestFocus()
                    focusRequested = true
                }
            }
            itemView.findViewById<CheckBox>(R.id.shopping_item_cb).apply {
                isSaveEnabled = false
                isChecked = item.achieved
                isClickable = !editingDisabled
            }
        }
    }

    private fun inflateNewContainerItem(): View {
        val itemView = inflater.inflate(R.layout.shopping_item, itemsContainer, false)
        itemView.findViewById<View>(R.id.shopping_item_btn_delete).apply {
            if (editingDisabled) {
                visibility = View.INVISIBLE
            } else {
                setOnClickListener { itemsContainer.removeView(itemView) }
            }
        }
        itemsContainer.post { itemsContainer.addView(itemView) }
        return itemView
    }

    private fun getItemsFromView(includeEmpty: Boolean): List<ShoppingItem> {
        val items = ArrayList<ShoppingItem>()
        for (i in 0 until itemsContainer.childCount) {
            val itemView = itemsContainer.getChildAt(i)
            val itemName = itemView.findViewById<EditText>(R.id.shopping_item_et).text.toString().trim()
            if (!includeEmpty && itemName.isEmpty()) {
                continue
            }
            val itemAchieved = itemView.findViewById<CheckBox>(R.id.shopping_item_cb).isChecked
            items.add(ShoppingItem(name = itemName, achieved = itemAchieved))
        }
        return items
    }
}