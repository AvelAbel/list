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
import android.text.Html


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
        adapter = TaskAdapter(this, R.layout.task_item, tasks, this::incrementTask, this::removeTask, this::showEditTaskDialog, this::isDeleteMode)

        val listView: ListView = findViewById(R.id.listView)
        listView.adapter = adapter


        val buttonAddTask: ImageView = findViewById(R.id.buttonAddTask)
        val buttonRemoveTasks: ImageView = findViewById(R.id.buttonRemoveTasks)
        val buttonInfo: ImageView = findViewById(R.id.buttonInfo)

        buttonAddTask.visibility = View.GONE
        buttonRemoveTasks.visibility = View.GONE
        buttonInfo.visibility = View.GONE
        buttonRemoveTasks.tag = "normal_mode"

        buttonAddTask.setOnClickListener {
            showAddTaskDialog()
        }

        val buttonChevron: ImageView = findViewById(R.id.buttonChevron)
        buttonChevron.tag = "down"

        val slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down)

        buttonChevron.setOnClickListener {
            val slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
            val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)

            if (buttonChevron.tag == "down") {
                buttonChevron.setImageResource(R.drawable.chevronup)
                buttonChevron.tag = "up"

                buttonAddTask.visibility = View.VISIBLE
                buttonAddTask.startAnimation(slideDownAnimation)

                buttonRemoveTasks.visibility = View.VISIBLE
                buttonRemoveTasks.startAnimation(slideDownAnimation)

                buttonInfo.visibility = View.VISIBLE
                buttonInfo.startAnimation(slideDownAnimation)

                listView.startAnimation(slideDownAnimation)
            } else {
                buttonChevron.setImageResource(R.drawable.chevrondown)
                buttonChevron.tag = "down"

                buttonAddTask.visibility = View.GONE

                buttonRemoveTasks.visibility = View.GONE

                buttonInfo.visibility = View.GONE
            }
        }

        buttonInfo.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.info_message)
            builder.setNeutralButton(R.string.ok) { _, _ -> }
            builder.show()
        }




        buttonRemoveTasks.setOnClickListener {
            deleteMode = if (deleteMode) {
                buttonRemoveTasks.setImageResource(R.drawable.edit)
                buttonAddTask.visibility = View.VISIBLE
                buttonInfo.visibility = View.VISIBLE
                false
            } else {
                buttonRemoveTasks.setImageResource(R.drawable.check)
                buttonAddTask.visibility = View.GONE
                buttonInfo.visibility = View.GONE
                true
            }
            toggleRemoveButtons()
        }
    }

    override fun onStop() {
        super.onStop()
        saveTasks()
    }

    private fun showEditTaskDialog(task: Task) {
        val editTextTask = EditText(this)
        editTextTask.setText(task.name)
        AlertDialog.Builder(this)
            .setTitle("Редактировать задачу")
            .setView(editTextTask)
            .setPositiveButton("Сохранить") { _, _ ->
                val taskName = editTextTask.text.toString()
                if (taskName.isNotBlank()) {
                    task.name = taskName
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    private fun editTask(task: Task) {
        val editTextTask = EditText(this)
        editTextTask.setText(task.name)
        AlertDialog.Builder(this)
            .setTitle("Редактировать задачу")
            .setView(editTextTask)
            .setPositiveButton("Сохранить") { _, _ ->
                val newTaskName = editTextTask.text.toString()
                if (newTaskName.isNotBlank()) {
                    task.name = newTaskName
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
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
            val buttonEdit: ImageView  = view.findViewById(R.id.buttonEdit)
            buttonEdit.visibility = if (buttonEdit.visibility == View.VISIBLE) View.GONE else View.VISIBLE
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
