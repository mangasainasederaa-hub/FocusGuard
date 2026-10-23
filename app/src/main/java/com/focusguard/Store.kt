package com.focusguard

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

data class Task(val name: String, val start: Int, val end: Int)

object Store {
    private fun p(c: Context) = c.getSharedPreferences("focus", Context.MODE_PRIVATE)

    fun tasks(c: Context): MutableList<Task> {
        val out = mutableListOf<Task>()
        val arr = JSONArray(p(c).getString("tasks", "[]"))
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(Task(o.getString("n"), o.getInt("s"), o.getInt("e")))
        }
        return out
    }

    fun saveTasks(c: Context, list: List<Task>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject().put("n", it.name).put("s", it.start).put("e", it.end))
        }
        p(c).edit().putString("tasks", arr.toString()).apply()
    }

    fun apps(c: Context): Set<String> =
        p(c).getStringSet("apps", emptySet()) ?: emptySet()

    fun saveApps(c: Context, s: Set<String>) {
        p(c).edit().putStringSet("apps", HashSet(s)).apply()
    }

    fun message(c: Context): String =
        p(c).getString("msg", "Fais d'abord ta tâche. Tu peux le faire !") ?: ""

    fun saveMessage(c: Context, m: String) {
        p(c).edit().putString("msg", m).apply()
    }

    fun fmt(minutes: Int): String = "%02d:%02d".format(minutes / 60, minutes % 60)

    /** Tâche en cours à l'instant présent (gère aussi les horaires qui passent minuit). */
    fun activeTask(c: Context): Task? {
        val cal = Calendar.getInstance()
        val m = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        return tasks(c).firstOrNull {
            if (it.start <= it.end) m >= it.start && m < it.end
            else m >= it.start || m < it.end
        }
    }
}
