package com.spbstu.journeypln.presenters.fragmentPresenters

import android.content.Context
import android.widget.CheckBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.spbstu.journeypln.adapters.TodoRecyclerAdapter
import com.spbstu.journeypln.data.firebase.pojo.TodoTask
import com.spbstu.journeypln.views.ToDoListView
import moxy.MvpPresenter

class ToDoListPresenter: MvpPresenter<ToDoListView>() {

    private val database = Firebase.database
    private val signInAccount = FirebaseAuth.getInstance()
    private lateinit var context: Context

    private lateinit var mTasksAdapter: TodoRecyclerAdapter

    private lateinit var tripId: String
    private var tasksList: ArrayList<TodoTask> = arrayListOf()

    fun setTripId(tripId: String) {
        this.tripId = tripId
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun loadAllTasks() {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$tripId/tasks")

        databaseReference.orderByChild("time").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newList = arrayListOf<TodoTask>()
                        tasksList.clear()
                        if (snapshot.exists()) {
                            val uploadList = snapshot.children.sortedByDescending { it.child("time")
                                    .getValue(Long::class.java) }.toList()

                            if (uploadList.isNotEmpty()) {
                                for (element in uploadList) {
                                    val upload = element.getValue(TodoTask::class.java)
                                    if (upload != null) {
                                        upload.setKey(element.key.toString())
                                        newList.add(upload)
                                    }
                                }

                                tasksList = newList
                                mTasksAdapter.setData(tasksList)
                                mTasksAdapter.notifyDataSetChanged()
                            }
                        } else {
                            tasksList.clear()
                            mTasksAdapter.setData(tasksList)
                            mTasksAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        viewState.showToast(error.message)
                    }
                }
        )
    }

    fun setAdapter() {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$tripId/tasks")

        mTasksAdapter = TodoRecyclerAdapter(context = context, tasks = tasksList,
                onClickListener = { compoundButton, todoTask ->
                    val b = (compoundButton as CheckBox).isChecked
                    databaseReference.child(todoTask.getKey()).updateChildren(mapOf("isChecked" to b))
                })
        viewState.setUpAdapter(mTasksAdapter)
    }

    fun addNewTask(taskText: String) {
        if (taskText.isNotBlank()) {
            val databaseReference = database.getReference("users/${signInAccount.uid}/$tripId/tasks")
            val task = TodoTask(name = taskText, isChecked = false, time = System.currentTimeMillis())
            databaseReference.push().setValue(task)
            viewState.hideInputErrorAndClear()
        } else {
            viewState.showInputError()
        }
    }

    fun deleteElement(position: Int) {
        val databaseReference = database.getReference("users/${signInAccount.uid}/$tripId/tasks")
        val key = tasksList[position].getKey()
        databaseReference.child(key).removeValue()
    }
}