package com.spbstu.journeypln.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.spbstu.journeypln.R
import com.spbstu.journeypln.data.room.entities.TodoTask
//import com.spbstu.journeypln.data.firebase.pojo.TodoTask
import kotlin.collections.ArrayList

class TodoRecyclerAdapter(
    tasks: List<TodoTask>,
    private val onClickListener: (View, TodoTask) -> Unit
): RecyclerView.Adapter<TodoRecyclerAdapter.MyViewHolder>() {

    private var mTasks: MutableList<TodoTask> = tasks.toMutableList()


    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.todo_checkbox)

    }

    fun setData(newTasksList: List<TodoTask>) {
        mTasks = newTasksList.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoRecyclerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_todo, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoRecyclerAdapter.MyViewHolder, position: Int) {
        val currentTask = mTasks[position]

        holder.checkBox.text = currentTask.name
        holder.checkBox.isChecked = currentTask.isChecked

        holder.checkBox.setOnClickListener { view ->
            onClickListener(view, currentTask)
        }
    }

    override fun getItemCount(): Int {
        return mTasks.size
    }

    fun removeItem(position: Int) {
        mTasks.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(task: TodoTask, position: Int) {
        mTasks.add(position, task)
        notifyItemInserted(position)
    }

    fun getData(): List<TodoTask> = mTasks

}