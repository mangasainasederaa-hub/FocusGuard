package com.focusguard

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class TasksActivity : Activity() {

    private lateinit var list: LinearLayout
    private var start = 8 * 60
    private var end = 9 * 60
    private lateinit var startBtn: Button
    private lateinit var endBtn: Button
    private lateinit var nameInput: EditText
    private var pad = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pad = (16 * resources.displayMetrics.density).toInt()

        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }
        col.addView(TextView(this).apply { text = "Mes tâches"; textSize = 24f })

        nameInput = EditText(this).apply {
            hint = "Nom de la tâche"
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        col.addView(nameInput)

        startBtn = Button(this).apply { setOnClickListener { pick(true) } }
        endBtn = Button(this).apply { setOnClickListener { pick(false) } }
        col.addView(startBtn)
        col.addView(endBtn)
        refreshButtons()

        col.addView(Button(this).apply {
            text = "Ajouter la tâche"
            setOnClickListener { add() }
        })

        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(list)

        setContentView(ScrollView(this).apply { addView(col) })
        refreshList()
    }

    private fun refreshButtons() {
        startBtn.text = "Début : ${Store.fmt(start)}"
        endBtn.text = "Fin : ${Store.fmt(end)}"
    }

    private fun pick(isStart: Boolean) {
        val cur = if (isStart) start else end
        TimePickerDialog(this, { _, h, m ->
            if (isStart) start = h * 60 + m else end = h * 60 + m
            refreshButtons()
        }, cur / 60, cur % 60, true).show()
    }

    private fun add() {
        val name = nameInput.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Écris le nom de la tâche", Toast.LENGTH_SHORT).show()
            return
        }
        if (start == end) {
            Toast.makeText(this, "Début et fin doivent être différents", Toast.LENGTH_SHORT).show()
            return
        }
        val tasks = Store.tasks(this)
        tasks.add(Task(name, start, end))
        Store.saveTasks(this, tasks)
        nameInput.setText("")
        refreshList()
    }

    private fun refreshList() {
        list.removeAllViews()
        val tasks = Store.tasks(this)
        tasks.forEachIndexed { i, t ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, pad / 2, 0, pad / 2)
            }
            row.addView(TextView(this).apply {
                text = "${t.name}\n${Store.fmt(t.start)} → ${Store.fmt(t.end)}"
                textSize = 16f
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            })
            row.addView(Button(this).apply {
                text = "Supprimer"
                setOnClickListener {
                    val l = Store.tasks(this@TasksActivity)
                    if (i < l.size) l.removeAt(i)
                    Store.saveTasks(this@TasksActivity, l)
                    refreshList()
                }
            })
            list.addView(row)
        }
    }
}
