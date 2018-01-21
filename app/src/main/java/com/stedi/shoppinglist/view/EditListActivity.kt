package com.stedi.shoppinglist.view

import com.stedi.shoppinglist.presenter.EditListPresenter

class EditListActivity : BaseActivity(), EditListPresenter.UIImpl {
    override fun onSaved() {
    }

    override fun onFailedToSave() {
    }
}