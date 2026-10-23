package com.focusguard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (16 * resources.displayMetrics.density).toInt()

        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }
        col.addView(TextView(this).apply {
            text = "FocusGuard"
            textSize = 26f
        })

        status = TextView(this).apply { setPadding(0, pad, 0, pad / 2) }
        col.addView(status)
        col.addView(Button(this).apply {
            text = "Activer le blocage (Accessibilité)"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        })

        col.addView(Button(this).apply {
            text = "Mes tâches et horaires"
            setOnClickListener { startActivity(Intent(this@MainActivity, TasksActivity::class.java)) }
        })
        col.addView(Button(this).apply {
            text = "Mes applications à bloquer"
            setOnClickListener { startActivity(Intent(this@MainActivity, AppsActivity::class.java)) }
        })

        col.addView(TextView(this).apply {
            text = "Message affiché sur l'écran de blocage"
            setPadding(0, pad, 0, 0)
        })
        val msg = EditText(this).apply {
            setText(Store.message(this@MainActivity))
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        col.addView(msg)
        col.addView(Button(this).apply {
            text = "Enregistrer le message"
            setOnClickListener {
                Store.saveMessage(this@MainActivity, msg.text.toString())
                Toast.makeText(this@MainActivity, "Message enregistré", Toast.LENGTH_SHORT).show()
            }
        })

        setContentView(ScrollView(this).apply { addView(col) })
    }

    override fun onResume() {
        super.onResume()
        status.text = if (serviceEnabled())
            "✅ Blocage activé"
        else
            "⚠️ Blocage désactivé : active FocusGuard dans Accessibilité > Applications installées."
    }

    private fun serviceEnabled(): Boolean {
        val enabled = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.contains("$packageName/")
    }
}
