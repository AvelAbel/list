package com.example.list2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.annotation.SuppressLint
import android.widget.ImageView


class TaskAdapter(
    context: Context,
    private val resource: Int,
    private val tasks: MutableList<Task>,
    private val incrementCallback: (Task) -> Unit,
    private val removeCallback: (Task) -> Unit,
    private val isDeleteMode: () -> Boolean
) : ArrayAdapter<Task>(context, resource, tasks) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(resource, null)

        val task = tasks[position]
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewCount: TextView = view.findViewById(R.id.textViewCount)
        val buttonRemove: ImageView = view.findViewById(R.id.buttonRemove)

        textViewName.text = task.name
        textViewCount.text = task.count.toString()

        // Привязка обработчиков
        view.setOnClickListener { incrementCallback(task) }
        buttonRemove.setOnClickListener { removeCallback(task) }

        // Установка видимости buttonRemove
        buttonRemove.visibility = if (isDeleteMode()) View.VISIBLE else View.GONE

        return view
    }
}
