package com.focusguard

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class BlockActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        build()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        build()
    }

    private fun build() {
        val task = intent.getStringExtra("task") ?: ""
        val pad = (24 * resources.displayMetrics.density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#1F2937"))
            setPadding(pad, pad, pad, pad)
        }
        root.addView(TextView(this).apply {
            text = "Tâche en cours"
            setTextColor(Color.parseColor("#9CA3AF"))
            textSize = 16f
            gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = task
            setTextColor(Color.WHITE)
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(0, pad / 2, 0, pad)
        })
        root.addView(TextView(this).apply {
            text = Store.message(this@BlockActivity)
            setTextColor(Color.parseColor("#FDE68A"))
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, pad * 2)
        })
        root.addView(Button(this).apply {
            text = "Retour à l'écran d'accueil"
            setOnClickListener { goHome() }
        })
        setContentView(root)
    }

    private fun goHome() {
        startActivity(
            Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        goHome()
    }
}
