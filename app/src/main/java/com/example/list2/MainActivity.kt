package com.example.list2

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.view.View
import android.widget.ImageView
import android.view.animation.AnimationUtils


data class Task(var name: String, var count: Int = 0)

class MainActivity : AppCompatActivity() {

    private lateinit var tasks: MutableList<Task>
    private lateinit var adapter: ArrayAdapter<Task>

    private val gson = Gson()

    var deleteMode = false

    fun isDeleteMode(): Boolean {
        return deleteMode
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tasks = loadTasks()
        adapter = TaskAdapter(this, R.layout.task_item, tasks, this::incrementTask, this::removeTask, this::isDeleteMode)

        val listView: ListView = findViewById(R.id.listView)
        listView.adapter = adapter

        val buttonAddTask: ImageView = findViewById(R.id.buttonAddTask)
        val buttonRemoveTasks: ImageView = findViewById(R.id.buttonRemoveTasks)
        buttonAddTask.visibility = View.GONE
        buttonRemoveTasks.visibility = View.GONE
        buttonRemoveTasks.tag = "normal_mode"

        buttonAddTask.setOnClickListener {
            showAddTaskDialog()
        }

        val buttonChevron: ImageView = findViewById(R.id.buttonChevron)
        buttonChevron.tag = "down"

        val slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down)

        buttonChevron.setOnClickListener {
            if (buttonChevron.tag == "down") {
                buttonChevron.setImageResource(R.drawable.chevronup)
                buttonChevron.tag = "up"

                buttonAddTask.visibility = View.VISIBLE
                buttonAddTask.startAnimation(slideDownAnimation)

                buttonRemoveTasks.visibility = View.VISIBLE
                buttonRemoveTasks.startAnimation(slideDownAnimation)
            } else {
                buttonChevron.setImageResource(R.drawable.chevrondown)
                buttonChevron.tag = "down"
                buttonAddTask.visibility = View.GONE
                buttonRemoveTasks.visibility = View.GONE
            }
        }




        buttonRemoveTasks.setOnClickListener {
            deleteMode = if (deleteMode) {
                buttonRemoveTasks.setImageResource(R.drawable.trash)
                buttonAddTask.visibility = View.VISIBLE
                false
            } else {
                buttonRemoveTasks.setImageResource(R.drawable.check)
                buttonAddTask.visibility = View.GONE
                true
            }
            toggleRemoveButtons()
        }
    }

    override fun onStop() {
        super.onStop()
        saveTasks()
    }

    private fun incrementTask(task: Task) {
        task.count++
        tasks.remove(task)
        tasks.add(0, task)
        adapter.notifyDataSetChanged()
    }

    private fun showAddTaskDialog() {
        val editTextTask = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Добавить задачу")
            .setView(editTextTask)
            .setPositiveButton("Добавить") { _, _ ->
                val taskName = editTextTask.text.toString()
                if (taskName.isNotBlank()) {
                    tasks.add(0, Task(taskName))
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun removeTask(task: Task) {
        tasks.remove(task)
        adapter.notifyDataSetChanged()
    }

    private fun toggleRemoveButtons() {
        val listView: ListView = findViewById(R.id.listView)
        for (i in 0 until listView.childCount) {
            val view = listView.getChildAt(i)
            val buttonRemove: ImageView  = view.findViewById(R.id.buttonRemove)
            buttonRemove.visibility = if (buttonRemove.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
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
