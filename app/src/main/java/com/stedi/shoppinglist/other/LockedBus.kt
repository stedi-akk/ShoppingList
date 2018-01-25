package com.stedi.shoppinglist.other

import com.squareup.otto.Bus
import java.util.*

class LockedBus : Bus() {
    private val pendingEvents = LinkedList<Runnable>()

    private var locked = false

    /**
     * New events will not be delivered until [unlock]
     */
    fun lock() {
        locked = true
    }

    /**
     * Normal events work, but if there are any of locked events, they will be delivered immediately
     */
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