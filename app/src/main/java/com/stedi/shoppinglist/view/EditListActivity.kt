package com.stedi.shoppinglist.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.stedi.shoppinglist.R
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.other.getAppComponent
import com.stedi.shoppinglist.other.showToast
import com.stedi.shoppinglist.presenter.EditListPresenter
import javax.inject.Inject

class EditListActivity : BaseActivity(), EditListPresenter.UIImpl {
    private val KEY_PRESENTER_STATE = "KEY_PRESENTER_STATE"
    private val KEY_PENDING_LIST = "KEY_PENDING_LIST"

    @BindView(R.id.edit_list_activity_et)
    lateinit var editText: EditText

    @Inject
    lateinit var presenter: EditListPresenter

    private lateinit var pendingList: ShoppingList

    companion object {
        private val KEY_EXTRA_LIST = "KEY_EXTRA_LIST"

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

        setContentView(R.layout.edit_list_activity)
        ButterKnife.bind(this)

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

        showPendingList()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_PRESENTER_STATE, presenter.retain())
        outState.putParcelable(KEY_PENDING_LIST, pendingList)
    }

    @OnClick(R.id.edit_list_activity_btn_save)
    fun onSaveClick(v: View) {
        presenter.save(pendingList)
    }

    override fun onSaved() {
        finish()
    }

    override fun onFailedToSave() {
        showToast(R.string.failed_to_save_list)
    }

    private fun showPendingList() {
        val sb = StringBuilder()
        for (item in pendingList.items) {
            sb.append(item.name).append("\n")
        }
        editText.setText(sb.toString())
    }
}