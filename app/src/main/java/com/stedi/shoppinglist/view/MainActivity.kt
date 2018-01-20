package com.stedi.shoppinglist.view

import android.os.Bundle
import com.stedi.shoppinglist.R
import com.stedi.shoppinglist.model.ShoppingList
import com.stedi.shoppinglist.other.getAppComponent
import com.stedi.shoppinglist.other.showToast
import com.stedi.shoppinglist.presenter.MainPresenter
import javax.inject.Inject

class MainActivity : BaseActivity(), MainPresenter.UIImpl {
    private val KEY_PRESENTER_STATE = "KEY_PRESENTER_STATE"

    @Inject
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAppComponent().inject(this)
        presenter.attach(this)
        savedInstanceState?.getSerializable(KEY_PRESENTER_STATE)?.apply {
            presenter.restore(this)
        }
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
    }

    override fun onFailedToLoad() {
        showToast(R.string.failed_to_load_lists)
    }
}