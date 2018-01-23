package com.stedi.shoppinglist.view

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.StringRes
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
        private const val KEY_REQUEST_CODE = "KEY_REQUEST_CODE"
        private const val KEY_TITLE_ID = "KEY_TITLE_ID"
        private const val KEY_MESSAGE_ID = "KEY_MESSAGE_ID"
        private const val KEY_CONFIRM_ID = "KEY_CONFIRM_ID"
        private const val KEY_CANCEL_ID = "KEY_CANCEL_ID"
        private const val KEY_BUNDLE = "KEY_BUNDLE"

        fun newInstance(requestCode: Int,
                        @StringRes titleId: Int = R.string.confirm, @StringRes messageId: Int,
                        @StringRes confirmId: Int = R.string.ok, @StringRes cancelId: Int = R.string.cancel,
                        bundle: Bundle? = null): ConfirmDialog {

            val args = Bundle()
            args.putInt(KEY_REQUEST_CODE, requestCode)
            args.putInt(KEY_TITLE_ID, titleId)
            args.putInt(KEY_MESSAGE_ID, messageId)
            args.putInt(KEY_CONFIRM_ID, confirmId)
            args.putInt(KEY_CANCEL_ID, cancelId)
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
        builder.setTitle(arguments.getInt(KEY_TITLE_ID))
        builder.setMessage(arguments.getInt(KEY_MESSAGE_ID))

        builder.setPositiveButton(arguments.getInt(KEY_CONFIRM_ID)) { _, _ -> postConfirmed(true) }
        builder.setNegativeButton(arguments.getInt(KEY_CANCEL_ID), null)
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