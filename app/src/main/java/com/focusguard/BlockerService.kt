package com.focusguard

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent

class BlockerService : AccessibilityService() {
    private var last = 0L

    override fun onAccessibilityEvent(e: AccessibilityEvent?) {
        if (e?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = e.packageName?.toString() ?: return
        if (pkg == packageName) return
        if (pkg !in Store.apps(this)) return
        val task = Store.activeTask(this) ?: return

        val now = SystemClock.elapsedRealtime()
        if (now - last < 800) return
        last = now

        startActivity(
            Intent(this, BlockActivity::class.java)
                .putExtra("task", task.name)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    override fun onInterrupt() {}
}
