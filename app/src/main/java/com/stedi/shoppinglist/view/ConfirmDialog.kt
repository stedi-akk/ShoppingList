package com.stedi.shoppinglist.view

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.squareup.otto.Bus
import com.stedi.shoppinglist.R
import com.stedi.shoppinglist.other.getAppComponent
import javax.inject.Inject

class ConfirmDialog : BaseDialog() {
    @Inject
    lateinit var bus: Bus

    private var requestCode: Int = 0
    private var posted: Boolean = false

    class Callback(val requestCode: Int, val confirmed: Boolean, val bundle: Bundle?)

    companion object {
        private val KEY_REQUEST_CODE = "KEY_REQUEST_CODE"
        private val KEY_TITLE = "KEY_TITLE"
        private val KEY_MESSAGE = "KEY_MESSAGE"
        private val KEY_BUNDLE = "KEY_BUNDLE"

        fun newInstance(requestCode: Int, title: String, message: String, bundle: Bundle? = null): ConfirmDialog {
            val args = Bundle()
            args.putInt(KEY_REQUEST_CODE, requestCode)
            args.putString(KEY_TITLE, title)
            args.putString(KEY_MESSAGE, message)
            if (bundle != null) {
                args.putBundle(KEY_BUNDLE, bundle)
            }
            val dlg = ConfirmDialog()
            dlg.arguments = args
            return dlg
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context.getAppComponent().inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        requestCode = arguments.getInt(KEY_REQUEST_CODE)

        val builder = AlertDialog.Builder(context)

        arguments.getString(KEY_TITLE, null)?.apply {
            builder.setTitle(this)
        }
        arguments.getString(KEY_MESSAGE, null)?.apply {
            builder.setMessage(this)
        }

        builder.setPositiveButton(R.string.ok) { _, _ -> postConfirmed(true) }
        builder.setNegativeButton(R.string.cancel, null)
        return builder.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        postConfirmed(false)
    }

    private fun postConfirmed(value: Boolean) {
        if (posted) {
            return
        }
        posted = true

        bus.post(Callback(requestCode, value, arguments.getBundle(KEY_BUNDLE)))
    }
}