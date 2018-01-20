package com.stedi.shoppinglist.other

import com.squareup.otto.Bus
import java.util.*

class LockedBus : Bus() {
    private val pendingEvents = LinkedList<Runnable>()

    private var locked = false

    fun lock() {
        locked = true
    }

    fun unlock() {
        locked = false
        releasePending()
    }

    override fun post(event: Any) {
        if (!locked) {
            super.post(event)
        } else {
            pendingEvents.add(Runnable { post(event) })
        }
    }

    private fun releasePending() {
        if (!pendingEvents.isEmpty()) {
            val release = LinkedList(pendingEvents)
            pendingEvents.clear()
            while (!release.isEmpty()) {
                release.pollFirst().run()
            }
        }
    }
}