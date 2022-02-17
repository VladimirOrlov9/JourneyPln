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
import com.spbstu.journeypln.data.room.databases.TripsDb
import com.spbstu.journeypln.data.room.entities.TodoTask
//import com.spbstu.journeypln.data.firebase.pojo.TodoTask
import com.spbstu.journeypln.views.ToDoListView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import kotlin.properties.Delegates

class ToDoListPresenter: MvpPresenter<ToDoListView>() {

//    private val database = Firebase.database
//    private val signInAccount = FirebaseAuth.getInstance()
    private lateinit var context: Context

    private lateinit var mTasksAdapter: TodoRecyclerAdapter

    private var tripId: Long = 0
    private var tasksList: List<TodoTask> = listOf()
    private lateinit var db: TripsDb

    fun setTripId(tripId: Long) {
        this.tripId = tripId
    }

    fun setContext(context: Context, db: TripsDb) {
        this.context = context
        this.db = db
    }

    fun loadAllTasks() {

        CoroutineScope(Dispatchers.IO).launch {
            val todoDao = db.todoDao()
            val tasks = todoDao.getAllTasks(tripId)

            tasksList = if (tasks.isNotEmpty()) tasks else listOf()
            mTasksAdapter.setData(tasksList)

            launch(Dispatchers.Main) {
                mTasksAdapter.notifyDataSetChanged()
            }
        }
    }

    fun setAdapter() {

        mTasksAdapter = TodoRecyclerAdapter(tasks = tasksList,
                onClickListener = { compoundButton, todoTask ->
                    val b = (compoundButton as CheckBox).isChecked
                    CoroutineScope(Dispatchers.IO).launch {
                        val todoDao = db.todoDao()
                        todoDao.updateCheckStatus(todoTask.uid, b)
                    }
                })
        viewState.setUpAdapter(mTasksAdapter)
    }

    fun addNewTask(taskText: String) {

        if (taskText.isNotBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                val todoDao = db.todoDao()
                todoDao.insertTask(
                    TodoTask(
                        name = taskText,
                        isChecked = false,
                        time = System.currentTimeMillis(),
                        tripId = tripId
                    )
                )

                launch(Dispatchers.Main) {
                    viewState.hideInputErrorAndClear()
                }
            }
        }
        else {
            viewState.showInputError()
        }
    }

    fun deleteElement(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val todoDao = db.todoDao()
            todoDao.deleteTask(tasksList[position].uid)

            launch(Dispatchers.Main) {
                viewState.hideInputErrorAndClear()
            }
        }
    }
}