package com.example.list2

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Task(var name: String, var count: Int = 0)

class MainActivity : AppCompatActivity() {

    private lateinit var tasks: MutableList<Task>
    private lateinit var adapter: ArrayAdapter<Task>

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tasks = loadTasks()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)

        val listView: ListView = findViewById(R.id.listView)
        listView.adapter = adapter

        val buttonAddTask: Button = findViewById(R.id.buttonAddTask)
        buttonAddTask.setOnClickListener {
            addTask()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            incrementTaskCount(position)
        }
    }

    override fun onStop() {
        super.onStop()
        saveTasks()
    }

    private fun addTask() {
        val editTextTask: EditText = findViewById(R.id.editTextTask)
        val taskName = editTextTask.text.toString()
        if (taskName.isNotBlank()) {
            tasks.add(0, Task(taskName))
            adapter.notifyDataSetChanged()
            editTextTask.text.clear()
        }
    }

    private fun incrementTaskCount(position: Int) {
        val task = tasks[position]
        task.count++
        tasks.removeAt(position)
        tasks.add(0, task)
        adapter.notifyDataSetChanged()
    }

    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("tasks", gson.toJson(tasks))
        editor.apply()
    }

    private fun loadTasks(): MutableList<Task> {
        val sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("tasks", "")
        val type = object : TypeToken<MutableList<Task>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }
}

