package com.stedi.shoppinglist.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.otto.Bus
import com.stedi.shoppinglist.other.LockedBus
import com.stedi.shoppinglist.other.getAppComponent
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity() {
    private companion object {
        private val createdActivities = HashSet<String>()
    }

    @Inject
    lateinit var bus: Bus

    protected var createdAfterProcessKill = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val activityClassName = javaClass.simpleName
        if (!createdActivities.contains(activityClassName)) {
            createdActivities.add(activityClassName)
            createdAfterProcessKill = savedInstanceState != null
        }

        super.onCreate(savedInstanceState)

        getAppComponent().inject(this)
    }

    override fun onResume() {
        super.onResume()
        if (bus is LockedBus) {
            (bus as LockedBus).unlock()
        }
    }

    override fun onPause() {
        super.onPause()
        if (bus is LockedBus) {
            (bus as LockedBus).lock()
        }
    }
}